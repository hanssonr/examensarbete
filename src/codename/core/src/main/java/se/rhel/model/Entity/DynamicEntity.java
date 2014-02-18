package se.rhel.model.Entity;

import com.badlogic.gdx.math.Vector3;


public class DynamicEntity extends GameObject {

    protected float mMovespeed;
    protected Vector3 mMoveDirection = new Vector3();

    public DynamicEntity(Vector3 position, Vector3 rotation, float movespeed) {
        super(position, rotation);

        setMovespeed(movespeed);
    }

    public void move(Vector3 direction) {
        mMoveDirection.set(direction);
    }

    protected void update(float delta) {
        Vector3 velocity = mMoveDirection.scl(mMovespeed * delta);
        Vector3 newpos = getPosition().add(velocity);
        setPosition(newpos);
    }

    public void setMovespeed(float movespeed) {
        mMovespeed = movespeed;
    }
}
