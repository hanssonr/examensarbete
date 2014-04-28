package se.rhel.model.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
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
    float shootTimer = 0;

    protected IPhysics mPhysicsComponent;
    protected IDamageable mDamageComponent;
    protected IActionable mActionComponent;
    protected IGravity mGravityComponent;
    private ZombieAIComponent mZombie;

    public DummyEntity(BulletWorld world, float radius, float height, int maxHealth, float movespeed, Vector3 position, IPlayer player) {
        super();
        mSize = new Vector2(radius, height);

        mPhysicsComponent = createPhysicsComponent(world);
        mDamageComponent = createDamageableComponent(maxHealth);
        mActionComponent = createActionComponent();
        mGravityComponent = createGravityComponent(world.getCollisionWorld(), 15f);
        mZombie = new ZombieAIComponent(player, mTransform);


        getTransformation().setTranslation(position);
        createPhysicBody();
    }

    public void createPhysicBody() {
        btCollisionShape shape = new btCapsuleShape(mSize.x, mSize.y);
        btRigidBodyConstructionInfo info = new btRigidBodyConstructionInfo(50f, null, shape, Vector3.Zero);
        info.setFriction(0f);
        btDefaultMotionState motionstate = new btDefaultMotionState(getTransformation());

        mPhysicsComponent.createPhysicsBody(shape, info, motionstate, this);
    }

    @Override
    public boolean isAlive() {
        return mDamageComponent.isAlive();
    }

    public void update(float delta) {
        mGravityComponent.checkOnGround(getPosition(), mSize.y);
        mGravityComponent.calculateGravity(delta);
        mZombie.update(delta);
        mActionComponent.update(delta);

//        shootTimer += delta;
//
//        if(shootTimer > 2f) {
//            mActionComponent.shoot();
//            shootTimer = 0f;
//        }

        Vector3 vel = new Vector3(mZombie.getDirection().scl(2f));
        vel.y = mGravityComponent.getGravity();
        mPhysicsComponent.getBody().activate(true);
        mPhysicsComponent.getBody().setLinearVelocity(vel);

        getTransformation().setTranslation(mPhysicsComponent.getBody().getCenterOfMassPosition());
    }

    public Vector3 calculateShootDirection() {
        double bias = Math.random();
        Vector3 dir = getDirection().cpy();

        if(bias > 0.75d) {
            dir.x += 0.2f;
            dir.z += 0.2f;
        }
        return dir;
    }
}
