package se.rhel.network.controller;

import com.badlogic.gdx.math.Vector3;
import se.rhel.Connection;
import se.rhel.Server;
import se.rhel.event.Events;
import se.rhel.model.component.*;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.RayVector;
import se.rhel.network.model.ConnectionWrappedObject;
import se.rhel.network.model.ServerWorldModel;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.network.packet.*;
import se.rhel.observer.ServerListener;
import se.rhel.util.Log;
import se.rhel.util.Utils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Group: Multiplayer
 * Created by rkh on 2014-04-05.
 */
public class ServerSynchronizedUpdate implements ServerListener {

    private ServerWorldModel mWorld;
    private Server mServer;
    private Events mEvents;

    private ArrayList<ConnectionWrappedObject> mUnsyncedObjects = new ArrayList<>();

    public ServerSynchronizedUpdate(ServerWorldModel world, Server server, Events events) {
        mWorld = world;
        mServer = server;
        mEvents = events;
    }

    public synchronized void update() {
        for (Iterator<ConnectionWrappedObject> it = mUnsyncedObjects.iterator(); it.hasNext();) {
            ConnectionWrappedObject uObj = it.next();
            Object obj = uObj.getObject();
            Connection con = uObj.getConnection();

            if(obj instanceof RequestInitialStatePacket) {
                Log.debug("ServerWorldModel", "Initial state requested from clientId: " + con.getId());
                PlayerPacket pp = new PlayerPacket(mWorld.getPlayers());
                mServer.sendTCP(pp, con);
            }

            else if(obj instanceof PlayerMovePacket) {
                PlayerMovePacket pmp = (PlayerMovePacket)obj;

                mWorld.transformEntity(pmp.clientId, pmp.mPosition, pmp.mRotation);
                mServer.sendToAllUDPExcept(new PlayerMovePacket(pmp.clientId, pmp.mPosition, pmp.mRotation), con);
            }

            else if (obj instanceof ShootPacket) {
                Log.debug("ServerSynchronizedUpdate", "ShotPacket received on server");
                ShootPacket sp = (ShootPacket)obj;

                RayVector ray = new RayVector(sp.mFrom, sp.mTo);
                mWorld.checkShootCollision(ray);

                // Resend to other clients that a player has shot for visual feedback
                ShootPacket sendP = new ShootPacket(sp.clientId, ray.getFrom(), ray.getTo());
                mServer.sendToAllUDPExcept(sendP, con);
            }

            else if (obj instanceof GrenadeCreatePacket) {
                Log.debug("ServerSynchronizedUpdate", "GrenadeCreatePacket received on server");
                GrenadeCreatePacket gcp = (GrenadeCreatePacket) obj;

                // A player wants to throw a grenade!
                GameObject go = (GameObject) mWorld.getExternalPlayer(gcp.clientId);
                IActionable ac = (IActionable) go.getComponent(ActionComponent.class);

                System.out.println("SERVER:" + gcp.position);

                // Check if the player can throw
                if(ac.canThrowGrenade()) {
                    //Add info to create grenade
                    Grenade g = new Grenade(mWorld.getBulletWorld(), gcp.position, gcp.direction);
                    g.addComponent(new NetworkComponent(Utils.getInstance().generateUniqueId()));
                    mWorld.addGrenade(g);

                    // Send tcp to clients
                    mServer.sendToAllTCP(new GrenadeCreatePacket(((NetworkComponent)g.getComponent(NetworkComponent.class)).getID(), ((IPlayer)go).getShootPosition(), go.getDirection()));
                }

            }

            it.remove();
        }
    }

    @Override
    public void connected(Connection con) {
        Log.debug("ServerWorldModel", "Some one connected to the server with id: " + con.getId());

        // Meaning, a new player should be added on the server
        ExternalPlayer ep = new ExternalPlayer(con.getId(), new Vector3(0, 10, 0), mWorld.getBulletWorld());
        mWorld.addPlayer(con.getId(), ep);
        // And sending to all clients except the one joined
        mServer.sendToAllTCPExcept(new PlayerPacket(con.getId(), ep.getPosition(), ep.getRotation()), con);
    }

    @Override
    public void disconnected(Connection con) {

    }

    @Override
    public synchronized void received(Connection con, Object obj) {
        mUnsyncedObjects.add(new ConnectionWrappedObject(con, obj));
    }
}
