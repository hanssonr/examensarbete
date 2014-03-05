package se.rhel.model.client;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.client.Client;
import se.rhel.model.BaseModel;
import se.rhel.model.BulletWorld;
import se.rhel.model.FPSCamera;
import se.rhel.model.Player;

public class ClientWorldModel implements BaseModel {

    private FPSCamera mCamera;
    private Player mLocalPlayer;
    private BulletWorld mBulletWorld;

    private Client mClient;
    private Array<Player> mPlayers;

    public ClientWorldModel(Client client) {
        mClient = client;
        create();
    }

    @Override
    public void create() {
        mBulletWorld = new BulletWorld();
        mCamera = new FPSCamera(75, 0.1f, 1000f);
        mLocalPlayer = new Player(new Vector3(0, 10, 0), mBulletWorld);
        mLocalPlayer.attachCamera(mCamera);
        mPlayers.add(mLocalPlayer);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float delta) {
        mBulletWorld.update(delta);
        mLocalPlayer.update(delta);
    }

    public BulletWorld getBulletWorld() { return mBulletWorld; }
    public Player getPlayer() {
        return mLocalPlayer;
    }
    public FPSCamera getCamera() { return mCamera; }

}