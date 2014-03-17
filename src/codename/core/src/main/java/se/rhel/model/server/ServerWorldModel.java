package se.rhel.model.server;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.Connection;
import se.rhel.network.packet.PlayerPacket;
import se.rhel.network.packet.RequestInitialStatePacket;
import se.rhel.packet.TestMaxPacket;
import se.rhel.packet.TestPacket;
import se.rhel.server.Server;
import se.rhel.model.BaseModel;
import se.rhel.model.BulletWorld;
import se.rhel.model.Player;
import se.rhel.observer.ServerListener;
import se.rhel.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Group: Mixed
 */
public class ServerWorldModel implements BaseModel, ServerListener {

    // private Array<Player> mPlayers = new Array<Player>();
    // Linkin ID's and Players
    private HashMap<Integer, Player> mPlayers;
    private BulletWorld mBulletWorld;

    private Server mServer;

    public ServerWorldModel(Server server) {
        mServer = server;
        mPlayers = new HashMap<>();
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

        for(Player p : mPlayers.values()) {
            p.update(delta);
        }
    }

    private void addPlayer(Integer id, Player player) {
        mPlayers.put(id, player);
    }

    private Player getPlayer(int id) {
        return mPlayers.get(id);
    }

    /**
     * Gets all the players except the one with the id specified
     * @param id of Player to not be in Array
     * @return Array with Player objects
     */
    private Array<Player> getPlayersExcept(int id) {
        Array<Player> toReturn = new Array<>();

        for(Map.Entry<Integer, Player> entry : mPlayers.entrySet()) {
            if(entry.getKey() != id) {
                toReturn.add(entry.getValue());
            }
        }

        return toReturn;
    }

    public BulletWorld getBulletWorld() { return mBulletWorld; }

    @Override
    public void connected(Connection con) {
        Log.debug("ServerWorldModel", "Some one connected to the server with id: " + con.getId());

        // Meaning, a new player should be added on the server
        addPlayer(con.getId(), new Player(new Vector3(0, 10, 0), mBulletWorld));
        // And sending to all clients except the one joined
        mServer.sendToAllTCPExcept(new PlayerPacket(0f, 10f, 0f), con);
    }

    @Override
    public void disconnected(Connection con) {

    }

    @Override
    public void received(Connection con, Object obj) {

        if(obj instanceof RequestInitialStatePacket) {
            Log.debug("ServerWorldModel", "Initial state requested from clientId: " + con.getId());
            mServer.sendTCP(new TestPacket(0, 1.1f, 1.2, 'a', Byte.valueOf("1"), Long.valueOf(2), Short.valueOf("1")), con);
            mServer.sendTCP(new TestMaxPacket(1), con);
            // Sending all the players to the client requested, except self
            for(Player p : getPlayersExcept(con.getId())) {
                Log.debug("ServerWorldModel", "Sending player to " + con.getId());
                PlayerPacket pp = new PlayerPacket(0f, 10f, 0f);
                mServer.sendTCP(pp, con);
            }
        }
        Log.debug("ServerWorldModel", "RECEIVED: " + obj + " FROM: " + con);
    }
}
