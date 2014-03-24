package se.rhel.model.entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import se.rhel.model.physics.BulletWorld;


/**
 * Group: Logic
 */
public abstract class GameObject {
    protected Matrix4 mTransformation = new Matrix4().idt();
    private btRigidBody mBody;
    protected BulletWorld mPhysicsworld;

    public GameObject(BulletWorld physicsworld) {
        mPhysicsworld = physicsworld;
    }

    public Matrix4 getTransformation() { return mTransformation; }

    public void createPhysicBody(btCollisionShape shape, btRigidBodyConstructionInfo info,
                                 btDefaultMotionState motionstate, Object userdata, ModelInstance instance)
    {
        mBody = new btRigidBody(info);
        mBody.userData = userdata;
        mBody.setMotionState(motionstate);

        mPhysicsworld.addToWorld(shape, info, motionstate, instance, mBody);
    }

    protected abstract void destroy();

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

    public void setPositionAndRotation(float x, float y, float z, float rY, float rW) {
        Vector3 toPos = new Vector3(x, y, z);
        double mag = Math.sqrt(rW * rW + rY * rY);
        Quaternion rotation = new Quaternion(0, (float)(rY/mag), 0, (float)(rW/mag));

        Matrix4 m = new Matrix4();
        m.rotate(rotation);
        m.setTranslation(toPos);

        getBody().setCenterOfMassTransform(m);
    }


}
