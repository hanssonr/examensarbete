package se.rhel.model.entity;

import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.model.physics.BulletWorld;

/**
 * Created by Emil on 2014-04-01.
 */
public class ActionEntity extends DamageAbleEntity {

    // Laser
    private boolean mHasShot = false;
    private float mDeltaShoot;

    // Grenades
    private final int MAX_GRENADES = 100;
    private int currentGrenades = 0;

    protected ActionEntity(BulletWorld world, int maxHealth, float movespeed) {
        super(world, maxHealth, movespeed);
    }

    public void update(float delta) {
        // Update shooting for unnecessary drawing / spam shooting
        if(mHasShot) {
            mDeltaShoot += delta;
            if(mDeltaShoot > 0.75f) {
                mHasShot = false;
                mDeltaShoot = 0f;
            }
        }
    }

    public void shoot() {
        // If can shoot
        if(!mHasShot) {
            mHasShot = true;
            EventHandler.events.notify(new ModelEvent(EventType.SHOOT));
        }
    }

    public void grenadeThrow() {
        if(currentGrenades <= MAX_GRENADES) {
            currentGrenades++;
            EventHandler.events.notify(new ModelEvent(EventType.GRENADE, getPosition(), getDirection()));
        }
    }
}
