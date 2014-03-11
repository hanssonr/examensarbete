package se.rhel.model.entity;

import com.badlogic.gdx.math.Matrix4;

/**
 * Group: Logic
 */
public abstract class GameObject {
    protected Matrix4 mTransformation = new Matrix4().idt();

    public Matrix4 getTransformation() { return mTransformation; }
}
