package se.rhel.model.component;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by rkh on 2014-04-25.
 */
public class TransformComponent implements ITransform, IComponent {

    private Vector2 mRotation = new Vector2();
    private Vector3 mDirection = new Vector3(0,0,-1);
    private Matrix4 mTransformation;

    public TransformComponent() {
        mTransformation = new Matrix4().idt();
    }

    public void rotate(Vector2 rotation) {
        mRotation.x += rotation.x;
        if(mRotation.x > 360) mRotation.x -= 360;
        if(mRotation.x < 0) mRotation.x += 360;

        mRotation.y += rotation.y;
        if(mRotation.y > 60) mRotation.y = 60;
        if(mRotation.y < -60) mRotation.y = -60;

        mTransformation.setToRotation(Vector3.Y, mRotation.x);
        calculateDirection();
    }

    public void rotateTo(Vector2 rotation) {
        mTransformation.setToRotation(Vector3.Y, rotation.x);
        mRotation.set(rotation);
        calculateDirection();
    }

    private void calculateDirection() {
        Quaternion q = new Quaternion().idt();
        q.setEulerAngles(mRotation.x, mRotation.y, 0);
        mDirection.set(q.transform(new Vector3(0,0,-1)));
    }

    public void rotateAndTranslate(Vector2 rotation, Vector3 position) {
        mRotation.set(rotation);
        Matrix4 m = new Matrix4().idt();
        m.setToRotation(Vector3.Y.cpy(), mRotation.x);
        m.setTranslation(position);
        mTransformation.set(m);

        calculateDirection();
    }

    @Override
    public Matrix4 getTransformation() {
        return mTransformation;
    }

    @Override
    public Vector3 getPosition() {
        return mTransformation.getTranslation(new Vector3());
    }

    @Override
    public Vector3 getDirection() {
        return mDirection;
    }

    public Vector2 getRotation() {
        return mRotation;
    }
}
