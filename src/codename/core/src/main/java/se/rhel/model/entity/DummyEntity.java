package se.rhel.model.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import se.rhel.model.component.*;
import se.rhel.model.physics.BulletWorld;

import java.util.Random;

/**
 * Group: Logic
 *
 * Created by rkh on 2014-03-24.
 */
public class DummyEntity extends GameObject implements IPlayer {

    private Vector2 mSize;
    float yRot = 0;
    float shootTimer = 0;
    float moveTimer = 0;

    //RayCasts
    private ClosestRayResultCallback rayTestCB;
    private Vector3 fromGround = new Vector3();
    private Vector3 toGround = new Vector3();
    private boolean mOnGround = false;
    private float mGravity = 0f;
    private final float GRAVITY_POWER = 15f;


    protected IPhysics mPhysicsComponent;
    protected IDamageable mDamageComponent;
    protected IActionable mActionComponent;

    public DummyEntity(BulletWorld world, float radius, float height, int maxHealth, float movespeed, Vector3 position) {
        super();
        mSize = new Vector2(radius, height);

        mPhysicsComponent = createPhysicsComponent(world);
        mDamageComponent = createDamageableComponent(maxHealth);
        mActionComponent = createActionComponent();

        getTransformation().setTranslation(position);
        createPhysicBody();
    }

    public void createPhysicBody() {
        btCollisionShape shape = new btCapsuleShape(mSize.x, mSize.y);
        btRigidBodyConstructionInfo info = new btRigidBodyConstructionInfo(5f, null, shape, Vector3.Zero);
        info.setFriction(0f);
        btDefaultMotionState motionstate = new btDefaultMotionState(getTransformation());

        mPhysicsComponent.createPhysicsBody(shape, info, motionstate, this);
    }

    @Override
    public boolean isAlive() {
        return mDamageComponent.isAlive();
    }

    public void update(float delta) {
        checkOnGround();
        calculateGravity(delta);

        mActionComponent.update(delta);
        yRot += delta;
        shootTimer += delta;

        if(shootTimer > 5f) {
            mActionComponent.shoot();
            shootTimer = 0f;
        }

        moveTimer += delta;
        if(moveTimer > 4f) {
            moveTimer = 0;
            Random rand = new Random();
            mTransform.rotateTo(new Vector2(rand.nextInt(359), 0));
        }

        Vector3 vel = mTransform.getDirection().cpy();
        vel.y = mGravity;
        mPhysicsComponent.getBody().activate(true);
        mPhysicsComponent.getBody().setLinearVelocity(vel.scl(7f));

        getTransformation().setTranslation(mPhysicsComponent.getBody().getCenterOfMassPosition());
    }

    private void checkOnGround() {
        mOnGround = false;
        fromGround.set(mTransform.getPosition());
        toGround.set(new Vector3(fromGround.x, fromGround.y - mSize.y, fromGround.z));

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

    private void calculateGravity(float delta) {
        if(!mOnGround) {
            mGravity -= GRAVITY_POWER * delta;
        } else {
            if (mGravity < -10) mGravity = -10;
            mGravity += GRAVITY_POWER * delta;
            if (mGravity > 0) mGravity = 0;
        }
    }
}
