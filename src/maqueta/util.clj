(ns maqueta.util)

(defn no-op
  "Takes any number of arguments and does nothing."
  [& _])

(defn name->keyword
  "Converts strings like \"KEY_J\" into :key-j."
  [name]
  (keyword (-> name
               .toLowerCase
               (.replaceAll "_" "-"))))

(defn keyword->name
  "Converts keywords like :key-j into \"KEY_J\"."
  [kword]
  (-> (name kword)
      .toUpperCase
      (.replaceAll "-" "_")))