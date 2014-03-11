package se.rhel.model.entity;

/**
 * Group: Logic
 */
public abstract class DynamicEntity extends GameObject {

    protected float mMovespeed = 0;
    protected DynamicEntity(float movespeed) {
        setMovespeed(movespeed);
    }

    public void setMovespeed(float movespeed) {
        mMovespeed = movespeed;
    }
}

