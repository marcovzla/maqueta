(ns maqueta.physics
  (:import com.jme3.scene.Node
           com.jme3.bullet.control.RigidBodyControl
           com.jme3.bullet.util.CollisionShapeFactory))

(defn rigid-body-control
  ([geom]
     (rigid-body-control geom 0))
  ([geom mass]
     (RigidBodyControl.
      (CollisionShapeFactory/createMeshShape (cast Node geom))
      mass)))