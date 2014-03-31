package se.rhel.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import se.rhel.model.Player;
import se.rhel.res.Resources;

/**
 * Created by rkh on 2014-03-28.
 */
public class PlayerRenderer {

    public enum PLAYERSTATE {
        idle, running
    }

    private PLAYERSTATE mState;
    private FPSCamera mCamera;

    //Instance
    ModelInstance mLaserWeapon = new ModelInstance(Resources.INSTANCE.fpsWeaponModel);

    //Weapon
    private Matrix4 mWeaponWorld = new Matrix4().idt();
    private Vector3 mWeaponOffset = new Vector3();

    //Bobbing
    private float mBobPower = 0.7f;
    private float mBobTimer = 0f;
    private Vector3 mBobVector = new Vector3();

    private Player mPlayer;

    public PlayerRenderer(FPSCamera camera, Player player) {
        mCamera = camera;
        mPlayer = player;
        mState = PLAYERSTATE.idle;
    }

    public void update(float delta) {
        updateCamera(delta);
        updateWeapon();

        if(mPlayer.getVelocity().x != 0 || mPlayer.getVelocity().z != 0) {
            mState = PLAYERSTATE.running;
        } else {
            mState = PLAYERSTATE.idle;
        }
    }

    public void render(ModelBatch batch, Environment env) {
        batch.begin(mCamera);
            Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
            batch.render(mLaserWeapon, env);
        batch.end();
    }

    private void updateWeapon() {
        mWeaponWorld.set(mCamera.view.cpy().inv());
        mWeaponWorld.getTranslation(mWeaponOffset);
        mWeaponOffset.sub(mCamera.up.cpy().scl(0.7f));
        mWeaponOffset.add(mCamera.direction);
        mWeaponOffset.add(mCamera.getRight());

        if(mState == PLAYERSTATE.running) {
            mWeaponOffset.add(mBobVector.cpy().scl(mBobPower));
        }

        mWeaponWorld.setTranslation(mWeaponOffset);
        mLaserWeapon.transform.set(mWeaponWorld);
    }

    private void updateCamera(float delta) {
        mBobTimer +=delta;

        mPlayer.getTransformation().getTranslation(mCamera.position);
        mCamera.position.add(mCamera.getOffset());

        //bobbing
        if(mState == PLAYERSTATE.running) {
            Vector3 dir = mCamera.getRight();
            dir.y = 1f;
            float x = (float)Math.sin(mBobTimer * 10) * 0.05f;
            float y = (float)Math.cos(mBobTimer * 20) * 0.03f;
            float z = (float)Math.sin(mBobTimer * 10) * 0.05f;
            mBobVector.set(x, y, z);
            mCamera.position.add(mBobVector.scl(dir)).cpy().scl(mBobPower);
        }

        mCamera.update();
    }
}
