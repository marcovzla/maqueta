(ns maqueta.hello.update-loop
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
  []
  (.start (make-app :root-node player
                    :update-fn #(.rotate player 0 (* 2 %2) 0))))
