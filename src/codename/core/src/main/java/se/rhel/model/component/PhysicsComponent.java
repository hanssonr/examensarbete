package se.rhel.model.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
    private Vector3 mBodySize = new Vector3();

    public PhysicsComponent(BulletWorld physicsWorld) {
        mPhysicsWorld = physicsWorld;
    }

    public void createPhysicsBody(btCollisionShape shape, btRigidBodyConstructionInfo info, btDefaultMotionState motionstate, Object userdata) {
        mBody = new btRigidBody(info);
        mBody.userData = userdata;
        mBody.setMotionState(motionstate);

        mPhysicsWorld.addToWorld(shape, info, motionstate, mBody);

        Vector3 min = new Vector3();
        Vector3 max = new Vector3();
        mBody.getAabb(min, max);

        mBodySize.x = max.x - mBody.getCenterOfMassPosition().x + mBody.getCenterOfMassPosition().x - min.x;
        mBodySize.y = max.y - mBody.getCenterOfMassPosition().y + mBody.getCenterOfMassPosition().y - min.y;
        mBodySize.z = max.z - mBody.getCenterOfMassPosition().z + mBody.getCenterOfMassPosition().z - min.z;
    }


    @Override
    public BulletWorld getPhysicsWorld() {
        return mPhysicsWorld;
    }

    @Override
    public btRigidBody getBody() {
        return mBody;
    }

    public Vector3 getBottomPosition() {
        return mBody.getCenterOfMassPosition().cpy().sub(new Vector3(0, mBodySize.y/2.0f, 0));
    }
}
