package se.rhel.model.entity;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.bullet.collision.*;
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

    private float mRespawnTimer;

    //Actions
    private boolean mIsJumping = false;

    private static Vector2 mPlayersize = new Vector2(0.6f, 1.2f);

    private Vector3 mVelocity = new Vector3();
    private float mMovespeed = 7f;

    private IActionable mActionComponent;
    private IDamageable mDamageComponent;
    private IPhysics mPhysicsComponent;
    private IGravity mGravityComponent;

    public Player(Vector3 position, BulletWorld world) {
        super();

        mTransform.getTransformation().setTranslation(position);
        int maxhealth = 100;

        mDamageComponent = createDamageableComponent(maxhealth);
        mActionComponent = createActionComponent();
        mPhysicsComponent = createPhysicsComponent(world);
        mGravityComponent = createGravityComponent(world.getCollisionWorld(), mPhysicsComponent, 15f);

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
        super.update(delta);

        if(mDamageComponent.isAlive()) {
            mPhysicsComponent.getBody().activate(true);
            mTransform.getTransformation().set(mPhysicsComponent.getBody().getCenterOfMassTransform());

            Vector3 vel = getVelocity();
            vel.y = mGravityComponent.getGravity();

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

    public void jump() {
        if(mGravityComponent.isGrounded()) {
            mGravityComponent.setGravity(JUMP_HEIGHT);
            mIsJumping = true;
        }
    }

    @Override
    public Vector3 getShootPosition() {
        return getPosition().add(new Vector3(0, 0.7f, 0));
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

    @Override
    public void rotateBy(Vector3 amount) {
        Vector3 temp = getRotation().cpy();

        temp.y += amount.y;
        if(temp.y > 60) amount.y -= temp.y - 60;
        if(temp.y < -60) amount.y -= temp.y + 60;

        super.rotateBy(amount);
    }

    public boolean wantToShoot() {
        return mActionComponent.hasShoot();
    }

    public Vector3 getVelocity() {
        return mVelocity;
    }

    public void respawn(Vector3 pos) {
        mTransform.getTransformation().setToTranslation(pos);
        mPhysicsComponent.getBody().setWorldTransform(getTransformation());
        mDamageComponent.reset();
    }
}
