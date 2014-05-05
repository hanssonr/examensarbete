package se.rhel.model.physics;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by rkh on 2014-04-17.
 */
public class RayVector {

    private Vector3 mFrom = new Vector3();
    private Vector3 mTo = new Vector3();

    public RayVector(Vector3 from, Vector3 to) {
        mFrom.set(from);
        mTo.set(to);
    }

    public RayVector(Vector3 position, Vector3 direction, float length) {
        mFrom.set(position);
        mTo.set(mFrom.cpy().add(direction.cpy().scl(length)));
    }

    public RayVector() {
        mFrom = new Vector3();
        mTo = new Vector3();
    }

    public void setFrom(Vector3 from) {
        mFrom.set(from);
    }

    public void setTo(Vector3 to) {
        mTo.set(to);
    }

    public Vector3 getDirection() {
        return getTo().sub(getFrom()).nor();
    }

    public Vector3 getFrom() {
        return mFrom.cpy();
    }

    public Vector3 getTo() {
        return mTo.cpy();
    }

    public static double getDistance(Vector3 from, Vector3 to) {
        return Math.abs(Math.sqrt((from.x-to.x)*(from.x-to.x) +
                         (from.y-to.y)*(from.y-to.y) +
                         (from.z-to.z)*(from.z-to.z)));
    }

    public static void convertToVisual(RayVector ray) {
        Vector3 offset = new Vector3();
        Vector3 right = ray.getDirection().cpy().crs(Vector3.Y).nor();
        Vector3 up = right.cpy().crs(ray.getDirection().nor());

        offset.add(right.cpy().scl(0.3f));
        offset.sub(up.cpy().scl(0.1f));
        //offset.add(ray.getDirection().cpy().scl(2f));
        ray.setFrom(ray.getFrom().add(offset));
    }
}
