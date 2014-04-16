package se.rhel.model.client;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.Client;
import se.rhel.model.*;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.util.Log;

import java.util.HashMap;


/**
 * Group: Mixed
 */
public class ClientWorldModel extends BaseWorldModel implements IWorldModel {

    private Client mClient;
    private Player mPlayer;

    private HashMap<Integer, IPlayer> mPlayers = new HashMap<>();

    public ClientWorldModel(Client client) {
        super();

        mPlayer = new Player(new Vector3(0, 10, 0), getBulletWorld());
        mClient = client;
    }

    public void update(float delta) {
        super.update(delta);
        mPlayer.update(delta);

        for(IPlayer p : mPlayers.values()) {
            p.update(delta);
        }
    }

    public ExternalPlayer getExternalPlayer(int id) {
       for (IPlayer entity : mPlayers.values()) {
            ExternalPlayer ep = (ExternalPlayer)entity;
            if(ep.getClientId() == id) {
                return ep;
            }
        }

        return null;
    }

    public void addPlayer(int id, ExternalPlayer player) {
        mPlayers.put(id, player);
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public Array<IPlayer> getExternalPlayers() {
        Array<IPlayer> ret = new Array<>();
        for(IPlayer ent : mPlayers.values()) {
            ret.add(ent);
        }

        return ret;
    }

    public void damageEntity(int id, int amount) {
        // Well, darn, it was me
        if(id == mClient.getId()) {
            mPlayer.damageEntity(amount);
        } else {
            // Phew, it was somebody else
            ExternalPlayer ep = getExternalPlayer(id);
            ep.damageEntity(amount);
        }
    }

    public void killEntity(int id) {
        if(id == mClient.getId()) {
            Log.debug("ClientWorldModel", "I AM DEAD");
            if(mPlayer.getHealth() != 0) {
                mPlayer.damageEntity(100);
            }
        } else {
            Log.debug("ClientWorldModel", "Someone else is DEAD with id: " + id);
        }
    }
}