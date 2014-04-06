package se.rhel.model.entity;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by rkh on 2014-03-28.
 */
public interface IPlayer {

    public Matrix4 getTransformation();
    public Vector2 getRotation();
    public boolean isAlive();
    public void update(float delta);

}
