(ns maqueta.geometry
  (:use maqueta.assets)
  (:import com.jme3.scene.Geometry
           com.jme3.scene.shape.Box
           (com.jme3.math ColorRGBA Vector3f)))

(defn make-box
  [name & {:keys [x y z center material color]
           :or {x 0.5
                y 0.5
                z 0.5
                center Vector3f/ZERO
                material "Common/MatDefs/Misc/Unshaded.j3md"
                color (ColorRGBA/randomColor)}}]
  (let [box (Box. center x y z)
        geom (Geometry. name box)
        mat (load-material material)]
    (.setColor mat "Color" color)
    (doto geom (.setMaterial mat))))
