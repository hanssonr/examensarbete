package se.rhel.model.BulletTest;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btQuaternion;
import se.rhel.model.Player;

/**
 * Created by Emil on 2014-02-20.
 * assigned to libgdx-gradle-template in se.rhel.model.BulletTest
 */
public class MotionStateTest extends btMotionState {

    private Vector3 mPos;
    private Player mPlayer;

    public MotionStateTest(Vector3 initial, Player player) {
        mPos = initial;
        mPlayer = player;
    }

    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        worldTrans.getTranslation(mPos);
    }

    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        // btQuaternion q = worldTrans.getRotation(q);
        // mPlayer.setRotation();

        //mPlayer.setPosition(worldTrans.getTranslation(mPlayer.getPosition()));
        // mPlayer.setPosition(worldTrans.getTranslation(mPlayer.getPosition()));
    }
}
