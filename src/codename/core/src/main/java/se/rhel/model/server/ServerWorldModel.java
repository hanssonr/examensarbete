package se.rhel.model.server;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.Connection;
import se.rhel.Server;
import se.rhel.model.BaseModel;
import se.rhel.model.BulletWorld;
import se.rhel.model.Player;
import se.rhel.observer.Listener;
import se.rhel.packet.ConnectPacket;
import se.rhel.packet.DisconnectPacket;
import se.rhel.packet.Packet;
import se.rhel.packet.PlayerJoinPacket;

public class ServerWorldModel implements BaseModel, Listener {

    private Array<Player> mPlayers = new Array<Player>();
    private BulletWorld mBulletWorld;

    private Server mServer;

    public ServerWorldModel(Server server) {
        mServer = server;
        mServer.addListener(this);
        create();
    }

    @Override
    public void create() {
        mBulletWorld = new BulletWorld();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float delta) {
        mBulletWorld.update(delta);

        for(Player p : mPlayers) {
            p.update(delta);
        }
    }

    public void addPlayer(Player player) {
        mPlayers.add(player);
    }

    public BulletWorld getBulletWorld() { return mBulletWorld; }

    @Override
    public void connected(Connection con) {
        System.out.println("ServerModel: SomeOneConnected!LOL!");

        mPlayers.add(new Player(new Vector3(0, 10, 0), mBulletWorld));
        mServer.sendToAllTCP(new PlayerJoinPacket(0f, 10f, 0f));
    }

    @Override
    public void disconnected(Connection con) {

    }

    @Override
    public void received(Connection con, Packet packet) {

    }
}
