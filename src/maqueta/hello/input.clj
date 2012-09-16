;;;; jMonkeyEngine 3 Tutorial (5) - Hello Input System
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_input_system

(ns maqueta.hello.input
  (:use (maqueta application assets))
  (:import (com.jme3.math ColorRGBA Vector3f)
           com.jme3.scene.Geometry
           com.jme3.scene.shape.Box))

(defn get-speed
  [app]
  (-> com.jme3.app.Application
      (.getDeclaredField "speed")
      (doto (.setAccessible true))
      (.get app)))

(def player (let [b (Box. Vector3f/ZERO 1 1 1)
                  geo (Geometry. "Player" b)
                  mat (load-material "Common/MatDefs/Misc/Unshaded.j3md")]
              (.setColor mat "Color" ColorRGBA/Blue)
              (.setMaterial geo mat)
              geo))

(def is-running (atom true))

(defn toggle-pause
  [app key-pressed tpf]
  (if (not key-pressed)
    (reset! is-running (not @is-running))))

(defn rotate
  [app value tpf]
  (if @is-running
    (.rotate player 0 (* value (get-speed app)) 0)
    (println "Press P to unpause")))

(defn right
  [app value tpf]
  (if @is-running
    (let [v (.getLocalTranslation player)]
      (.setLocalTranslation player
                            (+ (.getX v) (* value (get-speed app)))
                            (.getY v)
                            (.getZ v)))
    (println "Press P to unpause")))

(defn left
  [app value tpf]
  (if @is-running
    (let [v (.getLocalTranslation player)]
      (.setLocalTranslation player
                            (- (.getX v) (* value (get-speed app)))
                            (.getY v)
                            (.getZ v)))
    (println "Press P to unpause")))

(defn -main
  [& args]
  (.start (make-app :root-node player
                    :on-action {:key-p toggle-pause}
                    :on-analog {:key-j left
                                :key-k right
                                '(:key-space :button-left) rotate})))
