(ns maqueta.input
  (:import (com.jme3.input KeyInput MouseInput)
           (com.jme3.input.controls Trigger
                                    KeyTrigger
                                    MouseButtonTrigger)))

(defmacro make-trigger
  [trigger-class input-class input-name]
  `(new ~trigger-class (-> ~input-class
                           (.getField (-> (name ~input-name)
                                          .toUpperCase
                                          (.replaceAll "-" "_")))
                           (.get nil))))

(defn key-trigger
  [name]
  (make-trigger KeyTrigger KeyInput name))

(defn mouse-trigger
  [name]
  (make-trigger MouseButtonTrigger MouseInput name))

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
  (if (or (keyword? names) (string? names))
    (list (get-trigger names))
    (map #(get-trigger %) names)))

(defn register-inputs
  [input-manager listener key-map]
  (doseq [key (keys key-map)]
    (let [name (print-str key)]
      (doto input-manager
        (.addMapping name (into-array Trigger (get-triggers key)))
        (.addListener listener (into-array String [name]))))))
