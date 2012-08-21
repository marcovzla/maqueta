(ns maqueta.core
  (:use maqueta.assets)
  (:import com.jme3.app.SimpleApplication
           com.jme3.system.AppSettings
           com.jme3.math.ColorRGBA
           com.jme3.math.Vector3f
           com.jme3.scene.Geometry
           com.jme3.scene.shape.Box)
  (:gen-class))

(def ^:dynamic *app-settings* (doto (AppSettings. true)
                                (.setFullscreen false)
                                (.setTitle "maqueta")))

(def player (let [box (Box. Vector3f/ZERO 1 1 1)
                  geo (Geometry. "blue cube" box)
                  mat (load-material "Common/MatDefs/Misc/Unshaded.j3md")]
              (.setColor mat "Color" ColorRGBA/Blue)
              (.setMaterial geo mat)
              geo))

(def app (proxy [SimpleApplication] []
           (simpleInitApp []
             (.attachChild (.getRootNode this) player))

           (simpleUpdate [tpf]
             (.rotate player 0 (* 2 tpf) 0))))

(defn -main [& args]
  (doto app
    (.setShowSettings false)
    (.setSettings *app-settings*)
    (.start)))
