package se.rhel.model.Entity;


import com.badlogic.gdx.math.Vector3;

public class GameObject {

    protected Vector3 mPosition;
    protected Vector3 mRotation;

    public GameObject(Vector3 position, Vector3 rotation) {
        mPosition = position;
        mRotation = rotation;
    }

    public GameObject() {
        this(new Vector3(0,0,0), new Vector3(0,0,0));
    }

    public Vector3 getPosition() {
        return mPosition;
    }
}