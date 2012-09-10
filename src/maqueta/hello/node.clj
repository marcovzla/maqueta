;;;; jMonkeyEngine 3 Tutorial (2) - Hello Node
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_node

(ns maqueta.hello.node
  (:use (maqueta application assets))
  (:import com.jme3.scene.shape.Box
           (com.jme3.scene Geometry Node)
           (com.jme3.math ColorRGBA Vector3f)))

(defn setup-fn
  [app]
  (let [box1 (Box. (Vector3f. 1 -1 1) 1 1 1)
        blue (Geometry. "Box" box1)
        mat1 (load-material "Common/MatDefs/Misc/Unshaded.j3md")
        box2 (Box. (Vector3f. 1 3 1) 1 1 1)
        red (Geometry. "Box" box2)
        mat2 (load-material "Common/MatDefs/Misc/Unshaded.j3md")
        pivot (Node. "pivot")]
    (.setColor mat1 "Color" ColorRGBA/Blue)
    (.setMaterial blue mat1)
    (.setColor mat2 "Color" ColorRGBA/Red)
    (.setMaterial red mat2)
    (.attachChild (.getRootNode app)
                  (doto pivot
                    (.attachChild blue)
                    (.attachChild red)
                    (.rotate 0.4 0.4 0)))))

(defn -main
  []
  (.start (make-app :setup-fn setup-fn)))