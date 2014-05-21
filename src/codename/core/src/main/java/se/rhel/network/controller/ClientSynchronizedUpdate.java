package se.rhel.network.controller;

import se.rhel.Client;
import se.rhel.event.EventType;
import se.rhel.event.Events;
import se.rhel.event.ModelEvent;
import se.rhel.model.IWorldModel;
import se.rhel.model.component.NetworkComponent;
import se.rhel.model.entity.ControlledPlayer;
import se.rhel.network.event.NetworkEvent;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.model.ClientWorldModel;
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
    private Events mEvents;

    public ClientSynchronizedUpdate(Events events) {
        mEvents = events;
    }

    public synchronized void update(INetworkWorldModel world, Client client) {
        for (Iterator<Object> it = mUnsyncedObjects.iterator(); it.hasNext();) {
            Object obj = it.next();

            if (obj instanceof PlayerPacket) {
                PlayerPacket pp = (PlayerPacket)obj;
                Log.debug("ClientSynchronizedUpdate", "PlayerPacket received - Players received: " + pp.mPlayers.size());

                for(int i = 0; i < pp.mPlayers.size(); i++) {
                    UpdateStruct ps = pp.mPlayers.get(i);
                    if(ps.mID != client.getId()) {
                        ControlledPlayer cp = new ControlledPlayer(world.getBulletWorld(), ps.mPosition);
                        cp.addComponent(new NetworkComponent(ps.mID));
                        cp.rotateAndTranslate(ps.mRotation, ps.mPosition);
                        world.setPlayer(ps.mID, cp);
                        mEvents.notify(new ModelEvent(EventType.PLAYER_JOIN, cp));
                    }
                }
            }

            else if(obj instanceof PlayerMovePacket) {
                // An external player have moved and should be updated, accordingly
                PlayerMovePacket pmp = (PlayerMovePacket)obj;
                world.transformEntity(pmp.clientId, pmp.mPosition, pmp.mRotation);
            }

            else if (obj instanceof DamagePacket) {
                // A player has been damaged
                DamagePacket dp = (DamagePacket)obj;
                world.damageEntity(dp.clientId, dp.amount);
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
                world.killEntity(dep.clientId);
            }

            else if (obj instanceof GrenadeCreatePacket) {
                Log.debug("ClientWorldModel", "Received GrenadeCreatePacket");
                GrenadeCreatePacket gcp = (GrenadeCreatePacket) obj;

                Grenade g = new Grenade(world.getBulletWorld(), gcp.position, gcp.direction);
                g.addComponent(new NetworkComponent(gcp.clientId));

                world.addGrenade(g);
                mEvents.notify(new ModelEvent(EventType.GRENADE_CREATED, g));
            }

            else if(obj instanceof GrenadeUpdatePacket) {
                GrenadeUpdatePacket gup = (GrenadeUpdatePacket) obj;
                // Tell the client to update this grenade
                ((ClientWorldModel) world).updateGrenade(gup.clientId, gup.position, gup.rotation, gup.isAlive);
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
