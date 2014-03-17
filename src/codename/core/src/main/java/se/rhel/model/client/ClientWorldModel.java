package se.rhel.model.client;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.client.Client;
import se.rhel.model.*;
import se.rhel.network.PlayerMovePacket;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.network.packet.PlayerPacket;
import se.rhel.network.packet.RequestInitialStatePacket;
import se.rhel.observer.ClientControllerListener;
import se.rhel.observer.ClientListener;
import se.rhel.packet.Packet;
import se.rhel.packet.TestPacket;
import se.rhel.util.Log;


/**
 * Group: Mixed
 */
public class ClientWorldModel implements BaseModel, ClientListener, ClientControllerListener {

    private FPSCamera mCamera;
    private Player mLocalPlayer;
    private BulletWorld mBulletWorld;
    private boolean mIsLocal;

    private Client mClient;
    private Array<ExternalPlayer> mPlayers;

    public static ClientWorldModel newNetworkWorld(Client client) {
        return new ClientWorldModel(client);
    }

    public static ClientWorldModel newLocalWorld() {
        return new ClientWorldModel();
    }

    /**
     * Used for non-network play
     */
    private ClientWorldModel() {
        mBulletWorld = new BulletWorld();
        mIsLocal = true;
        create();
    }

    /**
     * Used for networked play
     * @param client
     */
    private ClientWorldModel(Client client) {
        mBulletWorld = new BulletWorld();
        mPlayers = new Array<>();
        mIsLocal = false;
        mClient = client;
        mClient.addListener(this);
        mClient.sendTcp(new RequestInitialStatePacket(mClient.getId()));
        create();
    }

    @Override
    public void create() {
        mCamera = new FPSCamera(75, 0.1f, 1000f);
        mLocalPlayer = new Player(new Vector3(0, 10, 0), mBulletWorld);
        mLocalPlayer.attachCamera(mCamera);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float delta) {
        mBulletWorld.update(delta);
        mLocalPlayer.update(delta);

        // Do client only updates
        if(!mIsLocal) {
            for (ExternalPlayer externalPlayer : mPlayers) {
                externalPlayer.update(delta);
            }
        }
    }

    public BulletWorld getBulletWorld() { return mBulletWorld; }
    public Player getPlayer() {
        return mLocalPlayer;
    }
    public FPSCamera getCamera() { return mCamera; }
    public Array<ExternalPlayer> getPlayers() { return mPlayers; }
    public ExternalPlayer getExternalPlayer(int id) {
        for (ExternalPlayer externalPlayer : mPlayers) {
            if(externalPlayer.getClientId() == id) {
                return externalPlayer;
            }
        }
        return null;
    }

    @Override
    public void connected() {

    }

    @Override
    public void disconnected() {

    }

    @Override
    public void received(Object obj, byte[] data) {
        Log.debug("ClientWorldModel", "RECEIVED: " + obj);
        if (obj instanceof PlayerPacket) {
            Log.debug("ClientWorldModel", "Player_Join packet - Player can be viewed on client!!");
            PlayerPacket pp = new PlayerPacket(data);
            ExternalPlayer ep = new ExternalPlayer(pp.clientId, new Vector3(pp.x, pp.y, pp.z), mBulletWorld);
            mPlayers.add(ep);
        }
        else if(obj instanceof TestPacket) {
            Log.debug("ClientWorldModel", "TestPacket received");
            TestPacket tp = new TestPacket(data);
            System.out.println();
        }
        else if(obj instanceof PlayerMovePacket) {
            // An external player have moved and should be updated, accordingly
            PlayerMovePacket pmp = new PlayerMovePacket(data);

            ExternalPlayer ep = getExternalPlayer(pmp.clientId);
            if(ep == null) {
                return;
            }

            // Set the position
            ep.setPosition(pmp.x, pmp.y, pmp.z);
        }
    }

    @Override
    public void sendTCP(Packet packet) {
        Log.info("ClientWorldModel", "Input received from Controller - TCP send");
        // mClient.sendTcp();
    }

    @Override
    public void sendUDP(Packet packet) {
        Log.info("ClientWorldModel", "Input received from Controller - UDP send");
        // mClient.sendUdpFromServer();
    }
}