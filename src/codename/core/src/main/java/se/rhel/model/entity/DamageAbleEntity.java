package se.rhel.model.entity;

import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.model.physics.BulletWorld;


/**
 * Group: Logic
 *
 * Created by rkh on 2014-03-24.
 */
public class DamageAbleEntity extends DynamicEntity {

    protected boolean mAlive = true;
    protected int mHealth;

    protected DamageAbleEntity(BulletWorld world, int maxHealth, float movespeed) {
        super(world, movespeed);

        mHealth = maxHealth;
    }

    public boolean isAlive() {
        return mAlive;
    }

    public int getHealth() {
        return mHealth;
    }

    public void damageEntity(int amount) {
        mHealth -= amount;

        EventHandler.events.notify(new ModelEvent(EventType.DAMAGE, this));
    }

    public void setAlive(boolean b) {
        mAlive = b;
    }
}
