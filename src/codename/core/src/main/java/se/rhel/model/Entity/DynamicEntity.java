package se.rhel.model.Entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;


public abstract class DynamicEntity extends GameObject {

    protected float mMovespeed = 0;
    protected DynamicEntity(float movespeed) {
        setMovespeed(movespeed);
    }

    public void setMovespeed(float movespeed) {
        mMovespeed = movespeed;
    }
}

