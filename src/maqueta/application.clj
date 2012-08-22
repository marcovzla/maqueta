(ns maqueta.application
  (:import com.jme3.app.SimpleApplication
           com.jme3.system.AppSettings
           com.jme3.input.controls.ActionListener))

(def ^:dynamic *app-settings* (doto (AppSettings. true)
                                (.setFullscreen false)
                                (.setTitle "maqueta")))

(defn no-op
  "Takes any number of arguments and does nothing."
  [& _])

(defn make-app
  [root-node setup-fn update-fn]
  (doto
    (proxy [SimpleApplication] []
      (simpleInitApp []
        ;; attach root-node to application
        (.attachChild (.getRootNode this) root-node)
        (setup-fn this))
      (simpleUpdate [tpf]
        (update-fn this tpf)))
    ;; don't show settings dialog
    (.setShowSettings false)
    (.setSettings *app-settings*)))
