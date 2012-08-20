(ns maqueta.core
  (:import (com.jme3.app SimpleApplication)
           (com.jme3.material Material)
           (com.jme3.scene Geometry)
           (com.jme3.scene.shape Box)
           (com.jme3.math Vector3f)
           (com.jme3.math ColorRGBA))
  (:gen-class))

(def app (proxy [SimpleApplication] []
           (simpleInitApp []
             (let [b (Box. Vector3f/ZERO 1 1 1)
                   geom (Geometry. "Box" b)
                   mat (Material. (.getAssetManager this)
                                  "Common/MatDefs/Misc/Unshaded.j3md")]
               (.setColor mat "Color" ColorRGBA/Blue)
               (.setMaterial geom mat)
               (.attachChild (.getRootNode this) geom)))))

(defn -main [& args]
  (.start app))
