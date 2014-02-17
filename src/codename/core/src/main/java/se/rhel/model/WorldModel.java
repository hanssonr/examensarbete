package se.rhel.model;

import com.badlogic.gdx.math.Vector3;

public class WorldModel {

    private FPSCamera mCamera;
    private Player mPlayer;


    public WorldModel() {
        mPlayer = new Player(new Vector3(0,0,0));
        mCamera = new FPSCamera(mPlayer, 67f, 0.1f, 1000f);
    }

    public void update(float delta) {

        mPlayer.update(delta);
        mCamera.update();
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public FPSCamera getCamera() {
        return mCamera;
    }
}
