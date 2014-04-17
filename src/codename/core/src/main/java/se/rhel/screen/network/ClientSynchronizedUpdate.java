package se.rhel.screen.network;

import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.event.NetworkEvent;
import se.rhel.model.client.ClientWorldModel;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.network.packet.*;
import se.rhel.observer.ClientListener;
import se.rhel.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Group: Mixed
 * Created by rkh on 2014-04-04.
 */
public class ClientSynchronizedUpdate implements ClientListener { // L

    private ArrayList<Object> mUnsyncedObjects = new ArrayList<>(); // L
    private ClientWorldModel mWorld; // L

    public ClientSynchronizedUpdate(ClientWorldModel world) { // L
        mWorld = world; // l
    } // L

    public synchronized void update() { // L
        for (Iterator<Object> it = mUnsyncedObjects.iterator(); it.hasNext();) {
            Object obj = it.next();

            if (obj instanceof PlayerPacket) {                                                                          // N
                Log.debug("ClientSynchronizedUpdate", "Player_Join packet - Player can be viewed on client!!");
                PlayerPacket pp = (PlayerPacket)obj;                                                                    // N
                ExternalPlayer ep = new ExternalPlayer(pp.clientId, pp.mPosition, mWorld.getBulletWorld());
                mWorld.addPlayer(pp.clientId, ep);

                EventHandler.events.notify(new NetworkEvent(pp));                                                       // N?
            }                                                                                                           // N
            else if(obj instanceof PlayerMovePacket) {                                                                  // N
                // An external player have moved and should be updated, accordingly
                PlayerMovePacket pmp = (PlayerMovePacket)obj;                                                           // N

                ExternalPlayer ep;
                synchronized (ep = mWorld.getExternalPlayer(pmp.clientId)) {
                    if(ep == null) {
                        return;
                    }

                    // Set the position & rotation
                    ep.setPositionAndRotation(pmp.mPosition, pmp.mRotation);
                }
            }                                                                                                           // N
            else if (obj instanceof DamagePacket) {
                // A player has been damaged
                DamagePacket dp = (DamagePacket)obj;                                                                    // N

                mWorld.damageEntity(dp.clientId, dp.amount);
                Log.debug("ClientSynchronizedUpdate", "Received DamagePacket, Playerid: " + dp.clientId + " got shot with amount: " + dp.amount);
            }                                                                                                           // N
            else if (obj instanceof ShootPacket) {                                                                      // N
                // Visual representation of shoot
                Log.debug("ClientSynchronizedUpdate", "ShotPacket received on client");
                ShootPacket sp = (ShootPacket)obj;                                                                      // N

                // Notify listeners about that an external player has shot
                EventHandler.events.notify(new NetworkEvent(sp));                                                       // N
            }                                                                                                           // N
            else if (obj instanceof BulletHolePacket) {                                                                 // N
                Log.debug("ClientSynchronizedUpdate", "BulletHolePacket received on client");
                // Someone else has shot, and missed, thus bullethole at this position
                BulletHolePacket bhp = (BulletHolePacket)obj;                                                           // N

                EventHandler.events.notify(new NetworkEvent(bhp));                                                      // N
            }                                                                                                           // N
            else if (obj instanceof DeadEntityPacket) {                                                                 // N
                DeadEntityPacket dep = (DeadEntityPacket)obj;                                                           // N

                mWorld.killEntity(dep.clientId);
            }                                                                                                           // N
            else if (obj instanceof GrenadeCreatePacket) {                                                              // N
                Log.debug("ClientWorldModel", "Received GrenadeCreatePacket");
                GrenadeCreatePacket gcp = (GrenadeCreatePacket) obj;                                                    // N

                Grenade g = new Grenade(mWorld.getBulletWorld(), gcp.position, gcp.direction);
                g.setId(gcp.clientId);                                                                                  // N
                mWorld.addGrenade(g);
                EventHandler.events.notify(new ModelEvent(EventType.GRENADE_CREATED, g));
            }                                                                                                           // N

            it.remove();
        }
    }

    @Override                                                                                                           // N
    public void connected() {}                                                                                          // N

    @Override                                                                                                           // N
    public void disconnected() {}                                                                                       // N

    @Override                                                                                                           // N
    public synchronized void received(Object obj) {                                                                     // N
        mUnsyncedObjects.add(obj);
    }                                                                                                                   // N
}
