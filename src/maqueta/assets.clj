(ns maqueta.assets
  (:import (com.jme3.system JmeSystem)
           (com.jme3.material Material)))

(def desktop-cfg (-> (Thread/currentThread)
                     .getContextClassLoader
                     (.getResource "com/jme3/asset/Desktop.cfg")))

(def asset-manager (JmeSystem/newAssetManager desktop-cfg))

(defn load-model [model-name]
  (.loadModel asset-manager model-name))

(defn load-texture [texture-name]
  (.loadTexture asset-manager texture-name))

(defn load-font [font-name]
  (.loadFont asset-manager font-name))

(defn load-material [material-name]
  (Material. asset-manager material-name))
