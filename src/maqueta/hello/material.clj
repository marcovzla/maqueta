;;;; jMonkeyEngine 3 Tutorial (6) - Hello Materials
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_material

(ns maqueta.hello.material
  (:use (maqueta application assets))
  (:import com.jme3.light.DirectionalLight
           com.jme3.material.RenderState
           com.jme3.math.ColorRGBA
           com.jme3.math.Vector3f
           com.jme3.scene.Geometry
           com.jme3.scene.shape.Box
           com.jme3.scene.shape.Sphere
           com.jme3.util.TangentBinormalGenerator
           com.jme3.renderer.queue.RenderQueue))

(defn init-app
  [app]
  (let [root-node (.getRootNode app)
        boxshape1 (Box. (Vector3f. -3 1.1 0) 1 1 1)
        cube (Geometry. "My Textured Box" boxshape1)
        mat-stl (load-material "Common/MatDefs/Misc/Unshaded.j3md")
        tex-ml (load-texture "Interface/Logo/Monkey.jpg")
        
        boxshape3 (Box. Vector3f/ZERO 1 1 0.01)
        window-frame (Geometry. "window frame" boxshape3)
        mat-tt (load-material "Common/MatDefs/Misc/Unshaded.j3md")

        boxshape4 (Box. (Vector3f. 3 -1 0) 1 1 1)
        cube-leak (Geometry. "Leak-through color cube" boxshape4)
        mat-tl (load-material "Common/MatDefs/Misc/Unshaded.j3md")

        rock (Sphere. 32 32 2)
        shiny-rock (Geometry. "Shiny rock" rock)
        mat-lit (load-material "Common/MatDefs/Light/Lighting.j3md")

        sun (DirectionalLight.)]

    (.setTexture mat-stl "ColorMap" tex-ml)
    (.setMaterial cube mat-stl)

    (.setTexture mat-tt "ColorMap" (load-texture "Textures/ColoredTex/Monkey.png"))
    (-> mat-tt
        .getAdditionalRenderState
        (.setBlendMode com.jme3.material.RenderState$BlendMode/Alpha))
    (doto window-frame
      (.setMaterial mat-tt)
      (.setQueueBucket com.jme3.renderer.queue.RenderQueue$Bucket/Transparent))

    (doto mat-tl
      (.setTexture "ColorMap" (load-texture "Textures/ColoredTex/Monkey.png"))
      (.setColor "Color" (ColorRGBA. 1 0 1 1)))
    (.setMaterial cube-leak mat-tl)

    (.setTextureMode rock com.jme3.scene.shape.Sphere$TextureMode/Projected)
    (TangentBinormalGenerator/generate rock)
    (doto mat-lit
      (.setTexture "DiffuseMap" (load-texture "Textures/Terrain/Pond/Pond.jpg"))
      (.setTexture "NormalMap" (load-texture "Textures/Terrain/Pond/Pond_normal.png"))
      (.setBoolean "UseMaterialColors" true)
      (.setColor "Specular" ColorRGBA/White)
      (.setColor "Diffuse" ColorRGBA/White)
      (.setFloat "Shininess" 5))
    (doto shiny-rock
      (.setMaterial mat-lit)
      (.setLocalTranslation 0 2 -2)
      (.rotate 1.6 0 0))

    (doto sun
      (.setDirection (.normalizeLocal (Vector3f. 1 0 -2)))
      (.setColor ColorRGBA/White))

    (doto root-node
      (.attachChild cube)
      (.attachChild window-frame)
      (.attachChild cube-leak)
      (.attachChild shiny-rock)
      (.addLight sun))))
        

(defn -main
  []
  (.start (make-app :setup-fn init-app)))