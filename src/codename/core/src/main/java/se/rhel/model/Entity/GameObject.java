package se.rhel.model.Entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import se.rhel.res.Resources;

import java.util.Collection;

public abstract class GameObject {

    protected ModelInstance mInstance;
    protected Matrix4 mTransformation = new Matrix4().idt();

    public GameObject() {

    }


    public Matrix4 getTransformation() { return mTransformation; }
    public ModelInstance getInstance() {
        return mInstance;
    }
}
