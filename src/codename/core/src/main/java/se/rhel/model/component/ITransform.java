package se.rhel.model.component;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by rkh on 2014-04-25.
 */
public interface ITransform {

    public Matrix4 getTransformation();
    public Vector3 getPosition();
    public Vector3 getDirection();
    public Vector3 getRotation();
    public void rotateBy(Vector3 amount);
    public void rotateTo(Vector3 rotation);
    public void rotateAndTranslate(Vector3 rotation, Vector3 position);
}
