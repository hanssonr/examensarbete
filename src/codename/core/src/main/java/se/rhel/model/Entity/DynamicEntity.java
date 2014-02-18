package se.rhel.model.Entity;

import com.badlogic.gdx.math.Vector3;


public class DynamicEntity extends GameObject {

    protected float mMovespeed;
    protected Vector3 mMoveDirection = new Vector3();

    public DynamicEntity(Vector3 position, Vector3 rotation, float movespeed) {
        super(position, rotation);

        setMovespeed(movespeed);
        move(Vector3.Zero);
    }

    public void move(Vector3 direction) {
        mMoveDirection.set(direction);
    }

    protected void update(float delta) {

        Vector3 velocity = mMoveDirection.scl(mMovespeed * delta);

        //System.out.println(velocity);
        Vector3 newpos = getPosition().scl(velocity);

        //setPosition(newpos);

        mMoveDirection.set(Vector3.Zero);
        //System.out.println(getPosition());
    }

    public void setMovespeed(float movespeed) {
        mMovespeed = movespeed;
    }
}
