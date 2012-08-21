(ns maqueta.core
  (:use maqueta.assets)
  (:import (com.jme3.app SimpleApplication)
           (com.jme3.system AppSettings)
           (com.jme3.font BitmapText)
           (com.jme3.light DirectionalLight)
           (com.jme3.math Vector3f)
           (com.jme3.scene Geometry)
           (com.jme3.scene.shape Box))
  (:gen-class))

(def ^:dynamic *app-settings* (doto (AppSettings. true)
                                (.setFullscreen false)
                                (.setTitle "maqueta")))

(def app (proxy [SimpleApplication] []
           (simpleInitApp []
             (let [teapot (load-model "Models/Teapot/Teapot.obj")
                   mat-default (load-material "Common/MatDefs/Misc/ShowNormals.j3md")
                   box (Box. Vector3f/ZERO 2.5 2.5 1.0)
                   wall (Geometry. "Box" box)
                   mat-brick (load-material "Common/MatDefs/Misc/Unshaded.j3md")
                   gui-font (load-font "Interface/Fonts/Default.fnt")
                   hello-text (BitmapText. gui-font false)
                   ninja (load-model "Models/Ninja/Ninja.mesh.xml")
                   sun (DirectionalLight.)]

               (.setMaterial teapot mat-default)
               (.attachChild (.getRootNode this) teapot)

               (.setTexture mat-brick "ColorMap"
                            (load-texture "Textures/Terrain/BrickWall/BrickWall.jpg"))
               (.setMaterial wall mat-brick)
               (.setLocalTranslation wall 2.0 -2.5 0.0)
               (.attachChild (.getRootNode this) wall)

               (.detachAllChildren (.getGuiNode this))
               (.setSize hello-text (.getRenderedSize (.getCharSet gui-font)))
               (.setText hello-text "Hello World")
               (.setLocalTranslation hello-text 300 (.getLineHeight hello-text) 0)
               (.attachChild (.getGuiNode this) hello-text)

               (.scale ninja 0.05 0.05 0.05)
               (.rotate ninja 0.0 -3.0 0.0)
               (.setLocalTranslation ninja 0.0 -5.0 -2.0)
               (.attachChild (.getRootNode this) ninja)

               (.setDirection sun (Vector3f. -0.1 -0.7 -1.0))
               (.addLight (.getRootNode this) sun)))))

(defn -main [& args]
  (doto app
    (.setShowSettings false)
    (.setSettings *app-settings*)
    (.start)))
