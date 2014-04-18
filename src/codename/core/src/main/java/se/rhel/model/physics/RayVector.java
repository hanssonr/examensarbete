package se.rhel.model.physics;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by rkh on 2014-04-17.
 */
public class RayVector {

    private Vector3 mFrom;
    private Vector3 mTo;

    public RayVector(Vector3 from, Vector3 to) {
        mFrom = from;
        mTo = to;
    }

    public Vector3 getFrom() {
        return mFrom;
    }

    public Vector3 getTo() {
        return mTo;
    }

    public static double getDistance(Vector3 from, Vector3 to) {
        return Math.abs(Math.sqrt((from.x-to.x)*(from.x-to.x) +
                         (from.y-to.y)*(from.y-to.y) +
                         (from.z-to.z)*(from.z-to.z)));
    }
}
