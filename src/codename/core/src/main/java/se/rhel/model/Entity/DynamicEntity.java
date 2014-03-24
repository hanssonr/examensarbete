package se.rhel.model.entity;


import se.rhel.model.physics.BulletWorld;

/**
 * Group: Logic
 */
public abstract class DynamicEntity extends GameObject {

    protected float mMovespeed = 0;

    protected DynamicEntity(BulletWorld world, float movespeed) {
        super(world);
        setMovespeed(movespeed);
    }

    public void setMovespeed(float movespeed) {
        mMovespeed = movespeed;
    }
}

