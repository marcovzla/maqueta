(ns maqueta.core
  (:use (maqueta application assets))
  (:import com.jme3.math.ColorRGBA
           com.jme3.math.Vector3f
           com.jme3.scene.Node
           com.jme3.light.DirectionalLight)
  (:gen-class))

(def light (DirectionalLight.))

(def player (load-model "Models/Oto/Oto.mesh.xml"))

(def action-key-map {:key-h (fn [n p t] (.rotate player 0 1 0))})

(def analog-key-map {:key-j (fn [n v t] (.rotate player 0 v 0))})

(defn setup-fn
  [app]
  (let [root-node (.getRootNode app)
        viewport (.getViewPort app)]
    (.setBackgroundColor viewport ColorRGBA/LightGray)
    (.setDirection light (.normalizeLocal (Vector3f. -0.1 -1 -1)))
    (.addLight root-node light)
    (.setLocalScale player (Vector3f. 0.5 0.5 0.5))))

(defn -main [& args]
  (.start (make-app :root-node player
                    :setup-fn setup-fn
                    :action-key-map action-key-map
                    :analog-key-map analog-key-map)))
