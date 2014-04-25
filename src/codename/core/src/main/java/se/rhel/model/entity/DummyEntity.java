package se.rhel.model.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import se.rhel.model.component.*;
import se.rhel.model.physics.BulletWorld;

/**
 * Group: Logic
 *
 * Created by rkh on 2014-03-24.
 */
public class DummyEntity extends GameObject implements IPlayer {

    private Vector2 mSize;
    float yRot = 0;
    float shootTimer = 0;

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
        btDefaultMotionState motionstate = new btDefaultMotionState(getTransformation());

        mPhysicsComponent.createPhysicsBody(shape, info, motionstate, this);
    }

    @Override
    public boolean isAlive() {
        return mDamageComponent.isAlive();
    }

    public void update(float delta) {
        mActionComponent.update(delta);
        yRot += delta;
        shootTimer += delta;

        if(shootTimer > 2f) {
            mActionComponent.shoot();
            shootTimer = 0f;
        }

        mTransform.rotate(new Vector2(1, (float)Math.sin(yRot)));
        getTransformation().setTranslation(mPhysicsComponent.getBody().getCenterOfMassPosition());
    }
}
