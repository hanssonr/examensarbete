package se.rhel.model.entity;


import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import se.rhel.model.physics.BulletWorld;

/**
 * Group: Logic
 */
public abstract class DynamicEntity extends GameObject {

    //Rotation
    protected Vector2 mRotation = new Vector2();
    protected Vector3 mDirection = new Vector3(0,0,-1);

    protected float mMovespeed = 0;

    protected DynamicEntity(BulletWorld world, float movespeed) {
        super(world);
        setMovespeed(movespeed);
    }

    public void setMovespeed(float movespeed) {
        mMovespeed = movespeed;
    }

    public void rotate(Vector2 rotation) {
        mRotation.x += rotation.x;
        if(mRotation.x > 360) mRotation.x -= 360;
        if(mRotation.x < 0) mRotation.x += 360;

        mRotation.y += rotation.y;
        if(mRotation.y > 60) mRotation.y = 60;
        if(mRotation.y < -60) mRotation.y = -60;

        /*
        mForward.rotate(Vector3.Y, rotation.x);

        mDirection.rotate(Vector3.Y, rotation.x);
        mDirection.rotate(mForward.cpy().crs(Vector3.Y), rotation.y);*/
        calculateDirection();
    }

    public void calculateDirection() {
        Quaternion q = new Quaternion().idt();
        q.setEulerAngles(getRotation().x, getRotation().y, 0);

        mDirection.set(q.transform(new Vector3(0,0,-1)));
    }

    public Vector2 getRotation() {
        return mRotation;
    }

    protected Vector3 getForward() {
        return new Vector3(mDirection.x, 0, mDirection.z).nor();
    }

    public Vector3 getDirection() {
        return mDirection.cpy().nor();
    }
}

