package se.rhel.model.client;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.client.Client;
import se.rhel.model.*;
import se.rhel.observer.ClientListener;
import se.rhel.packet.Packet;
import se.rhel.packet.PlayerJoinPacket;
import se.rhel.packet.RequestInitialStatePacket;

public class ClientWorldModel implements BaseModel, ClientListener {

    private FPSCamera mCamera;
    private Player mLocalPlayer;
    private BulletWorld mBulletWorld;

    private Client mClient;
    private Array<ExternalPlayer> mPlayers;

    public ClientWorldModel(Client client) {
        mBulletWorld = new BulletWorld();
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
        mPlayers = new Array<>();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float delta) {
        mBulletWorld.update(delta);
        mLocalPlayer.update(delta);

        for (ExternalPlayer externalPlayer : mPlayers) {
            externalPlayer.update(delta);
        }
    }

    public BulletWorld getBulletWorld() { return mBulletWorld; }
    public Player getPlayer() {
        return mLocalPlayer;
    }
    public FPSCamera getCamera() { return mCamera; }
    public Array<ExternalPlayer> getPlayers() { return mPlayers; }

    @Override
    public void connected() {

    }

    @Override
    public void disconnected() {

    }

    @Override
    public void received(Packet packet) {
        switch(Packet.lookupPacket(packet.getPacketId())) {
            case PLAYER_JOIN:
                System.out.println(">   ClientWorldModel: Player_Join packet - Player can be viewed on client!!");
                ExternalPlayer ep = new ExternalPlayer(new Vector3(1f, 10f, 0f), mBulletWorld);
                mPlayers.add(ep);
                break;
            default:
                System.out.println(">   ClientWorldModel: Unhandled packet");
                break;
        }
    }
}