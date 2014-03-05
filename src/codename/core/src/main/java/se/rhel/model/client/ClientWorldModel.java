package se.rhel.model.client;

import com.badlogic.gdx.math.Vector3;
import se.rhel.Client;
import se.rhel.Server;
import se.rhel.model.BaseModel;
import se.rhel.model.BulletWorld;
import se.rhel.model.FPSCamera;
import se.rhel.model.Player;

public class ClientWorldModel implements BaseModel {

    private FPSCamera mCamera;
    private Player mPlayer;
    private BulletWorld mBulletWorld;

    private Client mClient;

    public ClientWorldModel(Client client) {
        mClient = client;
        create();
    }

    @Override
    public void create() {
        mBulletWorld = new BulletWorld();
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

    public BulletWorld getBulletWorld() { return mBulletWorld; }
    public Player getPlayer() {
        return mPlayer;
    }
    public FPSCamera getCamera() { return mCamera; }

}