(ns maqueta.scene
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io])
  (:use (maqueta application assets))
  (:import java.lang.Math
           com.jme3.light.DirectionalLight
           (com.jme3.scene Node Geometry)
           com.jme3.font.BitmapText
           com.jme3.renderer.queue.RenderQueue$Bucket
           (com.jme3.scene.shape Box Cylinder Sphere)
           com.jme3.bullet.BulletAppState
           com.jme3.bullet.util.CollisionShapeFactory
           com.jme3.bullet.control.RigidBodyControl
           com.jme3.app.state.ScreenshotAppState
           com.jme3.scene.Spatial$CullHint
           (com.jme3.scene.control BillboardControl
                                   BillboardControl$Alignment)
           (com.jme3.math ColorRGBA Vector3f Quaternion)))

(def colors {"blue" ColorRGBA/Blue
             "green" ColorRGBA/Green
             "orange" ColorRGBA/Orange
             "pink" ColorRGBA/Pink
             "purple" (ColorRGBA. 0.5 0 0.5 1)
             "red" ColorRGBA/Red
             "yellow" ColorRGBA/Yellow})

; initialize directional light
(def light (doto (DirectionalLight.)
             (.setDirection (Vector3f. 0.5 -0.7 0.3))))

(def bullet-app-state (BulletAppState.))

(def screenshot-app-state (ScreenshotAppState. ""))

(defn take-screenshot
  []
  (.takeScreenshot screenshot-app-state))

; initialize table
(def table (doto (load-model "Models/Table2/table.j3o")
             (.scale 10 10 10)))

; the table has no mass
(def table-phy (RigidBodyControl.
                (CollisionShapeFactory/createMeshShape
                 (cast Node table))
                0))

(def table-loc (.getCenter (.getWorldBound table)))
(def table-min-x (- (.getX table-loc) (.getXExtent (.getWorldBound table))))
(def table-max-x (+ (.getX table-loc) (.getXExtent (.getWorldBound table))))
(def table-min-y (- (.getY table-loc) (.getYExtent (.getWorldBound table))))
(def table-max-y (+ (.getY table-loc) (.getYExtent (.getWorldBound table))))
(def table-min-z (- (.getZ table-loc) (.getZExtent (.getWorldBound table))))
(def table-max-z (+ (.getZ table-loc) (.getZExtent (.getWorldBound table))))

(def table-spec {:loc [(.getX table-loc)
                       (.getY table-loc)
                       (.getZ table-loc)]
                 :aabb {:min [table-min-x
                              table-min-y
                              table-min-z]
                        :max [table-max-x
                              table-max-y
                              table-max-z]}})
(def objects (atom ()))

