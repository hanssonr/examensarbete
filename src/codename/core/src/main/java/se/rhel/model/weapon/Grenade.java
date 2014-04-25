package se.rhel.model.weapon;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import se.rhel.model.component.*;
import se.rhel.model.physics.BulletWorld;

/**
 * Created by rkh on 2014-03-31.
 */
public class Grenade extends GameObject implements IExplodable {

    private int mDamage = 50;
    private float mExplosionRadius = 10f;
    private float size = 0.2f;
    private float explosionTime = 3f;
    private boolean isAlive = true;

    private IPhysics mPhysicsComponent;

    public Grenade(BulletWorld world, Vector3 position, Vector3 direction) {
        super();

        mPhysicsComponent = createPhysicsComponent(world);
        mTransform.getTransformation().setTranslation(position.add(Vector3.Y).add(direction.cpy().scl(0.5f)));

        createPhysicBody();
        mPhysicsComponent.getBody().applyImpulse(direction.cpy().scl(20f), new Vector3(0.1f, 0.05f, 0.1f).scl(0.1f));
    }

    public void createPhysicBody() {
        Vector3 inertia = new Vector3();
        btCollisionShape shape = new btCapsuleShape(size, size);
        shape.calculateLocalInertia(1f, inertia);
        btDefaultMotionState motionstate = new btDefaultMotionState(mTransform.getTransformation());
        btRigidBodyConstructionInfo info = new btRigidBodyConstructionInfo(1f, motionstate, shape, inertia);
        info.setFriction(5f);
        info.setRestitution(0.1f);

        mPhysicsComponent.createPhysicsBody(shape, info, motionstate, this);
    }

    public void update(float delta) {

        if(isAlive) {
            explosionTime -= delta;

            if (explosionTime <= 0) {
                isAlive = false;
            }
        }

        mPhysicsComponent.getBody().applyDamping(10f);
        mTransform.getTransformation().set(mPhysicsComponent.getBody().getCenterOfMassTransform());
    }

    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public float getExplosionRadius() {
        return mExplosionRadius;
    }

    @Override
    public int getExplosionDamage() {
        return mDamage;
    }
}
