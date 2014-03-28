package se.rhel.model.entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
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

        if(mHealth <= 0) {
            mHealth = 0;
            mAlive = false;
        }
    }

    public void destroy() {
        getWorld().getCollisionWorld().removeCollisionObject(getBody());
    }
}
