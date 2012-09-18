;;;; jMonkeyEngine 3 Tutorial (4) - Hello Update Loop
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_main_event_loop

(ns maqueta.hello.loop
  (:use (maqueta application geometry))
  (:import com.jme3.math.ColorRGBA))

(defn -main
  [& args]
  (let [player (make-box "blue cube" :x 1 :y 1 :z 1 :color ColorRGBA/Blue)]
    (.start (make-app :root-node player
                      :update #(.rotate player 0 (* 2 %2) 0)))))
