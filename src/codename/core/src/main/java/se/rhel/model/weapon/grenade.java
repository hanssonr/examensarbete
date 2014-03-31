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
    private float explosionTime = 4f;
    private boolean isAlive = true;

    //postition
    private Vector3 mPosition = new Vector3();
    private Vector3 mDirection = new Vector3();

    public Grenade(BulletWorld world, Vector3 position, Vector3 direction) {
        super(world);

        mPosition = position;
        mDirection = direction;

        getTransformation().setTranslation(position.add(Vector3.Y).add(direction));
        createPhysicBody();

        getBody().applyCentralImpulse(direction.scl(10f));
    }

    public void createPhysicBody() {
        btCollisionShape shape = new btSphereShape(size);
        btRigidBodyConstructionInfo info = new btRigidBodyConstructionInfo(.5f, null, shape, Vector3.Zero);
        info.setFriction(100f);
        btDefaultMotionState motionstate = new btDefaultMotionState(getTransformation());

        super.createPhysicBody(shape, info, motionstate, this);
    }

    public void update(float delta) {
        if(isAlive) {
            explosionTime -= delta;

            if (explosionTime <= 0) {
                isAlive = false;

                EventHandler.events.notify(new ModelEvent(EventType.EXPLOSION, this));
            }
        }

        mTransformation.set(getBody().getCenterOfMassTransform());
    }

    public boolean isAlive() {
        return isAlive;
    }
}
