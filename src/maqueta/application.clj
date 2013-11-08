(ns maqueta.application
  (:use (maqueta util input))
  (:import com.jme3.app.SimpleApplication
           com.jme3.system.AppSettings
           com.jme3.animation.AnimEventListener
           (com.jme3.input.controls ActionListener AnalogListener)))

(def ^:dynamic *app-settings* (doto (AppSettings. true)
                                (.setTitle "maqueta")
                                (.setFullscreen false)
                                (.setWidth 800)
                                (.setHeight 600)))

(defn get-speed
  [app]
  (-> com.jme3.app.Application
      (.getDeclaredField "speed")
      (doto (.setAccessible true))
      (.get app)))

(defn make-app
  [& {:keys [show-settings root-node init update
             display-fps display-stat-view
             on-action on-analog on-anim-change on-anim-cycle-done]
      :or {show-settings false
           root-node nil
           init no-op
           update no-op
           display-fps true
           display-stat-view true
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
          (onAction [name is-pressed tpf]
            (if-let [callback (on-action (read-string name))]
              (callback this is-pressed tpf)))
          (onAnalog [name value tpf]
            (if-let [callback (on-analog (read-string name))]
              (callback this value tpf)))
          (onAnimChange [control channel name]
            (if-let [callback (on-anim-change name)]
              (callback this control channel)))
          (onAnimCycleDone [control channel name]
            (if-let [callback (on-anim-cycle-done name)]
              (callback this control channel))))
    (.setSettings *app-settings*)
    (.setShowSettings show-settings)
    (.setDisplayFps display-fps)
    (.setDisplayStatView display-stat-view)))
