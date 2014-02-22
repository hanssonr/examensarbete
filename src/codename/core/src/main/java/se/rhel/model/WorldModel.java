package se.rhel.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import se.rhel.res.Resources;

public class WorldModel implements BaseModel {

    private FPSCamera mCamera;
    private Player mPlayer;

    private ModelInstance mBoxInstance;
    private CameraInputController mCamController;
    private BulletWorld mBulletWorld;

    public WorldModel() {
        create();
    }

    @Override
    public void create() {
        mBulletWorld = new BulletWorld();
        mPlayer = new Player(new Vector3(0, 0.1f, 4), mBulletWorld);
        mCamera = new FPSCamera(mPlayer, 67f, 0.1f, 300f);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float delta) {
        mPlayer.update(delta);
        mCamera.update();
        mBulletWorld.update(delta);

        // mBoxInstance.transform.rotate(new Quaternion().setEulerAngles(1, 0, 0));
    }

    public BulletWorld getBulletWorld() { return mBulletWorld; }
    public Player getPlayer() {
        return mPlayer;
    }
    public FPSCamera getCamera() { return mCamera; }
}
