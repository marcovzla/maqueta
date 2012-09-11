(ns maqueta.core
  (:require maqueta.hello.loop)
  (:gen-class))

(defn -main
  [& args]
  (apply maqueta.hello.loop/-main args))
