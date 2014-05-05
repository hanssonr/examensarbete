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
    private INetworkWorldModel mWorld;
    private Events mEvents;
    private Client mClient;

    public ClientSynchronizedUpdate(IWorldModel world, Client client, Events events) {
        mWorld = (INetworkWorldModel)world;
        mClient = client;
        mEvents = events;
    }

    public synchronized void update() {
        for (Iterator<Object> it = mUnsyncedObjects.iterator(); it.hasNext();) {
            Object obj = it.next();

            if (obj instanceof PlayerPacket) {
                PlayerPacket pp = (PlayerPacket)obj;
                Log.debug("ClientSynchronizedUpdate", "PlayerPacket received - Players received: " + pp.mPlayers.size());

                for(int i = 0; i < pp.mPlayers.size(); i++) {
                    UpdateStruct ps = pp.mPlayers.get(i);
                    if(ps.mID != mClient.getId()) {
                        ControlledPlayer cp = new ControlledPlayer(mWorld.getBulletWorld(), ps.mPosition);
                        cp.addComponent(new NetworkComponent(ps.mID));
                        cp.rotateAndTranslate(ps.mRotation, ps.mPosition);
                        mWorld.setPlayer(ps.mID, cp);
                        mEvents.notify(new ModelEvent(EventType.PLAYER_JOIN, cp));
                    }
                }
            }

            else if(obj instanceof PlayerMovePacket) {
                // An external player have moved and should be updated, accordingly
                PlayerMovePacket pmp = (PlayerMovePacket)obj;
                mWorld.transformEntity(pmp.clientId, pmp.mPosition, pmp.mRotation);
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
                g.addComponent(new NetworkComponent(gcp.clientId));

                mWorld.addGrenade(g);
                mEvents.notify(new ModelEvent(EventType.GRENADE_CREATED, g));
            }

            else if(obj instanceof GrenadeUpdatePacket) {
                GrenadeUpdatePacket gup = (GrenadeUpdatePacket) obj;
                // Tell the client to update this grenade
                ((ClientWorldModel) mWorld).updateGrenade(gup.clientId, gup.position, gup.rotation, gup.isAlive);
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
