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
 * Created by rkh on 2014-05-02.
 */
public class ControlledPlayer extends GameObject implements IPlayer {
    private Vector2 mSize;

    protected IPhysics mPhysicsComponent;
    protected IDamageable mDamageComponent;
    protected IActionable mActionComponent;
    protected IGravity mGravityComponent;

    public ControlledPlayer(BulletWorld world, Vector3 position) {
        super();
        mSize = new Vector2(0.6f, 1.2f);

        mPhysicsComponent = createPhysicsComponent(world);
        mDamageComponent = createDamageableComponent(100);
        mActionComponent = createActionComponent();
        mGravityComponent = createGravityComponent(world.getCollisionWorld(), mPhysicsComponent, 15f);

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
        super.update(delta);

        mPhysicsComponent.getBody().activate(true);
        mPhysicsComponent.getBody().setCenterOfMassTransform(getTransformation());
    }

    public Vector3 calculateShootDirection() {
        double hitchance = Math.random();
        Vector3 dir = getDirection().cpy();

        if(hitchance < 0.7d) {
            dir.x += 0.2f;
            dir.z += 0.2f;
        }
        return dir;
    }

    @Override
    public Vector3 getShootPosition() {
        return getPosition().add(new Vector3(0, 0.7f, 0));
    }

    public void respawn(Vector3 pos) {
        mTransform.getTransformation().setToTranslation(pos);
        mPhysicsComponent.getBody().setWorldTransform(getTransformation());
        mDamageComponent.reset();
    }
}
