package se.rhel.model.server;

import se.rhel.model.BaseWorldModel;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.Server;

import java.util.HashMap;

/**
 * Group: Mixed
 */
public class ServerWorldModel extends BaseWorldModel {

    // Linkin ID's and Players
    private HashMap<Integer, ExternalPlayer> mPlayers;
    private Server mServer;

    public ServerWorldModel(Server server) {
        super();

        mServer = server;
        mPlayers = new HashMap<>();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        for(ExternalPlayer p : mPlayers.values()) {
            p.update(delta);
        }
    }

    public HashMap<Integer, ExternalPlayer> getPlayers() {
        return mPlayers;
    }

    public void addPlayer(int id, ExternalPlayer player) {
        mPlayers.put(id, player);
    }

    public ExternalPlayer getExternalPlayer(int id) {
        return mPlayers.get(id);
    }
}
