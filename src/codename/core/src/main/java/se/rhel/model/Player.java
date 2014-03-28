package se.rhel.model;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.physics.BulletWorld;
import se.rhel.res.Resources;

/**
 * Group: Logic
 */
public class Player extends DamageAbleEntity {

    public enum PLAYERSTATE {
        idle, running
    }

    private FPSCamera mCamera;
    private PLAYERSTATE mState;

    //Finals
    private final float JUMP_HEIGHT = 7f;
    private final float GRAVITY_POWER = 15f;

    //Bobbing
    private float mBobPower = 0.7f;
    private float mBobTimer = 0f;
    private Vector3 mBobVector = new Vector3();

    private float mDeltaShoot;
    private float mRespawnTimer;
    private float mGravity = 0f;

    //Actions
    private boolean mIsJumping = false;
    private boolean mOnGround = false;

    //RayCasts
    private ClosestRayResultCallback rayTestCB;
    public boolean mHasShot = false;
    private Vector3 fromGround = new Vector3();
    private Vector3 toGround = new Vector3();

    //Weapon
    private Matrix4 mWeaponWorld = new Matrix4().idt();
    private Vector3 mWeaponOffset = new Vector3();

    private static Vector2 mPlayersize = new Vector2(0.6f, 1.5f);
    private Vector3 mVelocity = new Vector3();
    private static int MAX_HEALTH = 100;

    public Player(Vector3 position, BulletWorld world) {
        super(world, new ModelInstance(Resources.INSTANCE.fpsWeaponModel), MAX_HEALTH, 7f);

        getTransformation().setTranslation(position);
        getWorld().fpsModel = getInstance();
        mState = PLAYERSTATE.idle;
        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Zero);
        createPyshicsBody();
    }

    private void createPyshicsBody() {
        btCollisionShape shape = new btCapsuleShape(mPlayersize.x, mPlayersize.y);
        btRigidBodyConstructionInfo info = new btRigidBodyConstructionInfo(5f, null, shape, Vector3.Zero);
        btDefaultMotionState motionstate = new btDefaultMotionState(getTransformation());

        super.createPhysicBody(shape, info, motionstate, this, getInstance());
    }

    public void update(float delta) {
        if(isAlive()) {
            getBody().setGravity(Vector3.Zero);
            mTransformation.set(getBody().getCenterOfMassTransform());

            updateCamera(delta);
            updateWeapon();
            checkOnGround();
            calculateGravity(delta);

            // Update shooting for unnecessary drawing / spam shooting
            if(mHasShot) {
                mDeltaShoot += delta;
                if(mDeltaShoot > 0.3f) {
                    mHasShot = false;
                    mDeltaShoot = 0f;
                }
            }
        } else {
            mRespawnTimer += delta;
            if(mRespawnTimer > 5f) {

            }
        }
    }

    private void updateWeapon() {
        mWeaponWorld.set(mCamera.view.cpy().inv());
        mWeaponWorld.getTranslation(mWeaponOffset);
        mWeaponOffset.sub(mCamera.up.cpy().scl(0.7f));
        mWeaponOffset.add(mCamera.direction);
        mWeaponOffset.add(mCamera.getRight());

        if(mState == PLAYERSTATE.running) {
            mWeaponOffset.add(mBobVector.cpy().scl(mBobPower));
        }

        mWeaponWorld.setTranslation(mWeaponOffset);
        getInstance().transform.set(mWeaponWorld);
    }

    public void shoot() {
        // If can shoot
        if(!mHasShot) {
            mHasShot = true;
            EventHandler.events.notify(new ModelEvent(EventType.SHOOT));
        }
    }

    private void updateCamera(float delta) {
        if(mCamera != null) {
            mBobTimer +=delta;

            mTransformation.getTranslation(mCamera.position);
            mCamera.position.add(mCamera.getOffset());

            //bobbing
            if(mState == PLAYERSTATE.running) {
                Vector3 dir = mCamera.getRight().cpy();
                dir.y = 1f;
                float x = (float)Math.sin(mBobTimer * 10) * 0.05f;
                float y = (float)Math.cos(mBobTimer * 20) * 0.03f;
                float z = (float)Math.sin(mBobTimer * 10) * 0.05f;
                mBobVector.set(x, y, z);
                mCamera.position.add(mBobVector.scl(dir)).cpy().scl(mBobPower);
            }

            mCamera.update();
        }
    }

    public void attachCamera(FPSCamera camera) {
        this.mCamera = camera;
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
        mVelocity.add(mCamera.getForward().scl(direction.z * mMovespeed));
        mVelocity.add(mCamera.getRight().scl(direction.x * mMovespeed));

        mVelocity.y = mGravity;

        //Change gravity if jumping
        if (mIsJumping) {
            mVelocity.y = JUMP_HEIGHT;
            mIsJumping = false;
        }

        getBody().setLinearVelocity(mVelocity);

        if(Math.abs(mVelocity.x) > 0 || Math.abs(mVelocity.z) > 0) {
            mState = PLAYERSTATE.running;
        } else {
            mState = PLAYERSTATE.idle;
        }
    }

    public void rotate(Vector2 rotation) {
        mCamera.rotate(mCamera.getRight(), rotation.y);
        mCamera.rotate(FPSCamera.UP, rotation.x);
    }

    public Quaternion getRotation() {
        Quaternion q = new Quaternion();
        mCamera.view.cpy().inv().getRotation(q);
        return q;
    }

    public boolean isGrounded() {
        return mOnGround;
    }

}
