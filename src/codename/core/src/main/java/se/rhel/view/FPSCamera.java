package se.rhel.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.sun.java.swing.plaf.motif.resources.motif;
import se.rhel.model.physics.RayVector;


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
}
