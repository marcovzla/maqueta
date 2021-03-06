;;;; jMonkeyEngine 3 Tutorial (11) - Hello Audio
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_audio

(ns maqueta.hello.audio
  (:use (maqueta application assets geometry))
  (:import com.jme3.audio.AudioNode
           (com.jme3.math ColorRGBA Vector3f)))

(def player (make-box "Player" 1 1 1 :color ColorRGBA/Blue))

(def audio-gun (doto (load-audio "Sound/Effects/Gun.wav")
                 (.setLooping false)
                 (.setVolume 2)))

(def audio-nature (doto (load-audio "Sound/Environment/Nature.ogg")
                    (.setLooping true)
                    (.setPositional true)
                    (.setLocalTranslation (.clone Vector3f/ZERO))
                    (.setVolume 3)))

(defn shoot
  [app is-pressed tpf]
  (if (not is-pressed)
    (.playInstance audio-gun)))

(defn init-app
  [app]
  (let [root-node (.getRootNode app)
        fly-cam (.getFlyByCamera app)]
    (.setMoveSpeed fly-cam 40)
    (doto root-node
      (.attachChild player)
      (.attachChild audio-gun)
      (.attachChild audio-nature))
    (.play audio-nature)))

(defn update
  [app tpf]
  (let [cam (.getCamera app)
        listener (.getListener app)]
    (doto listener
      (.setLocation (.getLocation cam))
      (.setRotation (.getRotation cam)))))

(defn -main
  [& args]
  (.start (make-app :init init-app
                    :update update
                    :on-action {:button-left shoot})))
