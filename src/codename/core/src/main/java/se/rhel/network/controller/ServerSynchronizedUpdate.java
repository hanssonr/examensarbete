package se.rhel.network.controller;

import com.badlogic.gdx.math.Vector3;
import se.rhel.Connection;
import se.rhel.Server;
import se.rhel.event.Events;
import se.rhel.model.component.*;
import se.rhel.model.entity.ControlledPlayer;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.RayVector;
import se.rhel.network.model.ConnectionWrappedObject;
import se.rhel.network.model.ServerWorldModel;
import se.rhel.model.weapon.Grenade;
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


    private ArrayList<ConnectionWrappedObject> mUnsyncedObjects = new ArrayList<>();

    public ServerSynchronizedUpdate() {}

    public synchronized void update(ServerWorldModel world, Server server) {
        for (Iterator<ConnectionWrappedObject> it = mUnsyncedObjects.iterator(); it.hasNext();) {
            ConnectionWrappedObject uObj = it.next();
            Object obj = uObj.getObject();
            Connection con = uObj.getConnection();

            if(obj instanceof RequestInitialStatePacket) {
                Log.debug("ServerWorldModel", "Initial state requested from clientId: " + con.getId());
                PlayerPacket pp = new PlayerPacket(world.getAllPlayers());
                server.sendTCP(pp, con);
            }

            else if(obj instanceof PlayerMovePacket) {
                PlayerMovePacket pmp = (PlayerMovePacket)obj;

                world.transformEntity(pmp.clientId, pmp.mPosition, pmp.mRotation);
                server.sendToAllUDPExcept(new PlayerMovePacket(pmp.clientId, pmp.mPosition, pmp.mRotation), con);
            }

            else if (obj instanceof ShootPacket) {
                Log.debug("ServerSynchronizedUpdate", "ShotPacket received on server");
                ShootPacket sp = (ShootPacket)obj;

                RayVector ray = new RayVector(sp.mFrom, sp.mTo);
                world.checkShootCollision(ray, (GameObject) world.getPlayer(sp.clientId));

                // Resend to other clients that a player has shot for visual feedback
                ShootPacket sendP = new ShootPacket(sp.clientId, ray.getFrom(), ray.getTo());
                server.sendToAllUDPExcept(sendP, con);
            }

            else if (obj instanceof GrenadeCreatePacket) {
                Log.debug("ServerSynchronizedUpdate", "GrenadeCreatePacket received on server");
                GrenadeCreatePacket gcp = (GrenadeCreatePacket) obj;

                // A player wants to throw a grenade!
                GameObject go = (GameObject) world.getPlayer(gcp.clientId);
                IActionable ac = (IActionable) go.getComponent(ActionComponent.class);

                // Check if the player can throw
                if(ac.canThrowGrenade()) {
                    //Add info to create grenade
                    Grenade g = new Grenade(world.getBulletWorld(), gcp.position, gcp.direction);
                    g.addComponent(new NetworkComponent(Utils.getInstance().generateUniqueId()));
                    world.addGrenade(g);

                    // Send tcp to clients
                    server.sendToAllTCP(new GrenadeCreatePacket(((NetworkComponent)g.getComponent(NetworkComponent.class)).getID(), ((IPlayer)go).getShootPosition(), go.getDirection()));
                }

            }

            else if (obj instanceof PlayerPacket) {
                // Meaning, a new player should be added on the server
                ControlledPlayer cp = new ControlledPlayer(world.getBulletWorld(), new Vector3(0, 10, 0));
                cp.addComponent(new NetworkComponent(con.getId()));
                world.setPlayer(con.getId(), cp);
                // And sending to all clients except the one joined
                server.sendToAllTCPExcept(new PlayerPacket(con.getId(), cp.getPosition(), cp.getRotation()), con);
            }

            it.remove();
        }
    }

    @Override
    public void connected(Connection con) {
        Log.debug("ServerWorldModel", "Some one connected to the server with id: " + con.getId());
        mUnsyncedObjects.add(new ConnectionWrappedObject(con, new PlayerPacket(con.getId(), new Vector3(0, 10, 0), new Vector3(0,0,0))));
    }

    @Override
    public void disconnected(Connection con) {

    }

    @Override
    public synchronized void received(Connection con, Object obj) {
        mUnsyncedObjects.add(new ConnectionWrappedObject(con, obj));
    }
}
