;;;; jMonkeyEngine 3 Tutorial (8) - Hello Picking
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_picking

(ns maqueta.hello.picking
  (:use (maqueta application assets))
  (:import com.jme3.collision.CollisionResults
           com.jme3.font.BitmapText
           (com.jme3.math ColorRGBA Ray Vector3f)
           (com.jme3.scene Node Geometry)
           (com.jme3.scene.shape Box Sphere)))

(defn make-cube
  [name x y z]
  (let [box (Box. (Vector3f. x y z) 1 1 1)
        cube (Geometry. name box)
        mat (load-material "Common/MatDefs/Misc/Unshaded.j3md")]
    (.setColor mat "Color" (ColorRGBA/randomColor))
    (.setMaterial cube mat)
    cube))

(defn make-floor
  []
  (let [box (Box. (Vector3f. 0 -4 -5) 15 0.2 15)
        floor (Geometry. "the Floor" box)
        mat (load-material "Common/MatDefs/Misc/Unshaded.j3md")]
    (.setColor mat "Color" ColorRGBA/Gray)
    (.setMaterial floor mat)
    floor))

(def shootables (doto (Node. "Shootables")
                  (.attachChild (make-cube "a Dragon" -2 0 1))
                  (.attachChild (make-cube "a tin can" 1 -2 0))
                  (.attachChild (make-cube "the Sheriff" 0 1 -2))
                  (.attachChild (make-cube "the Deputy" 1 0 -4))
                  (.attachChild (make-floor))))

(def mark (let [sphere (Sphere. 30 30 0.2)
                mark (Geometry. "BOOM!" sphere)
                mat (load-material "Common/MatDefs/Misc/Unshaded.j3md")]
            (.setColor mat "Color" ColorRGBA/Red)
            (.setMaterial mark mat)
            mark))

(defn shoot
  [app is-pressed tpf]
  (if (not is-pressed)
    (let [root-node (.getRootNode app)
          results (CollisionResults.)
          cam (.getCamera app)
          ray (Ray. (.getLocation cam) (.getDirection cam))]
      (.collideWith shootables ray results)
      (println "-----" "Collisions?" (.size results) "-----")
      (doall (map (fn [i]
                    (let [collision (.getCollision results i)
                          dist (.getDistance collision)
                          pt (.getContactPoint collision)
                          hit (.getName (.getGeometry collision))]
                      (println "* Collision #" i)
                      (println " You shot" hit "at" pt "," dist "wu away.")))
                  (range (.size results))))
      (if (> (.size results) 0)
        (let [closest (.getClosestCollision results)]
          (.setLocalTranslation mark (.getContactPoint closest))
          (.attachChild root-node mark))
        (.detachChild root-node mark)))))

(def on-action {'(:key-space :button-left) shoot})

(defn init-cross-hairs
  [gui-node]
  (let [font (load-font "Interface/Fonts/Default.fnt")
        ch (BitmapText. font false)]
    (.detachAllChildren gui-node)
    (doto ch
      (.setSize (* 2 (.getRenderedSize (.getCharSet font))))
      (.setText "+")
      (.setLocalTranslation (- (/ (.getWidth *app-settings*) 2)
                               (* 2
                                  (/ (.getRenderedSize (.getCharSet font))
                                     3)))
                            (+ (/ (.getHeight *app-settings*) 2)
                               (/ (.getLineHeight ch) 2))
                            0))
    (.attachChild gui-node ch)))

(defn setup-fn
  [app]
  (let [root-node (.getRootNode app)
        gui-node (.getGuiNode app)]
    (init-cross-hairs gui-node)
    (.attachChild root-node shootables)))

(defn -main
  [& args]
  (.start (make-app :init setup-fn
                    :on-action on-action)))
