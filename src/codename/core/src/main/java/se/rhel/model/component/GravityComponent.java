package se.rhel.model.component;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;

/**
 * Created by rkh on 2014-04-27.
 */
public class GravityComponent implements IGravity, IComponent {

    //RayCasts
    private ClosestRayResultCallback rayTestCB;
    private Vector3 fromGround = new Vector3();
    private Vector3 toGround = new Vector3();
    private boolean mOnGround = false;
    private float mGravity = 0f;
    private float mGravityPower = 15f;

    private btCollisionWorld mCollisionWorld;

    public GravityComponent(btCollisionWorld collisionWorld, float gravityPower) {
        mCollisionWorld = collisionWorld;
        mGravityPower = gravityPower;
    }

    public void checkOnGround(Vector3 position, float length) {
        mOnGround = false;
        fromGround.set(position);
        toGround.set(new Vector3(fromGround.x, fromGround.y - length, fromGround.z));

        ClosestRayResultCallback cb = new ClosestRayResultCallback(fromGround, toGround);
        cb.setCollisionObject(null);

        mCollisionWorld.rayTest(fromGround, toGround, cb);

        if(cb.hasHit()) {
            final btCollisionObject obj = cb.getCollisionObject();
            if (obj.isStaticObject()) {
                mOnGround = true;
            }
        }
    }

    public void calculateGravity(float delta) {
        if(!mOnGround) {
            mGravity -= mGravityPower * delta;
        } else {
            if (mGravity < -10) mGravity = -10;
            mGravity += mGravityPower * delta;
            if (mGravity > 0) mGravity = 0;
        }
    }

    public float getGravity() {
        return mGravity;
    }

    @Override
    public boolean isGrounded() {
        return mOnGround;
    }

    @Override
    public void setGravity(float gravity) {
        mGravity = gravity;
    }
}
