package se.rhel.model;

import com.badlogic.gdx.math.Vector3;
import se.rhel.model.physics.BulletWorld;

/**
 * Group: Logic
 *
 * Created by rkh on 2014-03-21.
 */
public class WorldModel implements BaseModel {

    private FPSCamera mCamera;
    private Player mPlayer;
    private BulletWorld mBulletWorld;

    public WorldModel() {
        mBulletWorld = new BulletWorld();
        create();
    }

    @Override
    public void create() {
        mCamera = new FPSCamera(75, 0.1f, 1000f);
        mPlayer = new Player(new Vector3(0, 10, 0), mBulletWorld);
        mPlayer.attachCamera(mCamera);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float delta) {
        mBulletWorld.update(delta);
        mPlayer.update(delta);
    }

    public BulletWorld getBulletWorld() {
        return mBulletWorld;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public FPSCamera getCamera() {
        return mCamera;
    }
}
