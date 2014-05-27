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
import se.rhel.packet.ConnectAcceptPacket;
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

    public synchronized void update() {
        for (Iterator<Object> it = mUnsyncedObjects.iterator(); it.hasNext();) {
            Object obj = it.next();

            if (obj instanceof PlayerPacket) {
                PlayerPacket pp = (PlayerPacket)obj;
                Log.debug("ClientSynchronizedUpdate", "PlayerPacket received - Players received: " + pp.mPlayers.size());
                mEvents.notify(new NetworkEvent(pp));
            }

            else if(obj instanceof PlayerMovePacket) {
                // An external player have moved and should be updated, accordingly
                mEvents.notify(new NetworkEvent((PlayerMovePacket)obj));
            }

            else if (obj instanceof DamagePacket) {
                // A player has been damaged
                mEvents.notify(new NetworkEvent((DamagePacket)obj));
            }

            else if (obj instanceof ShootPacket) {
                // Visual representation of shoot
                Log.debug("ClientSynchronizedUpdate", "ShotPacket received on client");
                mEvents.notify(new NetworkEvent((ShootPacket)obj));
            }

            else if (obj instanceof BulletHolePacket) {
                Log.debug("ClientSynchronizedUpdate", "BulletHolePacket received on client");
                mEvents.notify(new NetworkEvent((BulletHolePacket)obj));
            }

            else if (obj instanceof DeadEntityPacket) {
                mEvents.notify(new NetworkEvent((DeadEntityPacket)obj));
            }

            else if (obj instanceof GrenadeCreatePacket) {
                Log.debug("ClientWorldModel", "Received GrenadeCreatePacket");
                mEvents.notify(new NetworkEvent((GrenadeCreatePacket)obj));
            }

            else if(obj instanceof GrenadeUpdatePacket) {
                mEvents.notify(new NetworkEvent((GrenadeUpdatePacket)obj));
            }

            else if(obj instanceof ConnectedPacket) {
                mEvents.notify(new NetworkEvent((ConnectedPacket)obj));
            }

            it.remove();
        }
    }

    @Override
    public void connected() {
        mUnsyncedObjects.add(new ConnectedPacket());
    }

    @Override
    public void disconnected() {}

    @Override
    public synchronized void received(Object obj) {
        mUnsyncedObjects.add(obj);
    }
}
