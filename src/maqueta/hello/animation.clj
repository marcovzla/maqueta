;;;; jMonkeyEngine 3 Tutorial (7) - Hello Animation
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_animation

(ns maqueta.hello.animation
  (:use (maqueta application assets))
  (:import com.jme3.light.DirectionalLight
           (com.jme3.math ColorRGBA Vector3f)
           (com.jme3.animation AnimControl LoopMode)))

(def light (DirectionalLight.))

(def player (load-model "Models/Oto/Oto.mesh.xml"))

(def control (.getControl player AnimControl))

(def channel (.createChannel control))

(defn walk
  [name is-pressed tpf]
  (if (not is-pressed)
    (doto channel
      (.setAnim "Walk" 0.5)
      (.setLoopMode LoopMode/Loop))))

(defn cycle-done
  [app control channel]
  (doto channel
    (.setAnim "stand" 0.5)
    (.setLoopMode LoopMode/DontLoop)
    (.setSpeed 1)))

(defn setup-fn
  [app]
  (let [root-node (.getRootNode app)
        viewport (.getViewPort app)]
    (.setBackgroundColor viewport ColorRGBA/LightGray)
    (.setDirection light (.normalizeLocal (Vector3f. -0.1 -1 -1)))
    (.setLocalScale player (Vector3f. 0.5 0.5 0.5))
    (.addListener control app)
    (.setAnim channel "stand")
    (doto root-node
      (.attachChild player)
      (.addLight light))))

(defn -main
  [& args]
  (.start (make-app :setup-fn setup-fn
                    :on-action {:key-space walk}
                    :on-anim-cycle-done {"Walk" cycle-done})))
