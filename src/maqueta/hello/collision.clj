;;;; jMonkeyEngine 3 Tutorial (9) - Hello Collision
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_collision

(ns maqueta.hello.collision
  (:use (maqueta application assets))
  (:import com.jme3.bullet.BulletAppState
           com.jme3.bullet.control.CharacterControl
           com.jme3.bullet.control.RigidBodyControl
           com.jme3.bullet.collision.shapes.CapsuleCollisionShape
           com.jme3.bullet.util.CollisionShapeFactory
           com.jme3.light.AmbientLight
           com.jme3.light.DirectionalLight
           com.jme3.scene.Node
           com.jme3.math.ColorRGBA
           com.jme3.scene.Spatial
           com.jme3.math.Vector3f))

(def player (doto (CharacterControl. (CapsuleCollisionShape. 1.5 6 1) 0.05)
              (.setJumpSpeed 20)
              (.setFallSpeed 30)
              (.setGravity 30)
              (.setPhysicsLocation (Vector3f. 0 10 0))))

(def walk-direction (Vector3f.))

(def left (atom false))
(def right (atom false))
(def up (atom false))
(def down (atom false))

(defn init-app
  [app]
  (let [bullet-app-state (BulletAppState.)
        al (AmbientLight.)
        dl (DirectionalLight.)
        scene-model (doto (load-model "Scenes/town/main.scene")
                      (.setLocalScale (Vector3f. 2 2 2)))
        scene-shape (CollisionShapeFactory/createMeshShape
                     (cast Node scene-model))
        landscape (RigidBodyControl. scene-shape 0)]
    (.attach (.getStateManager app) bullet-app-state)
    (.setBackgroundColor (.getViewPort app) (ColorRGBA. 0.7 0.8 1 1))
    (.setMoveSpeed (.getFlyByCamera app) 100)
    (.setColor al (.mult ColorRGBA/White 1.3))
    (doto dl
      (.setColor ColorRGBA/White)
      (.setDirection (.normalizeLocal (Vector3f. 2.8 -2.8 -2.8))))
    (.addControl scene-model landscape)
    (doto (.getRootNode app)
      (.attachChild scene-model)
      (.addLight al)
      (.addLight dl))
    (doto (.getPhysicsSpace bullet-app-state)
      (.add landscape)
      (.add player))))

(defn update
  [app tpf]
  (let [cam (.getCamera app)
        cam-dir (-> cam
                    .getDirection
                    .clone
                    (.multLocal 0.6 0.6 0.6))
        cam-left (-> cam
                     .getLeft
                     .clone
                     (.multLocal 0.4 0.4 0.4))]
    (.set walk-direction 0 0 0)
    (cond
     @left (.addLocal walk-direction cam-left)
     @right (.addLocal walk-direction (.negate cam-left))
     @up (.addLocal walk-direction cam-dir)
     @down (.addLocal walk-direction (.negate cam-dir)))
    (.setWalkDirection player walk-direction)
    (.setLocation cam (.getPhysicsLocation player))))

(def on-action {:key-a (fn [app value tpf] (reset! left value))
                :key-d (fn [app value tpf] (reset! right value))
                :key-w (fn [app value tpf] (reset! up value))
                :key-s (fn [app value tpf] (reset! down value))
                :key-space (fn [app value tpf] (.jump player))})

(defn -main
  [& args]
  (.start (make-app :init init-app
                    :update update
                    :on-action on-action)))
