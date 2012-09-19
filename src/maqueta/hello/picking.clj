;;;; jMonkeyEngine 3 Tutorial (8) - Hello Picking
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_picking

(ns maqueta.hello.picking
  (:use (maqueta application assets geometry))
  (:import com.jme3.collision.CollisionResults
           com.jme3.font.BitmapText
           (com.jme3.math ColorRGBA Ray Vector3f)
           com.jme3.scene.Node))

(defn make-cube
  [name x y z]
  (make-box name :x 1 :y 1 :z 1 :center (Vector3f. x y z)))

(defn make-floor
  []
  (make-box "the Floor" :x 15 :y 0.2 :z 15
            :center (Vector3f. 0 -4 -5)
            :color ColorRGBA/Gray))

(def shootables (doto (Node. "Shootables")
                  (.attachChild (make-cube "a Dragon" -2 0 1))
                  (.attachChild (make-cube "a tin can" 1 -2 0))
                  (.attachChild (make-cube "the Sheriff" 0 1 -2))
                  (.attachChild (make-cube "the Deputy" 1 0 -4))
                  (.attachChild (make-floor))))

(def mark (make-sphere "BOOM!" 30 30 0.2 :color ColorRGBA/Red))

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
