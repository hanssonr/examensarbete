package se.rhel.model.component;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Group: Logic
 */
public class MoveComponent implements IMovable, IComponent {

    private Vector2 mRotation = new Vector2();
    private Vector3 mDirection = new Vector3(0,0,-1);
    private Vector3 mVelocity = new Vector3();

    private float mMovespeed = 0;

    public MoveComponent(float movespeed) {
        mMovespeed = movespeed;
    }

    public void move(Vector3 direction) {
//        mVelocity.set(0, 0, 0);
//        mVelocity.add(getForward().scl(direction.z * mMovespeed));
//        mVelocity.add(getForward().crs(Vector3.Y).scl(direction.x * mMovespeed));
    }

//    public void rotate(Vector2 rotation) {
//        mRotation.x += rotation.x;
//        if(mRotation.x > 360) mRotation.x -= 360;
//        if(mRotation.x < 0) mRotation.x += 360;
//
//        mRotation.y += rotation.y;
//        if(mRotation.y > 60) mRotation.y = 60;
//        if(mRotation.y < -60) mRotation.y = -60;
//
//        calculateDirection();
//    }

//    @Override
//    public Vector3 getVelocity() {
//        return mVelocity;
//    }
//
//    private void calculateDirection() {
//        Quaternion q = new Quaternion().idt();
//        q.setEulerAngles(getRotation().x, getRotation().y, 0);
//
//        mDirection.set(q.transform(new Vector3(0,0,-1)));
//    }

//    public Vector2 getRotation() {
//        return mRotation;
//    }

//    private Vector3 getForward() {
//        return new Vector3(mDirection.x, 0, mDirection.z).nor();
//    }

//    public Vector3 getDirection() {
//        return mDirection.cpy().nor();
//    }

//    //Network only?
//    public void rotateTo(Vector2 rotation) {
//        mRotation.set(rotation);
//
//        calculateDirection();
//    }

//    public void setPosition(Vector3 val) {
////        getBody().translate(val);
////        Matrix4 m = new Matrix4(val, getBody().getOrientation(), new Vector3(1f, 1f, 1f));
////        getBody().proceedToTransform(m);
//    }

//    public void setPositionAndRotation(Vector3 position, float rotX) {
////        Matrix4 m = new Matrix4();
////        m.rotate(Vector3.Y, rotX);
////        m.setTranslation(position);
////        getBody().proceedToTransform(m);
//    }
}

