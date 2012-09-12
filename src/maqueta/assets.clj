(ns maqueta.assets
  (:use maqueta.util)
  (:import com.jme3.system.JmeSystem
           com.jme3.material.Material
           com.jme3.asset.plugins.ZipLocator
           com.jme3.audio.AudioNode))

(def desktop-cfg (-> (Thread/currentThread)
                     .getContextClassLoader
                     (.getResource "com/jme3/asset/Desktop.cfg")))

(def asset-manager (JmeSystem/newAssetManager desktop-cfg))

(defn register-zip
  [name]
  ;(.registerLocator asset-manager name ZipLocator)
  (.registerLocator asset-manager (path-join "resources" name) ZipLocator))

(defn load-model
  [name]
  (.loadModel asset-manager name))

(defn load-texture
  [name]
  (.loadTexture asset-manager name))

(defn load-font
  [name]
  (.loadFont asset-manager name))

(defn load-material
  [name]
  (Material. asset-manager name))

(defn load-audio
  [name]
  (AudioNode. asset-manager name false))
