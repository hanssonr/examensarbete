package se.rhel.model.component;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;

/**
 * Created by rkh on 2014-04-27.
 */
public class GravityComponent implements IGravity, IComponent, IUpdateable {

    //RayCasts
    private ClosestRayResultCallback rayTestCB;
    private Vector3 fromBottom = new Vector3();
    private Vector3 toGround = new Vector3();
    private boolean mOnGround = false;
    private float mGravity = 0f;
    private float mGravityPower = 15f;
    private float mCastRayLength = 0.2f;
    private IPhysics mPhysics;

    private btCollisionWorld mCollisionWorld;

    public GravityComponent(btCollisionWorld collisionWorld, IPhysics physiccomponent, float gravityPower) {
        mCollisionWorld = collisionWorld;
        mPhysics = physiccomponent;
        mGravityPower = gravityPower;
    }

    private void checkOnGround(Vector3 bottomPosition) {
        mOnGround = false;
        //0.1f bias since bottom position can sometimes be "under" the ground and therefore never trigger a collision
        fromBottom.set(new Vector3(bottomPosition.x, bottomPosition.y + 0.1f, bottomPosition.z));
        toGround.set(new Vector3(fromBottom.x, fromBottom.y - mCastRayLength, fromBottom.z));

        ClosestRayResultCallback cb = new ClosestRayResultCallback(fromBottom, toGround);
        cb.setCollisionObject(null);

        mCollisionWorld.rayTest(fromBottom, toGround, cb);

        if(cb.hasHit()) {
            final btCollisionObject obj = cb.getCollisionObject();
            if (obj.isStaticObject()) {
                mOnGround = true;
            }
        }
    }

    public void update(float delta) {
        mPhysics.getBody().setGravity(Vector3.Zero);
        checkOnGround(mPhysics.getBottomPosition());

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
