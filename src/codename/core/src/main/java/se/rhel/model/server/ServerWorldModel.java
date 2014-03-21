package se.rhel.model.server;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.Connection;
import se.rhel.model.WorldModel;
import se.rhel.network.packet.PlayerMovePacket;
import se.rhel.network.packet.PlayerPacket;
import se.rhel.network.packet.RequestInitialStatePacket;
import se.rhel.Server;
import se.rhel.model.Player;
import se.rhel.observer.ServerListener;
import se.rhel.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Group: Mixed
 */
public class ServerWorldModel extends WorldModel implements ServerListener {

    // Linkin ID's and Players
    private HashMap<Integer, Player> mPlayers;
    private Server mServer;

    public ServerWorldModel(Server server) {
        super();

        mServer = server;
        mServer.addListener(this);
        mPlayers = new HashMap<>();
    }

    @Override
    public void create() {
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float delta) {
        getBulletWorld().update(delta);

        for(Player p : mPlayers.values()) {
            // TODO: Server should update players
            // p.update(delta);
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

    @Override
    public void connected(Connection con) {
        Log.debug("ServerWorldModel", "Some one connected to the server with id: " + con.getId());

        // Meaning, a new player should be added on the server
        addPlayer(con.getId(), new Player(new Vector3(0, 10, 0), getBulletWorld()));
        // And sending to all clients except the one joined
        mServer.sendToAllTCPExcept(new PlayerPacket(con.getId(), 0f, 10f, 0f), con);
    }

    @Override
    public void disconnected(Connection con) {

    }

    @Override
    public void received(Connection con, Object obj) {

        if(obj instanceof RequestInitialStatePacket) {
            Log.debug("ServerWorldModel", "Initial state requested from clientId: " + con.getId());

            // Sending all the players to the client requested, except self
            for(Integer i : mPlayers.keySet()) {
                if(i != con.getId()) {
                    Log.debug("ServerWorldModel", "Sending player with id: " + i + " to " + con.getId());
                    PlayerPacket pp = new PlayerPacket(i, 0f, 10f, 0f);
                    mServer.sendTCP(pp, con);
                }
            }
        }
        else if(obj instanceof PlayerMovePacket) {
            PlayerMovePacket pmp = (PlayerMovePacket)obj;

            // Set the position
            Vector3 pos = new Vector3(pmp.pX, pmp.pY, pmp.pZ);
            Player p = getPlayer(pmp.clientId);
            if(p == null) return;

            p.setPosition(pos);

            // Notify the other clients, if any
            Vector3 tmp = p.getPosition();
            mServer.sendToAllUDPExcept(new PlayerMovePacket(pmp.clientId, tmp.x, tmp.y, tmp.z, pmp.rY, pmp.rW), con);
        }
    }
}
