package se.rhel.model.Entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;


public abstract class DynamicEntity extends GameObject {

    protected float mMovespeed = 0;

    protected DynamicEntity(Vector3 position, ModelInstance instance, float movespeed) {
        super(position, instance);

        setMovespeed(movespeed);
    }

    public void move(Vector3 direction) {
        getInstance().transform.setTranslation(getPosition());
        getPosition().add(direction.scl(mMovespeed));
    }

    public void rotate(Vector3 axis, float angle) {
        getInstance().transform.rotate(axis, angle);
    }

    public void setMovespeed(float movespeed) {
        mMovespeed = movespeed;
    }
}

