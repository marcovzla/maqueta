(ns maqueta.application
  (:use (maqueta util input))
  (:import com.jme3.app.SimpleApplication
           com.jme3.system.AppSettings
           com.jme3.animation.AnimEventListener
           (com.jme3.input.controls ActionListener AnalogListener)))

(def ^:dynamic *app-settings* (doto (AppSettings. true)
                                (.setFullscreen false)
                                (.setTitle "maqueta")))

(defn make-app
  [& {:keys [show-settings root-node init update
             on-action on-analog on-anim-change on-anim-cycle-done]
      :or {show-settings false
           root-node nil
           init no-op
           update no-op
           on-action {}
           on-analog {}
           on-anim-change {}
           on-anim-cycle-done {}}}]
  (doto (proxy [SimpleApplication
                ActionListener AnalogListener
                AnimEventListener] []
          (simpleInitApp []
            (if root-node
              (.attachChild (.getRootNode this) root-node))
            (doto (.getInputManager this)
              (register-inputs (cast ActionListener this) on-action)
              (register-inputs (cast AnalogListener this) on-analog))
            (init this))
          (simpleUpdate [tpf]
            (update this tpf))
          (onAction
            [name is-pressed tpf]
            (when-let [callback (on-action (read-string name))]
              (callback this is-pressed tpf)))
          (onAnalog
            [name value tpf]
            (when-let [callback (on-analog (read-string name))]
              (callback this value tpf)))
          (onAnimChange
            [control channel name]
            (when-let [callback (on-anim-change name)]
              (callback this control channel)))
          (onAnimCycleDone
            [control channel name]
            (when-let [callback (on-anim-cycle-done name)]
              (callback this control channel))))
    (.setShowSettings show-settings)
    (.setSettings *app-settings*)))
