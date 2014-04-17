package se.rhel.model.server;

import se.rhel.model.BaseWorldModel;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.Server;

import java.util.HashMap;

/**
 * Group: Mixed
 */
public class ServerWorldModel extends BaseWorldModel {                                  // N

    // Linkin ID's and Players
    private HashMap<Integer, ExternalPlayer> mPlayers;
    private Server mServer;                                                             // N

    public ServerWorldModel(Server server) {                                            // N
        super();

        mServer = server;                                                               // N
        mPlayers = new HashMap<>();
    }                                                                                   // N

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
}                                                                                       // N
