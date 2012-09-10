;;;; jMonkeyEngine 3 Tutorial (12) - Hello Effects
;;;; http://jmonkeyengine.org/wiki/doku.php/jme3:beginner:hello_effects

(ns maqueta.hello.effects
  (:use (maqueta application assets))
  (:import (com.jme3.effect ParticleEmitter ParticleMesh)
           (com.jme3.math ColorRGBA Vector3f)))

(defn init-app
  [app]
  (let [fire (ParticleEmitter. "Emitter"
                               com.jme3.effect.ParticleMesh$Type/Triangle
                               30)
        mat-red (load-material "Common/MatDefs/Misc/Particle.j3md")
        debris (ParticleEmitter. "Debris"
                                 com.jme3.effect.ParticleMesh$Type/Triangle
                                 10)
        debris-mat (load-material "Common/MatDefs/Misc/Particle.j3md")]
    (.setTexture mat-red "Texture"
                 (load-texture "Effects/Explosion/flame.png"))
    (.setTexture debris-mat "Texture"
                 (load-texture "Effects/Explosion/Debris.png"))
    (doto fire
      (.setMaterial mat-red)
      (.setImagesX 2)
      (.setImagesY 2)
      (.setEndColor (ColorRGBA. 1 0 0 1))
      (.setStartColor (ColorRGBA. 1 1 0 0.5))
      (.setStartSize 1.5)
      (.setEndSize 0.1)
      (.setGravity 0 0 0)
      (.setLowLife 1)
      (.setHighLife 3))
    (-> fire
        .getParticleInfluencer
        (.setInitialVelocity (Vector3f. 0 2 0)))
    (-> fire
        .getParticleInfluencer
        (.setVelocityVariation 0.3))
    (doto debris
      (.setMaterial debris-mat)
      (.setImagesX 3)
      (.setImagesY 3)
      (.setRotateSpeed 4)
      (.setSelectRandomImage true)
      (.setStartColor ColorRGBA/White)
      (.setGravity 0 6 0))
    (-> debris
        .getParticleInfluencer
        (.setInitialVelocity (Vector3f. 0 4 0)))
    (-> debris
        .getParticleInfluencer
        (.setVelocityVariation 0.6))
    (doto (.getRootNode app)
      (.attachChild fire)
      (.attachChild debris))
    (.emitAllParticles debris)))

(defn -main
  [& args]
  (.start (make-app :setup-fn init-app)))
