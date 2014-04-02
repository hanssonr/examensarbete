package se.rhel.model.client;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.Client;
import se.rhel.event.EventHandler;
import se.rhel.event.NetworkEvent;
import se.rhel.model.*;
import se.rhel.model.entity.IEntity;
import se.rhel.network.packet.*;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.observer.ClientListener;
import se.rhel.packet.TestPacket;
import se.rhel.util.Log;


/**
 * Group: Mixed
 */
public class ClientWorldModel extends BaseWorldModel implements ClientListener, IWorldModel {

    private Client mClient;
    private Player mPlayer;
    private Array<IEntity> mPlayers;

    public ClientWorldModel(Client client) {
        super();

        mPlayer = new Player(new Vector3(0, 20, 0), getBulletWorld());
        mPlayers = new Array<>();
        mClient = client;
        mClient.addListener(this);
        mClient.sendTcp(new RequestInitialStatePacket(mClient.getId()));
    }

    public void update(float delta) {
        super.update(delta);
        mPlayer.update(delta);

        for (int i = 0; i < mPlayers.size; i++) {
            mPlayers.get(i).update(delta);
        }
    }

    public ExternalPlayer getExternalPlayer(int id) {
        for (IEntity entity : mPlayers) {
            ExternalPlayer ep = (ExternalPlayer)entity;
            if(ep.getClientId() == id) {
                return ep;
            }
        }
        return null;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public Array<IEntity> getExternalPlayers() {
        return mPlayers;
    }

    @Override
    public void connected() {

    }

    @Override
    public void disconnected() {

    }

    @Override
    public void received(Object obj) {
        if (obj instanceof PlayerPacket) {
            Log.debug("ClientWorldModel", "Player_Join packet - Player can be viewed on client!!");
            PlayerPacket pp = (PlayerPacket)obj;
            ExternalPlayer ep = new ExternalPlayer(pp.clientId, new Vector3(pp.x, pp.y, pp.z), getBulletWorld());
            mPlayers.add(ep);
            EventHandler.events.notify(new NetworkEvent(pp));
        }
        else if(obj instanceof TestPacket) {
            Log.debug("ClientWorldModel", "TestPacket received");
            TestPacket tp = (TestPacket)obj;
        }
        else if(obj instanceof PlayerMovePacket) {
            // An external player have moved and should be updated, accordingly
            PlayerMovePacket pmp = (PlayerMovePacket)obj;

            ExternalPlayer ep;
            synchronized (ep = getExternalPlayer(pmp.clientId)) {
                if(ep == null) {
                    return;
                }

                // Set the position & rotation
                ep.setPositionAndRotation(pmp.mPosition, pmp.mRotX);
            }
        }
        else if (obj instanceof DamagePacket) {
            // A player has been damaged
            DamagePacket dp = (DamagePacket)obj;

            // Well, darn, it was me
            if(dp.clientId == mClient.getId()) {
                mPlayer.damageEntity(dp.amount);
            } else {
                // Phew, it was somebody else
                ExternalPlayer ep = getExternalPlayer(dp.clientId);
                ep.damageEntity(dp.amount);
            }

            Log.debug("ClientWorldModel", "Received DamagePacket, Playerid: " + dp.clientId + " got shot with amount: " + dp.amount);
        }
        else if (obj instanceof ShootPacket) {
            // Visual representation of shoot
            Log.debug("ClientWorldModel", "ShotPacket received on client");
            ShootPacket sp = (ShootPacket)obj;

            // Notify listeners about that an external player has shot
            EventHandler.events.notify(new NetworkEvent(sp));
        }
        else if (obj instanceof BulletHolePacket) {
            Log.debug("ClientWorldModel", "BulletHolePacket received on client");
            // Someone else has shot, and missed, thus bullethole at this position
            BulletHolePacket bhp = (BulletHolePacket)obj;

            EventHandler.events.notify(new NetworkEvent(bhp));
        }
        else if (obj instanceof DeadEntityPacket) {
            DeadEntityPacket dep = (DeadEntityPacket)obj;

            if(dep.clientId == mClient.getId()) {
                Log.debug("ClientWorldModel", "I AM DEAD");
                if(mPlayer.getHealth() != 0) {
                    mPlayer.damageEntity(100);
                }
            } else {
                Log.debug("ClientWorldModel", "Someone else is DEAD with id: " + dep.clientId);
            }

        }
        else if (obj instanceof GrenadeCreatePacket) {
            Log.debug("ClientWorldModel", "Received GrenadeCreatePacket");
            GrenadeCreatePacket gcp = (GrenadeCreatePacket) obj;
            // Notify networklistener
            EventHandler.events.notify(new NetworkEvent(gcp));
        }
    }
}