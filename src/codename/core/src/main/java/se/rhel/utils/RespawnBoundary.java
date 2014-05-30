package se.rhel.utils;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by rkh on 2014-05-30.
 */
public class RespawnBoundary {

    Vector3 mBr, mBl, mTr, mTl;

    public RespawnBoundary(Vector3 tl, Vector3 tr, Vector3 br, Vector3 bl) {
        mTl = tl;
        mTr = tr;
        mBr = br;
        mBl = bl;
    }

    public Vector3 getRandomPosInBoundary() {
        float max_x = Math.max(mTl.x, mTr.x);
        float min_x = Math.min(mTl.x, mTr.x);

        float max_z = Math.max(mTl.z, mBl.z);
        float min_z = Math.min(mTl.z, mBl.z);

        float x = (float) (Math.random() * (max_x - min_x) + min_x);
        float z = (float) (Math.random() * (max_z - min_z) + min_z);
        float y = (mBr.y + mBl.y + mTr.y + mTl.y)/4f;

        return new Vector3(x,y,z);
    }
}
