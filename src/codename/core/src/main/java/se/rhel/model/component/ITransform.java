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
    public Vector2 getRotation();
    public void rotateBy(Vector2 amount);
    public void rotateTo(Vector2 rotation);
    public void rotateAndTranslate(Vector2 rotation, Vector3 position);
}
