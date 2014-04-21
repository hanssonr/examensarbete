package se.rhel.model;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import se.rhel.model.entity.ActionEntity;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.BulletWorld;

/**
 * Group: Logic
 */
public class Player extends ActionEntity implements IPlayer {

    //Finals
    private final float JUMP_HEIGHT = 7f;
    private final float GRAVITY_POWER = 15f;


    private float mRespawnTimer;
    private float mGravity = 0f;

    //Actions
    private boolean mIsJumping = false;
    private boolean mOnGround = false;

    //RayCasts
    private ClosestRayResultCallback rayTestCB;
    private Vector3 fromGround = new Vector3();
    private Vector3 toGround = new Vector3();

    private static Vector2 mPlayersize = new Vector2(0.6f, 1.5f);
    private Vector3 mVelocity = new Vector3();
    private static int MAX_HEALTH = 100;

    public Player(Vector3 position, BulletWorld world) {
        super(world, MAX_HEALTH, 7f);

        getTransformation().setTranslation(position);

        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Zero);
        createPyshicsBody();
    }

    private void createPyshicsBody() {
        btCollisionShape shape = new btCapsuleShape(mPlayersize.x, mPlayersize.y);
        btRigidBodyConstructionInfo info = new btRigidBodyConstructionInfo(5f, null, shape, Vector3.Zero);
        info.setFriction(0f);
        btDefaultMotionState motionstate = new btDefaultMotionState(getTransformation());

        super.createPhysicBody(shape, info, motionstate, this);
    }

    public void update(float delta) {
        super.update(delta);
        if(isAlive()) {
            getBody().setGravity(Vector3.Zero);
            mTransformation.set(getBody().getCenterOfMassTransform());

            checkOnGround();
            calculateGravity(delta);

        } else {
            mRespawnTimer += delta;
            if(mRespawnTimer > 5f) {

            }
        }
    }

    private void checkOnGround() {
        mOnGround = false;
        fromGround.set(getPosition());
        toGround.set(new Vector3(fromGround.x, fromGround.y - mPlayersize.y, fromGround.z));

        ClosestRayResultCallback cb = new ClosestRayResultCallback(fromGround, toGround);
        cb.setCollisionObject(null);

        getWorld().getCollisionWorld().rayTest(fromGround, toGround, cb);

        if(cb.hasHit()) {
            final btCollisionObject obj = cb.getCollisionObject();
            if (obj.isStaticObject()) {
                mOnGround = true;
            }
        }
    }

    public void jump() {
        if(isGrounded()) {
            mGravity = JUMP_HEIGHT;
            mIsJumping = true;
        }
    }

    private void calculateGravity(float delta) {
        if(!isGrounded()) {
            mGravity -= GRAVITY_POWER * delta;
        } else {
            if (mGravity < -10) mGravity = -10;
            mGravity += GRAVITY_POWER * delta;
            if (mGravity > 0) mGravity = 0;
        }
    }

    public void move(Vector3 direction) {
        getBody().activate(true);

        mVelocity.set(0, 0, 0);
        mVelocity.add(getForward().scl(direction.z * mMovespeed));
        mVelocity.add(getForward().crs(Vector3.Y).scl(direction.x * mMovespeed));

        mVelocity.y = mGravity;

        //Change gravity if jumping
        if (mIsJumping) {
            mVelocity.y = JUMP_HEIGHT;
            mIsJumping = false;
        }

        getBody().setLinearVelocity(mVelocity);
    }

    public Vector3 getVelocity() {
        return mVelocity;
    }

    public boolean isGrounded() {
        return mOnGround;
    }

}
