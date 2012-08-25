(ns maqueta.application
  (:import com.jme3.app.SimpleApplication
           com.jme3.system.AppSettings
           (com.jme3.input KeyInput MouseInput)
           (com.jme3.input.controls Trigger
                                    KeyTrigger
                                    MouseButtonTrigger
                                    ActionListener
                                    AnalogListener)))

(def ^:dynamic *app-settings* (doto (AppSettings. true)
                                (.setFullscreen false)
                                (.setTitle "maqueta")))

(defn no-op
  "Takes any number of arguments and does nothing."
  [& _])

(defmacro name->keyword
  [name]
  `(keyword
    (-> ~name
        .toLowerCase
        (.replaceAll "_" "-"))))

(defmacro keyword->name
  "Converts keywords like :key-j into \"KEY_J\"."
  [keyword]
  `(-> (name ~keyword)
       .toUpperCase
       (.replaceAll "-" "_")))

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
            (.addMapping input-manager name
                         (into-array Trigger [trigger]))
            (.addListener input-manager listener
                          (into-array String [name]))))
        (keys key-map))))

(defn make-app
  [root-node setup-fn update-fn action-key-map analog-key-map]
  (doto
      (proxy [SimpleApplication ActionListener AnalogListener] []
        (simpleInitApp []
          (initialize-inputs (.getInputManager this)
                             (cast ActionListener this) 
                             action-key-map)
          (initialize-inputs (.getInputManager this)
                             (cast AnalogListener this)
                             analog-key-map)
          ;; attach root-node to application
          (.attachChild (.getRootNode this) root-node)
          (setup-fn this))
        (simpleUpdate [tpf]
          (update-fn this tpf))
        (onAction
          [name is-pressed tpf]
          (if-let [callback (action-key-map (name->keyword name))]
            (callback this is-pressed tpf)))
        (onAnalog
          [name value tpf]
          (if-let [callback (analog-key-map (name->keyword name))]
            (callback this value tpf))))
    ;; don't show settings dialog
    (.setShowSettings false)
    (.setSettings *app-settings*)))
