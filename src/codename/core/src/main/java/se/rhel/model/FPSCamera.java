package se.rhel.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


/**
 * Group: Logic
 */
public class FPSCamera extends PerspectiveCamera {

    public static final Vector3 UP = new Vector3(0,1,0);
    private final Vector3 mOffset = new Vector3(0, 1f, 0);

    /**
     *
     * @param fov Field of View
     * @param near Nearplane
     * @param far Farplane
     */
    public FPSCamera(float fov, float near, float far) {
        this.fieldOfView = fov;
        this.near = near;
        this.far = far;
        this.viewportWidth = Gdx.graphics.getWidth();
        this.viewportHeight = Gdx.graphics.getHeight();
        update();
    }

    public Vector3 getOffset() {
        return mOffset;
    }

    public Vector3 getRight() {
        Vector3 ret = new Vector3();
        ret.set(direction.cpy().crs(UP));
        return ret.nor();
    }

    public Vector3 getForward() {
        Vector3 ret = new Vector3();
        ret.set(direction.x, 0, direction.z);
        return ret.nor();
    }

    public void rotate(Vector2 amount) {
        rotate(getRight(), amount.y);
        rotate(FPSCamera.UP, amount.x);
    }

}
