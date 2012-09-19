;;;; jMonkeyEngine 3 Tutorial (10) - Hello Terrain
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_terrain

(ns maqueta.hello.terrain
  (:use (maqueta application assets))
  (:import com.jme3.texture.Texture$WrapMode
           com.jme3.terrain.heightmap.ImageBasedHeightMap
           (com.jme3.terrain.geomipmap TerrainQuad TerrainLodControl)))

(def patch-size 65)

(defn init-app
  [app]
  (let [fly-cam (.getFlyByCamera app)
        mat-terrain (load-material "Common/MatDefs/Terrain/Terrain.j3md")
        grass (load-texture "Textures/Terrain/splat/grass.jpg")
        dirt (load-texture "Textures/Terrain/splat/dirt.jpg")
        rock (load-texture "Textures/Terrain/splat/road.jpg")
        hmap-img (load-texture "Textures/Terrain/splat/mountains512.png")
        heightmap (doto (ImageBasedHeightMap. (.getImage hmap-img)) .load)
        terrain (TerrainQuad. "my terrain" patch-size 513 (.getHeightMap heightmap))]
    (.setMoveSpeed fly-cam 50)
    (.setWrap grass Texture$WrapMode/Repeat)
    (.setWrap dirt Texture$WrapMode/Repeat)
    (.setWrap rock Texture$WrapMode/Repeat)
    (doto mat-terrain
      (.setTexture "Alpha" (load-texture "Textures/Terrain/splat/alphamap.png"))
      (.setTexture "Tex1" grass)
      (.setFloat "Tex1Scale" 64)
      (.setTexture "Tex2" dirt)
      (.setFloat "Tex2Scale" 32)
      (.setTexture "Tex3" rock)
      (.setFloat "Tex3Scale" 128))
    (.attachChild (.getRootNode app) (doto terrain
                                       (.setMaterial mat-terrain)
                                       (.setLocalTranslation 0 -100 0)
                                       (.setLocalScale 2 1 2)))
    (.addControl terrain (TerrainLodControl. terrain (.getCamera app)))))

(defn -main
  [& args]
  (.start (make-app :init init-app)))
