package se.rhel.model.Entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import se.rhel.res.Resources;

public abstract class GameObject {

    protected Vector3 mPosition = new Vector3();
    protected ModelInstance mInstance;

    public GameObject(Vector3 position, ModelInstance instance) {
        mPosition = position;
        mInstance = instance;
    }

    public Vector3 getPosition() {
        return mPosition;
    }

    public ModelInstance getInstance() {
        return mInstance;
    }
}
