(ns maqueta.application
  (:use maqueta.util)
  (:import com.jme3.app.SimpleApplication
           com.jme3.system.AppSettings
           (com.jme3.input KeyInput MouseInput)
           (com.jme3.input.controls Trigger
                                    KeyTrigger
                                    MouseButtonTrigger
                                    ActionListener
                                    AnalogListener)
           (com.jme3.animation AnimChannel
                               AnimControl
                               AnimEventListener
                               LoopMode)))

(def ^:dynamic *app-settings* (doto (AppSettings. true)
                                (.setFullscreen false)
                                (.setTitle "maqueta")))

(defmacro make-trigger
  [keyword input-class trigger-class]
  `(new ~trigger-class
        ;; get value of input-class's field by name
        ;; for example, KeyInput/KEY_J => 36
        (-> ~input-class
            (.getField (keyword->name ~keyword))
            (.get nil))))

(defn key-trigger
  [name]
  (make-trigger name KeyInput KeyTrigger))

(defn mouse-trigger
  [name]
  (make-trigger name MouseInput MouseButtonTrigger))

(defn get-trigger
  [name]
  (try
    (key-trigger name)
    (catch NoSuchFieldException e
      (try
        (mouse-trigger name)
        (catch NoSuchFieldException e
          nil)))))

(defn get-triggers
  [names]
  (if (seq? names)
    (vec (map #(get-trigger %) names))
    [(get-trigger names)]))

(defn initialize-inputs
  [input-manager listener key-map]
  (doseq [key (keys key-map)]
    (let [name (print-str key)]
      (doto input-manager
        (.addMapping name (into-array Trigger (get-triggers key)))
        (.addListener listener (into-array String [name]))))))

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
              (initialize-inputs (cast ActionListener this) on-action)
              (initialize-inputs (cast AnalogListener this) on-analog))
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
