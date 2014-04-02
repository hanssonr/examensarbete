package se.rhel.model.weapon;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.model.entity.GameObject;
import se.rhel.model.physics.BulletWorld;

/**
 * Created by rkh on 2014-03-31.
 */
public class Grenade extends GameObject  {

    private int damage = 50;
    private float size = 0.2f;
    private float explosionTime = 3f;
    private boolean isAlive = true;

    //postition
    private Vector3 mPosition = new Vector3();
    private Vector3 mDirection = new Vector3();

    public Grenade(BulletWorld world, Vector3 position, Vector3 direction) {
        super(world);

        mPosition = position;
        mDirection = direction;

        getTransformation().setTranslation(position.add(Vector3.Y).add(direction));
    }

    private void throwMe() {
        getBody().applyImpulse(mDirection.scl(20f), new Vector3(0.1f, 0.05f, 0.1f).scl(0.1f));
    }

    public void createPhysicBody() {
        Vector3 inertia = new Vector3();
        btCollisionShape shape = new btCapsuleShape(size, size);
        shape.calculateLocalInertia(1f, inertia);
        btDefaultMotionState motionstate = new btDefaultMotionState(getTransformation());
        btRigidBodyConstructionInfo info = new btRigidBodyConstructionInfo(1f, motionstate, shape, inertia);
        info.setFriction(5f);
        info.setRestitution(0.1f);

        super.createPhysicBody(shape, info, motionstate, this);

        throwMe();
    }

    public void update(float delta) {

        if(isAlive) {
            explosionTime -= delta;

            if (explosionTime <= 0) {
                isAlive = false;

                EventHandler.events.notify(new ModelEvent(EventType.EXPLOSION, this));
            }
        }

        getBody().applyDamping(10f);
        mTransformation.set(getBody().getCenterOfMassTransform());
    }

    public boolean isAlive() {
        return isAlive;
    }
}
