;;;; jMonkeyEngine 3 Tutorial (4) - Hello Update Loop
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_main_event_loop

(ns maqueta.hello.loop
  (:use (maqueta application assets))
  (:import (com.jme3.math ColorRGBA Vector3f)
           com.jme3.scene.Geometry
           com.jme3.scene.shape.Box))

(def player (let [box (Box. Vector3f/ZERO 1 1 1)
                  geo (Geometry. "blue cube" box)
                  mat (load-material "Common/MatDefs/Misc/Unshaded.j3md")]
              (.setColor mat "Color" ColorRGBA/Blue)
              (.setMaterial geo mat)
              geo))

(defn -main
  [& args]
  (.start (make-app :root-node player
                    :update-fn #(.rotate player 0 (* 2 %2) 0))))
