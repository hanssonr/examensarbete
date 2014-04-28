package se.rhel.model.component;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by rkh on 2014-04-27.
 */
public interface IGravity {

    public void checkOnGround(Vector3 position, float height);
    public void calculateGravity(float delta);
    public float getGravity();
    public boolean isGrounded();
    public void setGravity(float gravity);
}
