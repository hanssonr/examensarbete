package se.rhel.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import se.rhel.model.entity.DynamicEntity;
import se.rhel.res.Resources;
import se.rhel.view.BulletHoleRenderer;


/**
 * Group: Logic
 */
public class Player extends DynamicEntity {

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

    private BulletWorld mWorld;
    private btRigidBody mBody;

    private float mDeltaShoot;
    private float mGravity = 0f;

    //Actions
    private boolean mIsJumping = false;
    private boolean mOnGround = false;

    //RayCasts
    private ClosestRayResultCallback rayTestCB;
    public boolean mHasShot = false;
    public Vector3 from = new Vector3();
    public Vector3 to = new Vector3();
    private Vector3 fromGround = new Vector3();
    private Vector3 toGround = new Vector3();


    //Weapon
    private ModelInstance mWeapon;
    private Matrix4 mWeaponWorld = new Matrix4().idt();
    private Vector3 mWeaponOffset = new Vector3();

    private Vector3 mVelocity = new Vector3();
    private static Vector2 mPlayersize = new Vector2(0.6f, 1.5f);

    public Player(Vector3 position, BulletWorld world) {
        super(7f);
        mWorld = world;

        getTransformation().setTranslation(position);
        createPyshicsBody();
        mWeapon = new ModelInstance(Resources.INSTANCE.fpsWeaponModel);
        mWorld.fpsModel = mWeapon;

        mState = PLAYERSTATE.idle;
    }

    private void createPyshicsBody() {
        btCollisionShape playerShape = new btCapsuleShape(mPlayersize.x, mPlayersize.y);
        btRigidBodyConstructionInfo playerInfo = new btRigidBodyConstructionInfo(5f, null, playerShape, Vector3.Zero);
        btDefaultMotionState playerMotionState = new btDefaultMotionState(getTransformation());

        mBody = new btRigidBody(playerInfo);
        mBody.setMotionState(playerMotionState);
        mBody.setGravity(Vector3.Zero);

        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);

        mWorld.addToWorld(playerShape,
                playerInfo,
                playerMotionState,
                mBody);
    }

    public void update(float delta) {
        mBody.setGravity(Vector3.Zero);
        mTransformation.set(mBody.getCenterOfMassTransform());

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
        mWeapon.transform.set(mWeaponWorld);
    }

    public void shoot() {

        if(mHasShot)
            return;

        mHasShot = true;

        // We want a ray from middle of screen as basis of hit detection
        Ray ray = mCamera.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        ray = ray.cpy();

        // For debugging purposes
        from.set(ray.origin);
        to.set(ray.direction).scl(50f).add(from);

        rayTestCB.setCollisionObject(null);
        rayTestCB.setClosestHitFraction(1f);
        rayTestCB.getRayFromWorld().setValue(from.x, from.y, from.z);
        rayTestCB.getRayToWorld().setValue(to.x, to.y, to.z);

        mWorld.getCollisionWorld().rayTest(from, to, rayTestCB);

        if (rayTestCB.hasHit()) {
            to.set(new Vector3(rayTestCB.getHitPointWorld().getX(), rayTestCB.getHitPointWorld().getY(), rayTestCB.getHitPointWorld().getZ()));
            final btCollisionObject obj = rayTestCB.getCollisionObject();
            if (!obj.isStaticOrKinematicObject()) {
                final btRigidBody body = (btRigidBody)(obj);
                body.activate();
                body.applyCentralImpulse(Vector3.tmp2.set(ray.direction).scl(20f));
            }

            if(obj.isStaticOrKinematicObject()) {
                btVector3 v = rayTestCB.getHitPointWorld();
                btVector3 t = rayTestCB.getHitNormalWorld();
                BulletHoleRenderer.addBullethole(new Vector3(v.getX(), v.getY(), v.getZ()), new Vector3(t.getX(), t.getY(), t.getZ()).nor());
                v.dispose();
                t.dispose();
            }
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

        mWorld.getCollisionWorld().rayTest(fromGround, toGround, cb);

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
        mBody.activate(true);

        mVelocity.set(0,0,0);
        mVelocity.add(mCamera.getForward().scl(direction.z * mMovespeed));
        mVelocity.add(mCamera.getRight().scl(direction.x * mMovespeed));

        mVelocity.y = mGravity;

        //Change gravity if jumping
        if (mIsJumping) {
            mVelocity.y = JUMP_HEIGHT;
            mIsJumping = false;
        }

        mBody.setLinearVelocity(mVelocity);

        if(Math.abs(mVelocity.x) > 0 || Math.abs(mVelocity.z) > 0) {
            mState = PLAYERSTATE.running;
        } else {
            mState = PLAYERSTATE.idle;
        }
    }

    public void rotate(Vector3 axis, float angle) {
        Quaternion quat = new Quaternion().setFromAxis(axis, angle);
        mBody.setCenterOfMassTransform(mBody.getWorldTransform().rotate(quat));
    }

    public void rotate(Vector2 rotation) {
        mCamera.rotate(mCamera.getRight(), rotation.y);
        mCamera.rotate(FPSCamera.UP, rotation.x);
        //mPlayer.rotate(FPSCamera.UP, rotation.x);
    }

    public Vector3 getPosition() {
        return mBody.getCenterOfMassPosition();
    }

    public Vector3 getVelocity() {
        return mBody.getLinearVelocity().cpy();
    }

    public boolean isGrounded() {
        return mOnGround;
    }

    public float getMoveSpeed() {
        return mMovespeed;
    }

}





















