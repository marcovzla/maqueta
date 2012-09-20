(ns maqueta.geometry
  (:use maqueta.assets)
  (:import com.jme3.scene.Geometry
           (com.jme3.scene.shape Box Sphere)
           (com.jme3.math ColorRGBA Vector3f)))

(defn make-box
  [name x y z & {:keys [center material color]
                  :or {center Vector3f/ZERO
                       material "Common/MatDefs/Misc/Unshaded.j3md"
                       color (ColorRGBA/randomColor)}}]
  (let [box (Box. center x y z)
        geom (Geometry. name box)
        mat (load-material material)]
    (.setColor mat "Color" color)
    (doto geom (.setMaterial mat))))

(defn make-sphere
  [name z-samples radial-samples radius
   & {:keys [material color]
      :or {material "Common/MatDefs/Misc/Unshaded.j3md"
           color (ColorRGBA/randomColor)}}]
  (let [sphere (Sphere. z-samples radial-samples radius)
        geom (Geometry. name sphere)
        mat (load-material material)]
    (.setColor mat "Color" color)
    (doto geom (.setMaterial mat))))
