package se.rhel.model.entity;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import se.rhel.model.physics.BulletWorld;

/**
 * Group: Logic
 */
public abstract class DynamicEntity extends GameObject {

    //Rotation
    private float mRotation = 0f;
    private float mUp = 0f;
    private Vector3 mForward = new Vector3(0, 0, -1);
    private Vector3 up = new Vector3(0,1,0);
    private Vector3 mDirection = new Vector3(0,0,-1);

    protected float mMovespeed = 0;

    protected DynamicEntity(BulletWorld world, float movespeed) {
        super(world);
        setMovespeed(movespeed);
    }

    public void setMovespeed(float movespeed) {
        mMovespeed = movespeed;
    }

    public void rotate(Vector2 rotation) {
        mRotation += rotation.x;
        if(mRotation > 360) mRotation -= 360;
        if(mRotation < 0) mRotation += 360;

        mUp += rotation.y;
        if(mUp > 60) mUp = 60;
        if(mUp < -60) mUp = -60;

        mForward.rotate(Vector3.Y, rotation.x);
        up.rotate(Vector3.X, rotation.y);

        mDirection.rotate(Vector3.Y, rotation.x);
        mDirection.rotate(mForward.cpy().crs(Vector3.Y), rotation.y);
    }

    public float getRotation() {
        return mRotation;
    }

    protected Vector3 getForward() {
        return mForward.cpy();
    }

    public Vector3 getDirection() {
        return mDirection.cpy().nor();
    }
}

