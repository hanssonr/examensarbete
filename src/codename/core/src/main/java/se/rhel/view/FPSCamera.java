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

    public RayVector getShootRay() {
        // We want a ray from middle of screen as basis of hit detection
        Ray ray = getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        Vector3 end = new Vector3();
        ray.getEndPoint(end, 75f); TODO:// Should be weapons shootrange
        ray = ray.cpy();

        // For debugging purposes
//        from.set(ray.origin);
//        to.set(ray.direction).scl(75f).add(from);

        RayVector rv = new RayVector(ray.origin, end);

        return rv;
    }

    public RayVector convertToVisualRay(RayVector ray) {
        Vector3 offset = new Vector3();
        offset.add(getRight().scl(0.5f));
        offset.sub(up.cpy().scl(0.2f));
        offset.add(direction.cpy());
        ray.setFrom(ray.getFrom().add(offset));

        return ray;
    }

//    public Vector3[] getVisualRepresentationShoot() {
//        // Visual representation of the starting point bottom right
//        Ray vis = getPickRay(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        vis = vis.cpy();
//
//        // bottom right + 20%
//        Ray vis2 = getPickRay(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * 0.8f);
//        vis2 = vis2.cpy();
//
//        // Middle + 20%
//        Ray vis3 = getPickRay(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() / 2));
//        vis3 = vis3.cpy();
//
//        // Middle
//        Ray vis4 = getPickRay(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() / 2) * 0.9f);
//        vis4 = vis4.cpy();
//
//        Vector3 from = new Vector3();
//        Vector3 from2 = new Vector3();
//        Vector3 to = new Vector3();
//        Vector3 to2 = new Vector3();
//
//        from.set(vis.origin);
//        from2.set(vis2.origin);
//        to.set(vis4.direction).scl(50f).add(from);
//        to2.set(vis3.direction).scl(50f).add(from2);
//
//        from.add(direction.cpy().scl(0.2f));
//        from2.add(direction.cpy().scl(0.2f));
//
//        return new Vector3[] {from, to, from2, to2};
//    }

}
