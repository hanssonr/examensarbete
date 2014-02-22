package se.rhel.model.Entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;


public abstract class DynamicEntity extends GameObject {

    protected float mMovespeed = 0;
    protected DynamicEntity(float movespeed) {
        super();

        setMovespeed(movespeed);
    }

    public void move(Vector3 direction) {
        //mTransformation.translate(direction.scl(mMovespeed));
    }

    public void rotate(Vector3 axis, float angle) {
        //mTransformation.rotate(axis, angle);
        //getInstance().transform.rotate(axis, angle);
    }

    public void setMovespeed(float movespeed) {
        mMovespeed = movespeed;
    }
}

