package se.rhel.model.entity;

import com.badlogic.gdx.math.Matrix4;

/**
 * Created by rkh on 2014-03-28.
 */
public interface IEntity {

    public Matrix4 getTransformation();
    public boolean isAlive();
    public void update(float delta);

}
