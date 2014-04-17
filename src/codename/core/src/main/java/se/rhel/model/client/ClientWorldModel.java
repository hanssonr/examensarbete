package se.rhel.model.client;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.Client;
import se.rhel.model.*;
import se.rhel.model.entity.IPlayer;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.util.Log;

import java.util.HashMap;


/**
 * Group: Mixed
 */
public class ClientWorldModel extends BaseWorldModel implements IWorldModel { // N

    private Client mClient; // N
    private Player mPlayer; // L

    private HashMap<Integer, IPlayer> mPlayers = new HashMap<>(); // L

    public ClientWorldModel(Client client) { // N
        super(); // L

        mPlayer = new Player(new Vector3(0, 10, 0), getBulletWorld()); // L
        mClient = client; // N
    } // N

    public void update(float delta) { // L
        super.update(delta); // L
        mPlayer.update(delta); // L

        for(IPlayer p : mPlayers.values()) { // L
            p.update(delta); // L
        } // L
    } // L

    public ExternalPlayer getExternalPlayer(int id) { // L
       for (IPlayer entity : mPlayers.values()) { // L
            ExternalPlayer ep = (ExternalPlayer)entity; // L
            if(ep.getClientId() == id) { // N
                return ep; // L
            } // N
        } // L

        return null; // L
    } // L

    public void addPlayer(int id, ExternalPlayer player) { // L
        mPlayers.put(id, player); // L
    } // L

    public Player getPlayer() {
        return mPlayer;
    } // L

    public Array<IPlayer> getExternalPlayers() { // L
        Array<IPlayer> ret = new Array<>(); // L
        for(IPlayer ent : mPlayers.values()) { // L
            ret.add(ent); // L
        } // L

        return ret; // L
    } // L

    public void damageEntity(int id, int amount) { // L
        // Well, darn, it was me
        if(id == mClient.getId()) { // N
            mPlayer.damageEntity(amount); // L
        } else { // N
            // Phew, it was somebody else
            ExternalPlayer ep = getExternalPlayer(id); // L
            ep.damageEntity(amount); // L
        } // N
    } // L

    public void killEntity(int id) { // L
        if(id == mClient.getId()) { // N
            Log.debug("ClientWorldModel", "I AM DEAD"); // L
            if(mPlayer.getHealth() != 0) { // L
                mPlayer.damageEntity(100); // L
            } // L
        } else { // N
            Log.debug("ClientWorldModel", "Someone else is DEAD with id: " + id); // L
        } // N
    } // L
} // N