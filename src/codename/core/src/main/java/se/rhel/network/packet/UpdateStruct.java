package se.rhel.network.packet;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by rkh on 2014-04-29.
 */
public class UpdateStruct {

    public Vector3 mPosition;
    public Vector3 mRotation;
    public int mID;

    public UpdateStruct(int id, Vector3 pos, Vector3 rot) {
        mID = id;
        mPosition = pos;
        mRotation = rot;
    }
}
