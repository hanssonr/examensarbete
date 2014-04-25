package se.rhel.model.entity;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import se.rhel.model.component.*;
import se.rhel.model.physics.BulletWorld;

/**
 * Group: Logic
 */
public class Player extends GameObject implements IPlayer{

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
    private float mMovespeed = 7f;

    private IActionable mActionComponent;
    private IDamageable mDamageComponent;
    private IPhysics mPhysicsComponent;

    public Player(Vector3 position, BulletWorld world) {
        super();

        mTransform.getTransformation().setTranslation(position);
        int maxhealth = 100;

        mPhysicsComponent = createPhysicsComponent(world);
        mDamageComponent = createDamageableComponent(maxhealth);
        mActionComponent = createActionComponent();

        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Zero);
        createPyshicsBody();
    }

    private void createPyshicsBody() {
        btCollisionShape shape = new btCapsuleShape(mPlayersize.x, mPlayersize.y);
        btRigidBodyConstructionInfo info = new btRigidBodyConstructionInfo(50f, null, shape, Vector3.Zero);
        info.setFriction(0f);
        btDefaultMotionState motionstate = new btDefaultMotionState(mTransform.getTransformation());

        mPhysicsComponent.createPhysicsBody(shape, info, motionstate, this);
    }

    public void update(float delta) {
        mActionComponent.update(delta);

        if(mDamageComponent.isAlive()) {
            mPhysicsComponent.getBody().activate(true);
            mPhysicsComponent.getBody().setGravity(Vector3.Zero);
            mTransform.getTransformation().set(mPhysicsComponent.getBody().getCenterOfMassTransform());

            checkOnGround();
            calculateGravity(delta);

            Vector3 vel = getVelocity();
            vel.y = mGravity;

            //Change gravity if jumping
            if (mIsJumping) {
                vel.y = JUMP_HEIGHT;
                mIsJumping = false;
            }

            mPhysicsComponent.getBody().setLinearVelocity(vel);

        } else {
            mRespawnTimer += delta;
            if(mRespawnTimer > 5f) {}
        }
    }

    private void checkOnGround() {
        mOnGround = false;
        fromGround.set(mTransform.getPosition());
        toGround.set(new Vector3(fromGround.x, fromGround.y - mPlayersize.y, fromGround.z));

        ClosestRayResultCallback cb = new ClosestRayResultCallback(fromGround, toGround);
        cb.setCollisionObject(null);

        mPhysicsComponent.getPhysicsWorld().getCollisionWorld().rayTest(fromGround, toGround, cb);

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

    public boolean isGrounded() {
        return mOnGround;
    }

    @Override
    public boolean isAlive() {
        return mDamageComponent.isAlive();
    }

    public void move(Vector3 direction) {
        mVelocity.set(0, 0, 0);
        mVelocity.add(getForward().scl(direction.z * mMovespeed));
        mVelocity.add(getForward().crs(Vector3.Y).scl(direction.x * mMovespeed));
    }

    private Vector3 getForward() {
        Vector3 dir = getDirection();
        return new Vector3(dir.x, 0, dir.z);
    }

    public void shoot() {
        mActionComponent.shoot();
    }

    public boolean wantToShoot() {
        return mActionComponent.hasShoot();
    }

    public Vector3 getVelocity() {
        return mVelocity;
    }
}
