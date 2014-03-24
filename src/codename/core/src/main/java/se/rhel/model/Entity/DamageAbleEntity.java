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
    private ModelInstance mInstance;

    protected DamageAbleEntity(BulletWorld world, ModelInstance instance, int maxHealth, float movespeed) {
        super(world, movespeed);

        mInstance = instance;
        mHealth = maxHealth;
    }

    public boolean isAlive() {
        return mAlive;
    }

    public int getHealth() {
        return mHealth;
    }

    public ModelInstance getInstance() {
        return mInstance;
    }

    public void damageEntity(int amount) {
        mHealth -= amount;

        if(mHealth <= 0) mAlive = false;
    }

    public void destroy() {
        getWorld().removeInstance(mInstance);
        getWorld().getCollisionWorld().removeCollisionObject(getBody());
    }
}
