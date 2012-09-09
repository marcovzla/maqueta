(ns maqueta.hello.simple-application
  (:use (maqueta application assets))
  (:import com.jme3.scene.Geometry
           com.jme3.scene.shape.Box
           (com.jme3.math ColorRGBA Vector3f)))

(def geom (let [b (Box. Vector3f/ZERO 1 1 1)
                geom (Geometry. "Box" b)
                mat (load-material "Common/MatDefs/Misc/Unshaded.j3md")]
            (.setColor mat "Color" ColorRGBA/Blue)
            (.setMaterial geom mat)
            geom))

(defn -main
  []
  (.start (make-app :root-node geom)))