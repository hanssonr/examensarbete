package se.rhel.model.entity;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import se.rhel.model.physics.BulletWorld;


/**
 * Group: Mixed
 */
public abstract class GameObject {

    private int ID;
    public int getId() { return ID; }
    public void setId(int id) { ID = id; }

    protected Matrix4 mTransformation = new Matrix4().idt();
    private btRigidBody mBody;
    protected BulletWorld mPhysicsworld;

    public GameObject(BulletWorld physicsworld) {
        mPhysicsworld = physicsworld;
    }

    public Matrix4 getTransformation() { return mTransformation; }

    public void createPhysicBody(btCollisionShape shape, btRigidBodyConstructionInfo info,
                                 btDefaultMotionState motionstate, Object userdata  )
    {
        mBody = new btRigidBody(info);
        mBody.userData = userdata;
        mBody.setMotionState(motionstate);

        mPhysicsworld.addToWorld(shape, info, motionstate, mBody);
    }

    protected btRigidBody getBody() {
        if(mBody == null) try {
            throw new Exception("Need to call createPhysicsBody before using it");
        } catch (Exception e) { System.exit(-1);}

        return mBody;
    }

    protected BulletWorld getWorld() {
        return mPhysicsworld;
    }

    public Vector3 getPosition() {
        return getBody().getCenterOfMassPosition();
    }

    public void setPosition(Vector3 val) {
        getBody().translate(val);
        Matrix4 m = new Matrix4(val, getBody().getOrientation(), new Vector3(1f, 1f, 1f));
        getBody().setCenterOfMassTransform(m);
    }

    public void setPositionAndRotation(Vector3 position, float rotX) {
        Matrix4 m = new Matrix4();
        m.rotate(Vector3.Y, rotX);
        m.setTranslation(position);

        getBody().setCenterOfMassTransform(m);
    }

    public void destroy() {
        getWorld().getCollisionWorld().removeCollisionObject(getBody());
    }
}
