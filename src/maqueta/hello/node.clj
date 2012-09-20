;;;; jMonkeyEngine 3 Tutorial (2) - Hello Node
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_node

(ns maqueta.hello.node
  (:use (maqueta application geometry))
  (:import com.jme3.scene.Node
           (com.jme3.math ColorRGBA Vector3f)))

(defn setup-fn
  [app]
  (.attachChild (.getRootNode app)
                (doto (Node. "pivot")
                  (.attachChild (make-box "Box" 1 1 1
                                          :center (Vector3f. 1 -1 1)
                                          :color ColorRGBA/Blue))
                  (.attachChild (make-box "Box" 1 1 1
                                          :center (Vector3f. 1 3 1)
                                          :color ColorRGBA/Red))
                  (.rotate 0.4 0.4 0))))

(defn -main
  [& args]
  (.start (make-app :init setup-fn)))
