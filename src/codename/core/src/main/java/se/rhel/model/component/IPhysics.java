package se.rhel.model.component;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import se.rhel.model.physics.BulletWorld;

/**
 * Created by rkh on 2014-04-24.
 */
public interface IPhysics {

    public BulletWorld getPhysicsWorld();
    public btRigidBody getBody();
    public void createPhysicsBody(btCollisionShape shape, btRigidBodyConstructionInfo info, btDefaultMotionState motionstate, Object userdata);
    public Vector3 getBottomPosition();
}
