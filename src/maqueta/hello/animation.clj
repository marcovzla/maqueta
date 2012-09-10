;;;; jMonkeyEngine 3 Tutorial (7) - Hello Animation
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_animation

(ns maqueta.hello.animation
  (:use (maqueta application assets))
  (:import com.jme3.math.ColorRGBA
           com.jme3.math.Vector3f
           com.jme3.scene.Node
           (com.jme3.animation AnimControl LoopMode)
           com.jme3.light.DirectionalLight))

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

(def on-action {:key-space walk})

(defn cycle-done
  [app control channel]
  (doto channel
    (.setAnim "stand" 0.5)
    (.setLoopMode LoopMode/DontLoop)
    (.setSpeed 1)))

(def on-anim-cycle-done {"Walk" cycle-done})

(defn setup-fn
  [app]
  (let [root-node (.getRootNode app)
        viewport (.getViewPort app)]
    (.setBackgroundColor viewport ColorRGBA/LightGray)
    (.setDirection light (.normalizeLocal (Vector3f. -0.1 -1 -1)))
    (.addLight root-node light)
    (.setLocalScale player (Vector3f. 0.5 0.5 0.5))
    (.addListener control app)
    (.setAnim channel "stand")))

(defn -main
  [& args]
  (.start (make-app :root-node player
                    :setup-fn setup-fn
                    :on-anim-cycle-done on-anim-cycle-done
                    :on-action on-action)))