package se.rhel.network.controller;

import com.badlogic.gdx.math.Vector3;
import se.rhel.Connection;
import se.rhel.Server;
import se.rhel.event.EventHandler;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.physics.RayVector;
import se.rhel.network.model.ConnectionWrappedObject;
import se.rhel.network.model.ServerWorldModel;
import se.rhel.model.util.Utils;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.network.packet.*;
import se.rhel.observer.ServerListener;
import se.rhel.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Group: Multiplayer
 * Created by rkh on 2014-04-05.
 */
public class ServerSynchronizedUpdate implements ServerListener {

    private ServerWorldModel mWorld;
    private Server mServer;

    private ArrayList<ConnectionWrappedObject> mUnsyncedObjects = new ArrayList<>();

    public ServerSynchronizedUpdate(ServerWorldModel world, Server server) {
        mWorld = world;
        mServer = server;
    }

    public synchronized void update() {
        for (Iterator<ConnectionWrappedObject> it = mUnsyncedObjects.iterator(); it.hasNext();) {
            ConnectionWrappedObject uObj = it.next();
            Object obj = uObj.getObject();
            Connection con = uObj.getConnection();

            if(obj instanceof RequestInitialStatePacket) {
                Log.debug("ServerWorldModel", "Initial state requested from clientId: " + con.getId());

                // Sending all the players to the client requested, except self
                for(Map.Entry<Integer, ExternalPlayer> pairs : mWorld.getPlayers().entrySet()) {
                    int id = pairs.getKey();
                    ExternalPlayer ep = pairs.getValue();
                    if(id != con.getId()) {
                        Log.debug("ServerWorldModel", "Sending player with id: " + id + " to " + con.getId());
                        PlayerPacket pp = new PlayerPacket(id, ep.getPosition());
                        mServer.sendTCP(pp, con);
                    }
                }
            }
            else if(obj instanceof PlayerMovePacket) {
                PlayerMovePacket pmp = (PlayerMovePacket)obj;

                // Set the position
                ExternalPlayer p = mWorld.getExternalPlayer(pmp.clientId);
                if(p == null) return;

                p.setPositionAndRotation(pmp.mPosition, pmp.mRotation);

                mServer.sendToAllUDPExcept(new PlayerMovePacket(pmp.clientId, pmp.mPosition, pmp.mRotation), con);
            }
            else if (obj instanceof ShootPacket) {
                Log.debug("ServerSynchronizedUpdate", "ShotPacket received on server");
                ShootPacket sp = (ShootPacket)obj;
                Vector3 dir = sp.vTo.cpy().sub(sp.vFrom.cpy()).nor();

                mWorld.checkShootCollision(new RayVector(sp.mFrom, sp.mTo), con);

                // Resend to other clients that a player has shot for visual feedback
                ShootPacket sendP = new ShootPacket(sp.clientId, sp.mFrom, sp.mTo, sp.vFrom.cpy().add(dir), sp.vTo.cpy().add(dir), sp.vFrom2.cpy().add(dir), sp.vTo2.cpy().add(dir));
                mServer.sendToAllUDPExcept(sendP, con);
            }
            else if (obj instanceof GrenadeCreatePacket) {
                Log.debug("ServerSynchronizedUpdate", "GrenadeCreatePacket received on server");
                GrenadeCreatePacket gcp = (GrenadeCreatePacket) obj;

                //Add info to create grenade
                Grenade g = new Grenade(mWorld.getBulletWorld(), gcp.position, gcp.direction);
                g.setId(Utils.getInstance().generateUniqueId());
                mWorld.addGrenade(g);

                // A player wants to throw a grenade!
                ExternalPlayer ep = mWorld.getExternalPlayer(gcp.clientId);
                if(ep != null) {
                    ep.grenadeThrow();
                }

                mServer.sendToAllTCP(new GrenadeCreatePacket(g.getId(), ep.getPosition(), ep.getDirection()));
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
        mServer.sendToAllTCPExcept(new PlayerPacket(con.getId(), ep.getPosition()), con);
    }

    @Override
    public void disconnected(Connection con) {

    }

    @Override
    public synchronized void received(Connection con, Object obj) {
        mUnsyncedObjects.add(new ConnectionWrappedObject(con, obj));
    }
}
