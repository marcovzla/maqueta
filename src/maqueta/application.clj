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

(defn initialize-inputs
  [input-manager listener key-map]
  (doall
   (map (fn [keyword]
          (let [name (keyword->name keyword)
                trigger (get-trigger keyword)]
            (doto input-manager
              (.addMapping name (into-array Trigger [trigger]))
              (.addListener listener (into-array String [name])))))
        (keys key-map))))

(defn make-app
  [& {:keys [root-node setup-fn update-fn show-settings
             on-action on-analog on-anim-cycle-done]
      :or {root-node nil
           setup-fn no-op
           update-fn no-op
           show-settings false
           on-action {}
           on-analog {}
           on-anim-cycle-done {}}}]
  (doto
      (proxy [SimpleApplication
              ActionListener AnalogListener
              AnimEventListener] []
        (simpleInitApp []
          (if root-node
            (.attachChild (.getRootNode this) root-node))
          (doto (.getInputManager this)
            (initialize-inputs (cast ActionListener this) on-action)
            (initialize-inputs (cast AnalogListener this) on-analog))
          (setup-fn this))
        (simpleUpdate [tpf]
          (update-fn this tpf))
        (onAction
          [name is-pressed tpf]
          (if-let [callback (on-action (name->keyword name))]
            (callback this is-pressed tpf)))
        (onAnalog
          [name value tpf]
          (if-let [callback (on-analog (name->keyword name))]
            (callback this value tpf)))
        (onAnimChange
          [control channel name])
        (onAnimCycleDone
          [control channel name]
          (if-let [callback (on-anim-cycle-done name)]
            (callback this control channel))))
    ;; don't show settings dialog
    (.setShowSettings show-settings)
    (.setSettings *app-settings*)))
