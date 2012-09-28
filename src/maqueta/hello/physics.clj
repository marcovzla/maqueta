;;;; jMonkeyEngine 3 Tutorial (13) - Hello Physics
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_physics

(ns maqueta.hello.physics
  (:use (maqueta application assets))
  (:import (com.jme3.scene.shape Box Sphere Sphere$TextureMode)
           (com.jme3.math Vector2f Vector3f)
           com.jme3.texture.Texture$WrapMode
           com.jme3.asset.TextureKey
           com.jme3.bullet.BulletAppState
           com.jme3.bullet.control.RigidBodyControl
           com.jme3.font.BitmapText
           com.jme3.scene.Geometry))

(def brick-length 0.48)
(def brick-width 0.24)
(def brick-height 0.12)

(def sphere (doto (Sphere. 32 32 0.4 true false)
              (.setTextureMode Sphere$TextureMode/Projected)))

(def box (doto (Box. Vector3f/ZERO brick-length brick-height brick-width)
           (.scaleTextureCoordinates (Vector2f. 1 0.5))))

(def floor (doto (Box. Vector3f/ZERO 10 0.1 5)
             (.scaleTextureCoordinates (Vector2f. 3 6))))

(def wall-mat (let [mat (load-material "Common/MatDefs/Misc/Unshaded.j3md")
                    key (TextureKey. "Textures/Terrain/BrickWall/BrickWall.jpg")
                    tex (load-texture (doto key (.setGenerateMips true)))]
                (doto mat
                  (.setTexture "ColorMap" tex))))

(def stone-mat (let [mat (load-material "Common/MatDefs/Misc/Unshaded.j3md")
                     key (TextureKey. "Textures/Terrain/Rock/Rock.PNG")
                     tex (load-texture (doto key (.setGenerateMips true)))]
                 (doto mat
                   (.setTexture "ColorMap" tex))))

(def floor-mat (let [mat (load-material "Common/MatDefs/Misc/Unshaded.j3md")
                     key (TextureKey. "Textures/Terrain/Pond/Pond.jpg")
                     tex (load-texture (doto key (.setGenerateMips true)))]
                 (.setWrap tex Texture$WrapMode/Repeat)
                 (doto mat
                   (.setTexture "ColorMap" tex))))

(def bullet-app-state (BulletAppState.))

(defn init-floor
  [app]
  (let [floor-geo (Geometry. "Floor" floor)
        floor-phy (RigidBodyControl. (float 0))]
    (doto floor-geo
      (.setMaterial floor-mat)
      (.setLocalTranslation 0 -0.1 0)
      (.addControl floor-phy))
    (.attachChild (.getRootNode app) floor-geo)
    (-> bullet-app-state
        .getPhysicsSpace
        (.add floor-phy))))

(defn make-brick
  [app loc]
  (let [brick-geo (Geometry. "brick" box)
        brick-phy (RigidBodyControl. (float 2))]
    (doto brick-geo
      (.setMaterial wall-mat)
      (.setLocalTranslation loc)
      (.addControl brick-phy))
    (.attachChild (.getRootNode app) brick-geo)
    (-> bullet-app-state
        .getPhysicsSpace
        (.add brick-phy))))

(defn make-brick-rows
  [app j startpt height]
  (when (> j 0)
    (doseq [i (range 6)]
      (make-brick app (Vector3f. (+ startpt (* i brick-length 2))
                                 (+ brick-height height)
                                 0)))
    (make-brick-rows app (- j 1) (- startpt) (+ height (* brick-height 2)))))

(defn init-wall
  [app]
  (make-brick-rows app 15 (/ brick-length 4) 0))

(defn make-cannon-ball
  [app]
  (let [ball-geo (Geometry. "cannon ball" sphere)
        ball-phy (RigidBodyControl. (float 1))
        cam (.getCamera app)]
    (doto ball-geo
      (.setMaterial stone-mat)
      (.setLocalTranslation (.getLocation cam))
      (.addControl ball-phy))
    (.attachChild (.getRootNode app) ball-geo)
    (-> bullet-app-state
        .getPhysicsSpace
        (.add ball-phy))
    (.setLinearVelocity ball-phy (-> cam
                                     .getDirection
                                     (.mult (float 25))))))

(defn init-cross-hairs
  [app]
  (let [gui-node (.getGuiNode app)
        font (load-font "Interface/Fonts/Default.fnt")
        ch (BitmapText. font false)]
    (.detachAllChildren gui-node)
    (doto ch
      (.setSize (* 2 (.getRenderedSize (.getCharSet font))))
      (.setText "+")
      (.setLocalTranslation (- (/ (.getWidth *app-settings*) 2)
                               (* 2
                                  (/ (.getRenderedSize (.getCharSet font))
                                     3)))
                            (+ (/ (.getHeight *app-settings*) 2)
                               (/ (.getLineHeight ch) 2))
                            0))
    (.attachChild gui-node ch)))

(defn init-app
  [app]
  (let [state-manager (.getStateManager app)
        cam (.getCamera app)]
    (.attach state-manager bullet-app-state)
    (doto cam
      (.setLocation (Vector3f. 0 4 6))
      (.lookAt (Vector3f. 2 2 0) Vector3f/UNIT_Y))
    (init-wall app)
    (init-floor app)
    (init-cross-hairs app)))

(defn -main
  [& args]
  (.start (make-app :init init-app
                    :on-action {:button-left (fn [app is-pressed tpf]
                                               (if (not is-pressed)
                                                 (make-cannon-ball app)))})))
