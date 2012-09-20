;;;; jMonkeyEngine 3 Tutorial (1) - Hello SimpleApplication
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_simpleapplication

(ns maqueta.hello.simple-application
  (:use (maqueta application geometry))
  (:import com.jme3.math.ColorRGBA))

(defn -main
  [& args]
  (.start (make-app :root-node (make-box "Box" 1 1 1
                                         :color ColorRGBA/Blue))))
