package se.rhel.model.component;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by rkh on 2014-04-27.
 */
public interface IGravity {

    public float getGravity();
    public boolean isGrounded();
    public void setGravity(float gravity);
}
