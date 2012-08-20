(ns maqueta.core
  (:import (com.jme3.app SimpleApplication)
           (com.jme3.material Material)
           (com.jme3.scene Geometry)
           (com.jme3.scene.shape Box)
           (com.jme3.math Vector3f)
           (com.jme3.math ColorRGBA)
           (com.jme3.scene Node))
  (:gen-class))

(def app (proxy [SimpleApplication] []
           (simpleInitApp []
             (let [box1 (Box. (Vector3f. 1 -1 1) 1 1 1)
                   blue (Geometry. "Box" box1)
                   mat1 (Material. (.getAssetManager this)
                                   "Common/MatDefs/Misc/Unshaded.j3md")
                   box2 (Box. (Vector3f. 1 3 1) 1 1 1)
                   red (Geometry. "Box" box2)
                   mat2 (Material. (.getAssetManager this)
                                   "Common/MatDefs/Misc/Unshaded.j3md")
                   pivot (Node. "pivot")]
               (.setColor mat1 "Color" ColorRGBA/Blue)
               (.setMaterial blue mat1)
               (.setColor mat2 "Color" ColorRGBA/Red)
               (.setMaterial red mat2)
               (.attachChild (.getRootNode this) pivot)
               (.attachChild pivot blue)
               (.attachChild pivot red)
               (.rotate pivot 0.4 0.4 0)))))

(defn -main [& args]
  (.start app))
