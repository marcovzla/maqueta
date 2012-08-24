(ns maqueta.application
  (:import com.jme3.app.SimpleApplication
           com.jme3.system.AppSettings
           (com.jme3.input KeyInput MouseInput)
           (com.jme3.input.controls Trigger KeyTrigger MouseButtonTrigger)
           com.jme3.input.controls.ActionListener))

(def ^:dynamic *app-settings* (doto (AppSettings. true)
                                (.setFullscreen false)
                                (.setTitle "maqueta")))

(defn no-op
  "Takes any number of arguments and does nothing."
  [& _])

(defmacro trigger->keyword
  [tname]
  `(keyword
     (-> ~tname
       .toLowerCase
       (.replaceAll "_" "-"))))

(defmacro keyword->trigger
  "Converts keywords like :key-j into \"KEY_J\"."
  [tn]
  `(-> (name ~tn)
     .toUpperCase
     (.replaceAll "-" "_")))

(defmacro make-trigger
  [name input-class trigger-class]
  `(new ~trigger-class
        ;; get value of input-class's field by name
        ;; for example, KeyInput/KEY_J => 36
        (-> ~input-class
          (.getField (keyword->trigger ~name))
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
    (mouse-trigger name)
    (catch NoSuchFieldException e
      (try
        (key-trigger name)
        (catch NoSuchFieldException e
          nil)))))

(defn initialize-inputs
  [app input-manager key-map]
  (doall
    (map (fn [name]
           (let [name (keyword->trigger name)
                 trigger (get-trigger name)]
             (.addMapping input-manager
                          name
                          (into-array Trigger [trigger]))
             (.addListener input-manager app
                           (into-array String [name]))))
         (keys key-map))))

(defn make-app
  [root-node setup-fn update-fn key-map]
  (doto
    (proxy [SimpleApplication ActionListener] []
      (simpleInitApp []
        (initialize-inputs this (.getInputManager this) key-map)
        ;; attach root-node to application
        (.attachChild (.getRootNode this) root-node)
        (setup-fn this))

      (simpleUpdate [tpf]
        (update-fn this tpf))

      (onAction
        [action-name is-pressed tpf]
        (if-let [react (key-map (trigger->keyword action-name))]
          (react this is-pressed tpf))))
    ;; don't show settings dialog
    (.setShowSettings false)
    (.setSettings *app-settings*)))
