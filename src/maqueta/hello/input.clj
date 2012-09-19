;;;; jMonkeyEngine 3 Tutorial (5) - Hello Input System
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_input_system

(ns maqueta.hello.input
  (:use (maqueta application geometry))
  (:import com.jme3.math.ColorRGBA))

(defn get-speed
  [app]
  (-> com.jme3.app.Application
      (.getDeclaredField "speed")
      (doto (.setAccessible true))
      (.get app)))

(def player (make-box "Player" :x 1 :y 1 :z 1 :color ColorRGBA/Blue))

(def is-running (atom true))

(defn toggle-pause
  [app key-pressed tpf]
  (if (not key-pressed)
    (reset! is-running (not @is-running))))

(defmacro def-pausable
  [name & body]
  `(defn ~name
     [~'app ~'value ~'tpf]
     (if @is-running
       (do ~@body)
       (println "Press P to unpause"))))

(def-pausable rotate
  (.rotate player 0 (* value (get-speed app)) 0))

(def-pausable right
  (let [v (.getLocalTranslation player)]
    (.setLocalTranslation player
                          (+ (.getX v) (* value (get-speed app)))
                          (.getY v)
                          (.getZ v))))

(def-pausable left
  (let [v (.getLocalTranslation player)]
    (.setLocalTranslation player
                          (- (.getX v) (* value (get-speed app)))
                          (.getY v)
                          (.getZ v))))

(defn -main
  [& args]
  (.start (make-app :root-node player
                    :on-action {:key-p toggle-pause}
                    :on-analog {:key-j left
                                :key-k right
                                [:key-space :button-left] rotate})))
