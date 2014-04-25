package se.rhel.model.component;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import se.rhel.model.physics.BulletWorld;

/**
 * Created by rkh on 2014-04-24.
 */
public class PhysicsComponent implements IPhysics, IComponent {

    private btRigidBody mBody;
    private BulletWorld mPhysicsWorld;

    public PhysicsComponent(BulletWorld physicsWorld) {
        mPhysicsWorld = physicsWorld;
    }

    public void createPhysicsBody(btCollisionShape shape, btRigidBodyConstructionInfo info, btDefaultMotionState motionstate, Object userdata) {
        mBody = new btRigidBody(info);
        mBody.userData = userdata;
        mBody.setMotionState(motionstate);

        mPhysicsWorld.addToWorld(shape, info, motionstate, mBody);
    }


    @Override
    public BulletWorld getPhysicsWorld() {
        return mPhysicsWorld;
    }

    @Override
    public btRigidBody getBody() {
        return mBody;
    }
}
