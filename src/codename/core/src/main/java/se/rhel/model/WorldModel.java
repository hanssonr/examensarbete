package se.rhel.model;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import se.rhel.res.Resources;


public class WorldModel {

    private FPSCamera mCamera;
    private Player mPlayer;

    private ModelInstance mBoxInstance;
    private CameraInputController mCamController;

    public WorldModel() {

        mPlayer = new Player(new Vector3(0,0,5));
        mCamera = new FPSCamera(mPlayer, 67, 0.1f, 300f);

        mBoxInstance = new ModelInstance(Bodybuilder.INSTANCE.createBox(1f,1f,1f), 0f, 0f, 0f);
        Resources.INSTANCE.modelInstanceArray.add(mBoxInstance);
    }

    public void update(float delta) {
        mPlayer.update(delta);
        mCamera.update();

        mBoxInstance.transform.rotate(new Quaternion().setEulerAngles(1, 0, 0));
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public FPSCamera getCamera() {
        return mCamera;
    }
}