(defn obj-spec
  [obj]
  (let [mesh-cls (class (.getMesh obj))
        c (.getValue (.getParam (.getMaterial obj) "Ambient"))
        bb (.getWorldBound obj)
        loc (.getCenter bb)
        x (.getX loc)
        y (.getY loc)
        z (.getZ loc)]
    {:name (.getName obj)
     :type (subs (str mesh-cls)
                 (+ 1 (.lastIndexOf (str mesh-cls) ".")))
     :color [(.getRed c) (.getGreen c) (.getBlue c) (.getAlpha c)]
     :color-name (first (first (filter #(= c (second %)) (seq colors))))
     :loc [x y z]
     :aabb {:min [(- x (.getXExtent bb))
                  (- y (.getYExtent bb))
                  (- z (.getZExtent bb))]
            :max [(+ x (.getXExtent bb))
                  (+ y (.getYExtent bb))
                  (+ z (.getZExtent bb))]}}))

(defn objects-spec
  []
  (map obj-spec @objects))

(defn cam-spec
  [cam]
  (let [loc (.getLocation cam)
        dir (.getDirection cam)
        rot (.getRotation cam)]
    {:loc [(.getX loc) (.getY loc) (.getZ loc)]
     :dir [(.getX dir) (.getY dir) (.getZ dir)]
     :rot [(.getX rot) (.getY rot) (.getZ rot) (.getW rot)]}))

(defn get-spec
  [app]
  {:cam (cam-spec (.getCamera app))
   :table table-spec
   :objects (objects-spec)})

(defn save-scene
  [app]
  (with-open [w (io/writer "spec.json")]
    (binding [*out* w]
      (json/pprint (get-spec app))))
  (take-screenshot))

(defn rand-in-range
  [min max]
  (+ min (* (rand) (- max min))))

(defn billboard
  [app text obj]
  (let [obj-pos (.getCenter (.getWorldBound obj))
        new-pos (Vector3f. (.getX obj-pos)
                           (+ 1.5 (.getY obj-pos))
                           (.getZ obj-pos))
        font (load-font "Interface/Fonts/Default.fnt")
        bm-text (BitmapText. font false)
        bb-control (BillboardControl.)
        txt-node (Node. "billboard text")]
    (doto bm-text
      (.setSize 0.75) ;(.getRenderedSize (.getCharSet font)))
      (.setText text)
      (.setQueueBucket RenderQueue$Bucket/Transparent)
      (.setColor ColorRGBA/Cyan))
    (.setAlignment bb-control BillboardControl$Alignment/Camera)
    (doto txt-node
      (.setLocalTranslation new-pos)
      (.setCullHint Spatial$CullHint/Never)
      (.attachChild bm-text)
      (.addControl bb-control))
    (.attachChild (.getRootNode app) txt-node)))

(defn show-billboards
  [app]
  (doseq [obj @objects]
    (billboard app (.getName obj) obj)))

(defn add-obj
  [app obj x y z color mass]
  (let [geo (Geometry. (str (inc (count @objects))) obj)
        phy (RigidBodyControl. (float mass))
        mat (load-material "Common/MatDefs/Light/Lighting.j3md")]
    (doto mat
      (.setBoolean "UseMaterialColors" true)
      (.setColor "Ambient" color)
      (.setColor "Diffuse" color)
      (.setColor "Specular" ColorRGBA/White)
      (.setFloat "Shininess" (float 12)))
    (doto geo
      (.setMaterial mat)
      (.setLocalTranslation x y z)
      ;; FIXME we need to rotate cylinders only
      ;; but we are rotating everything
      (.rotate (/ Math/PI 2) 0 0)
      (.addControl phy))
    (reset! objects (cons geo @objects))
    (-> bullet-app-state
        .getPhysicsSpace
        (.add phy))
    (.attachChild (.getRootNode app) geo)))

(defn add-box
  [app & {:keys [width height depth x y z color mass]
          :or {width (rand-in-range 0.2 0.7)
               height (rand-in-range 0.2 0.7)
               depth (rand-in-range 0.2 0.7)
               x (rand-in-range (+ table-min-x 1.5) (- table-max-x 1.5))
               y 10
               z (rand-in-range (+ table-min-z 1.5) (- table-max-z 1.5))
               color (rand-nth (vals colors))
               mass 2}}]
  (let [box (Box. Vector3f/ZERO width height depth)]
    (add-obj app box x y z color mass)))

(defn add-sphere
  [app & {:keys [radius x y z color mass]
          :or {radius (rand-in-range 0.2 0.7)
               x (rand-in-range (+ table-min-x 1.5) (- table-max-x 1.5))
               y 10
               z (rand-in-range (+ table-min-z 1.5) (- table-max-z 1.5))
               color (rand-nth (vals colors))
               mass 2}}]
  (let [sphere (Sphere. 20 20 radius)]
    (add-obj app sphere x y z color mass)))

(defn add-cylinder
  [app & {:keys [radius height x y z color mass]
          :or {radius (rand-in-range 0.2 0.7)
               height (rand-in-range 0.2 0.7)
               x (rand-in-range (+ table-min-x 1.5) (- table-max-x 1.5))
               y 10
               z (rand-in-range (+ table-min-z 1.5) (- table-max-z 1.5))
               color (rand-nth (vals colors))
               mass 2}}]
  (let [cylinder (Cylinder. 20 20 radius height true)]
    (add-obj app cylinder x y z color mass)))

(defn add-objects
  [app n]
  (doseq [i (range n)]
    (let [adder (rand-nth [add-box add-cylinder add-sphere])]
      (apply adder [app])
      (Thread/sleep 500))))

(defn init
  [app]
  (let [root-node (.getRootNode app)
        viewport (.getViewPort app)
        cam (.getCamera app)
        state-manager (.getStateManager app)]
    ;; light gray background color
    (.setBackgroundColor viewport ColorRGBA/LightGray)
    ;; initialize camera position
    (doto cam
      (.setLocation (Vector3f. -8.011132 13.489606 -4.876288))
      (.setRotation (Quaternion. 0.15853512 0.6889404 -0.15845956 0.68928826)))
    ;; initialize physics and screenshots
    (doto state-manager
      (.attach bullet-app-state)
      (.attach screenshot-app-state))
    (.addControl table table-phy)    
    (-> bullet-app-state
        .getPhysicsSpace
        (.add table-phy))
    ;; attach objects
    (doto root-node
      (.addLight light)
      (.attachChild table))))

(defmacro act
  [& body]
  `(fn [~'app ~'value ~'tpf]
     (when-not ~'value
       ~@body)))

(defn start-app
  []
  (doto (make-app :init init
                  :display-fps false
                  :display-stat-view false
                  :on-action {:key-1 (act (add-box app))
                              :key-2 (act (add-cylinder app))
                              :key-3 (act (add-sphere app))
                              :key-b (act (show-billboards app))
                              :key-x (act (save-scene app))})
    .start))

(defn -main
  [& args]
  (start-app))
