package se.rhel.model.entity;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by rkh on 2014-03-28.
 */
public interface IPlayer {

    public Matrix4 getTransformation();
    public Vector3 getRotation();
    public Vector3 getPosition();
    public Vector3 getDirection();
    public Vector3 getShootPosition();
    public boolean isAlive();
    public void respawn(Vector3 pos);
    public void update(float delta);
}
