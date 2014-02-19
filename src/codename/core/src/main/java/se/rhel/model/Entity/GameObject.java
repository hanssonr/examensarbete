package se.rhel.model.Entity;


import com.badlogic.gdx.math.Vector3;

public class GameObject {

    protected Vector3 mPosition = new Vector3();
    protected Vector3 mRotation = new Vector3();

    public GameObject(Vector3 position, Vector3 rotation) {
        setPosition(position);
        setRotation(rotation);
    }

    public void setPosition(Vector3 position) {
        mPosition.set(position);
    }

    public void setRotation(Vector3 rotation) {
        mRotation.set(rotation);
    }

    public GameObject() {
        this(new Vector3(0,0,0), new Vector3(0,0,0));
    }

    public Vector3 getPosition() {
        return mPosition;
    }

    public Vector3 getRotation() {
        return mRotation;
    }
}