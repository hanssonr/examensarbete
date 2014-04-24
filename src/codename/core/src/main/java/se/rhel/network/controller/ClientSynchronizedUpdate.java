package se.rhel.network.controller;

import com.badlogic.gdx.math.Vector3;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.Events;
import se.rhel.event.ModelEvent;
import se.rhel.model.IWorldModel;
import se.rhel.network.event.NetworkEvent;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.network.model.INetworkWorldModel;
import se.rhel.network.packet.*;
import se.rhel.observer.ClientListener;
import se.rhel.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Group: Multiplayer
 * Created by rkh on 2014-04-04.
 */
public class ClientSynchronizedUpdate implements ClientListener {

    private ArrayList<Object> mUnsyncedObjects = new ArrayList<>();
    private INetworkWorldModel mWorld;
    private Events mEvents;

    public ClientSynchronizedUpdate(IWorldModel world, Events events) {
        mWorld = (INetworkWorldModel)world;
        mEvents = events;
    }

    public synchronized void update() {
        for (Iterator<Object> it = mUnsyncedObjects.iterator(); it.hasNext();) {
            Object obj = it.next();

            if (obj instanceof PlayerPacket) {
                Log.debug("ClientSynchronizedUpdate", "Player_Join packet - Player can be viewed on client!!");
                PlayerPacket pp = (PlayerPacket)obj;
                ExternalPlayer ep = new ExternalPlayer(pp.clientId, pp.mPosition, mWorld.getBulletWorld());
                mWorld.addPlayer(pp.clientId, ep);

                mEvents.notify(new NetworkEvent(pp));
            }

            else if(obj instanceof PlayerMovePacket) {
                // An external player have moved and should be updated, accordingly
                PlayerMovePacket pmp = (PlayerMovePacket)obj;

                ExternalPlayer ep = mWorld.getExternalPlayer(pmp.clientId);
                if(ep != null) {
                    // Set the position & rotation
                    ep.setPositionAndRotation(pmp.mPosition, pmp.mRotation);
                }
            }

            else if (obj instanceof DamagePacket) {
                // A player has been damaged
                DamagePacket dp = (DamagePacket)obj;
                mWorld.damageEntity(dp.clientId, dp.amount);
            }

            else if (obj instanceof ShootPacket) {
                // Visual representation of shoot
                Log.debug("ClientSynchronizedUpdate", "ShotPacket received on client");
                ShootPacket sp = (ShootPacket)obj;

                // Notify listeners about that an external player has shot
                mEvents.notify(new NetworkEvent(sp));
            }

            else if (obj instanceof BulletHolePacket) {
                Log.debug("ClientSynchronizedUpdate", "BulletHolePacket received on client");
                // Someone else has shot, and missed, thus bullethole at this position
                BulletHolePacket bhp = (BulletHolePacket)obj;

                mEvents.notify(new NetworkEvent(bhp));
            }

            else if (obj instanceof DeadEntityPacket) {
                DeadEntityPacket dep = (DeadEntityPacket)obj;
                mWorld.killEntity(dep.clientId);
            }

            else if (obj instanceof GrenadeCreatePacket) {
                Log.debug("ClientWorldModel", "Received GrenadeCreatePacket");
                GrenadeCreatePacket gcp = (GrenadeCreatePacket) obj;

                Grenade g = new Grenade(mWorld.getBulletWorld(), gcp.position, gcp.direction);
                g.setId(gcp.clientId);
                mWorld.addGrenade(g);
                mEvents.notify(new ModelEvent(EventType.GRENADE_CREATED, g));
            }

            it.remove();
        }
    }

    @Override
    public void connected() {}

    @Override
    public void disconnected() {}

    @Override
    public synchronized void received(Object obj) {
        mUnsyncedObjects.add(obj);
    }
}
