package se.rhel.model.component;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by rkh on 2014-04-25.
 */
public class TransformComponent implements ITransform, IComponent {

    private Vector3 mRotation = new Vector3();
    private Vector3 mDirection = new Vector3(0,0,-1);
    private Matrix4 mTransformation;

    public TransformComponent() {
        mTransformation = new Matrix4().idt();
    }

    public void rotateBy(Vector3 amount) {
        mRotation.add(amount);
        mTransformation.rotate(Vector3.Y, mRotation.x);
        calculateDirection();
    }

    public void rotateTo(Vector3 rotation) {
        mTransformation.setToRotation(Vector3.Y, rotation.x);
        mRotation.set(rotation);
        calculateDirection();
    }

    private void calculateDirection() {
        Quaternion q = new Quaternion().idt();
        q.setEulerAngles(mRotation.x, mRotation.y, mRotation.z);
        mDirection.set(q.transform(new Vector3(0,0,-1)));
    }

    public void rotateAndTranslate(Vector3 rotation, Vector3 position) {
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

    public Vector3 getRotation() {
        return mRotation;
    }
}
