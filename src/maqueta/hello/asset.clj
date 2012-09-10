;;;; jMonkeyEngine 3 Tutorial (3) - Hello Assets
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_asset

(ns maqueta.hello.asset
  (:use (maqueta application assets))
  (:import com.jme3.light.DirectionalLight
           com.jme3.font.BitmapText
           com.jme3.scene.shape.Box
           com.jme3.scene.Geometry
           com.jme3.math.Vector3f))

(defn setup-fn
  [app]
  (let [teapot (load-model "Models/Teapot/Teapot.obj")
        mat-default (load-material "Common/MatDefs/Misc/ShowNormals.j3md")
        wall (Geometry. "Box" (Box. Vector3f/ZERO 2.5 2.5 1.0))
        mat-brick (load-material "Common/MatDefs/Misc/Unshaded.j3md")
        gui-font (load-font "Interface/Fonts/Default.fnt")
        hello-text (BitmapText. gui-font false)
        ninja (load-model "Models/Ninja/Ninja.mesh.xml")
        sun (DirectionalLight.)]

    (.setMaterial teapot mat-default)

    (.setTexture mat-brick "ColorMap"
                 (load-texture "Textures/Terrain/BrickWall/BrickWall.jpg"))

    (doto wall
      (.setMaterial mat-brick)
      (.setLocalTranslation 2.0 -2.5 0.0))

    (doto hello-text
      (.setSize (.getRenderedSize (.getCharSet gui-font)))
      (.setText "Hello World")
      (.setLocalTranslation 300 (.getLineHeight hello-text) 0))

    (doto (.getGuiNode app)
      .detachAllChildren
      (.attachChild hello-text))

    (doto ninja
      (.scale 0.05 0.05 0.05)
      (.rotate 0.0 -3.0 0.0)
      (.setLocalTranslation 0.0 -5.0 -2.0))

    (.setDirection sun (Vector3f. -0.1 -0.7 -1.0))

    (doto (.getRootNode app)
      (.attachChild teapot)
      (.attachChild wall)
      (.attachChild ninja)
      (.addLight sun))))

(defn -main
  [& args]
  (.start (make-app :setup-fn setup-fn)))
