(defproject maqueta "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/marcovzla/maqueta"
  :license {:name "MIT License"
            :url "https://raw.github.com/marcovzla/maqueta/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.json "0.2.0"]
                 ;; jMonkeyEngine 3.0
                 [local-repo/eventbus "3.0"]
                 [local-repo/jbullet "3.0"]
                 [local-repo/jinput "3.0"]
                 [local-repo/jME3-blender "3.0"]
                 [local-repo/jME3-core "3.0"]
                 [local-repo/jME3-desktop "3.0"]
                 [local-repo/jME3-effects "3.0"]
                 [local-repo/jME3-jbullet "3.0"]
                 [local-repo/jME3-jogg "3.0"]
                 [local-repo/jME3-lwjgl "3.0"]
                 [local-repo/jME3-lwjgl-natives "3.0"]
                 [local-repo/jME3-networking "3.0"]
                 [local-repo/jME3-niftygui "3.0"]
                 [local-repo/jME3-plugins "3.0"]
                 [local-repo/jME3-terrain "3.0"]
                 ;[local-repo/jME3-testdata "3.0"]
                 [local-repo/j-ogg-oggd "3.0"]
                 [local-repo/j-ogg-vorbisd "3.0"]
                 [local-repo/lwjgl "3.0"]
                 [local-repo/nifty "3.0"]
                 [local-repo/nifty-default-controls "3.0"]
                 [local-repo/nifty-examples "3.0"]
                 [local-repo/nifty-style-black "3.0"]
                 [local-repo/stack-alloc "3.0"]
                 [local-repo/vecmath "3.0"]
                 [local-repo/xmlpull-xpp3 "3.0"]]
  :repositories {"project" "file:local-repo"}
  :main maqueta.core)
