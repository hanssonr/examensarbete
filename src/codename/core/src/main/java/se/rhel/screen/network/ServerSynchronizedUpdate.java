package se.rhel.screen.network;

import com.badlogic.gdx.math.Vector3;
import se.rhel.Connection;
import se.rhel.Server;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.server.ServerWorldModel;
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
 * Group: Mixed
 * Created by rkh on 2014-04-05.
 */
public class ServerSynchronizedUpdate implements ServerListener {                                                       // N

    private ServerWorldModel mWorld;
    private Server mServer;                                                                                             // N

    private ArrayList<UnsyncedObject> mUnsyncedObjects = new ArrayList<>();

    public ServerSynchronizedUpdate(ServerWorldModel world, Server server) {                                            // N
        mWorld = world;
        mServer = server;                                                                                               // N
    }                                                                                                                   // N

    public synchronized void update() {
        for (Iterator<UnsyncedObject> it = mUnsyncedObjects.iterator(); it.hasNext();) {
            UnsyncedObject uObj = it.next();
            Object obj = uObj.getObject();
            Connection con = uObj.getConnection();                                                                      // N

            if(obj instanceof RequestInitialStatePacket) {                                                              // N
                Log.debug("ServerWorldModel", "Initial state requested from clientId: " + con.getId());

                // Sending all the players to the client requested, except self
                for(Map.Entry<Integer, ExternalPlayer> pairs : mWorld.getPlayers().entrySet()) {
                    int id = pairs.getKey();                                                                            // L
                    ExternalPlayer ep = pairs.getValue();
                    if(id != con.getId()) {                                                                             // N
                        Log.debug("ServerWorldModel", "Sending player with id: " + id + " to " + con.getId());
                        PlayerPacket pp = new PlayerPacket(id, ep.getPosition());                                       // N
                        mServer.sendTCP(pp, con);                                                                       // N
                    }                                                                                                   // N
                }
            }                                                                                                           // N
            else if(obj instanceof PlayerMovePacket) {                                                                  // N
                PlayerMovePacket pmp = (PlayerMovePacket)obj;                                                           // N

                // Set the position
                ExternalPlayer p = mWorld.getExternalPlayer(pmp.clientId);
                if(p == null) return;

                p.setPositionAndRotation(pmp.mPosition, pmp.mRotation);

                mServer.sendToAllUDPExcept(new PlayerMovePacket(pmp.clientId, pmp.mPosition, pmp.mRotation), con);      // N
            }                                                                                                           // N
            else if (obj instanceof ShootPacket) {                                                                      // N
                Log.debug("ServerSynchronizedUpdate", "ShotPacket received on server");
                ShootPacket sp = (ShootPacket)obj;                                                                      // N

                Vector3 to = sp.vTo.cpy();
                Vector3 to2 = sp.vTo2.cpy();
                Vector3 from = sp.vFrom.cpy();
                Vector3 from2 = sp.vFrom2.cpy();
                Vector3 dir = to.cpy().sub(from.cpy()).nor();

                // But what should we do on the server, eh?
                MyContactListener.CollisionObject co = mWorld.getContactListener().checkShootCollision(mWorld.getBulletWorld().getCollisionWorld(), new Vector3[] {from, to});

                if (co != null) {

                    if(co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
                        // World hit
                        to = co.hitPoint.cpy();
                        to2 = to.cpy();
                        mServer.sendToAllTCPExcept(new BulletHolePacket(co.hitPoint, co.hitNormal), con);               // N
                    } else {
                        // Entity hit
                        if(co.entity instanceof  ExternalPlayer) {
                            ExternalPlayer ep = (ExternalPlayer) co.entity;
                            // Damage player on server
                            ep.damageEntity(25);
                            Log.debug("ServerWorldModel", "ServerHit: " + co.entity.getHealth());

                            // Notify clients
                            mServer.sendToAllTCP(new DamagePacket(ep.getClientId(), 25));                               // N

                            // If the entity died
                            if(!co.entity.isAlive()) {
                                co.entity.destroy();
                                //mDestroy.add(co.entity);

                                // .. also notify clients
                                mServer.sendToAllTCP(new DeadEntityPacket(ep.getClientId()));                           // N
                            }
                        }
                    }
                }

                // Resend to other clients that a player has shot for visual feedback
                ShootPacket sendP = new ShootPacket(sp.clientId, sp.mFrom, sp.mTo, from.add(dir), to.add(dir), from2.add(dir), to2.add(dir)); // N
                mServer.sendToAllUDPExcept(sendP, con);                                                                 // N
            }                                                                                                           // N
            else if (obj instanceof GrenadeCreatePacket) {                                                              // N
                Log.debug("ServerSynchronizedUpdate", "GrenadeCreatePacket received on server");
                GrenadeCreatePacket gcp = (GrenadeCreatePacket) obj;                                                    // N

                //Add info to create grenade
                Grenade g = new Grenade(mWorld.getBulletWorld(), gcp.position, gcp.direction);
                g.setId(Utils.getInstance().generateUniqueId());                                                        // N ?
                mWorld.addGrenade(g);

                // A player wants to throw a grenade!
                ExternalPlayer ep = mWorld.getExternalPlayer(gcp.clientId);
                if(ep != null) {
                    ep.grenadeThrow();
                }

                mServer.sendToAllTCP(new GrenadeCreatePacket(g.getId(), ep.getPosition(), ep.getDirection()));          // N
            }                                                                                                           // N

            it.remove();
        }
    }

    private class UnsyncedObject {                                                                                      // N
        private Connection mConnection;                                                                                 // N
        private Object mObj;

        public UnsyncedObject(Connection con, Object obj) {                                                             // N
            mConnection = con;                                                                                          // N
            mObj = obj;
        }                                                                                                               // N

        public Connection getConnection() {
            return mConnection;
        }                                                       // N

        public Object getObject() {
            return mObj;
        }
    }                                                                                                                   // N

    @Override                                                                                                           // N
    public void connected(Connection con) {                                                                             // N
        Log.debug("ServerWorldModel", "Some one connected to the server with id: " + con.getId());

        // Meaning, a new player should be added on the server
        ExternalPlayer ep = new ExternalPlayer(con.getId(), new Vector3(0, 10, 0), mWorld.getBulletWorld());
        mWorld.addPlayer(con.getId(), ep);
        // And sending to all clients except the one joined
        mServer.sendToAllTCPExcept(new PlayerPacket(con.getId(), ep.getPosition()), con);                               // N
    }                                                                                                                   // N

    @Override                                                                                                           // N
    public void disconnected(Connection con) {                                                                          // N

    }                                                                                                                   // N

    @Override                                                                                                           // N
    public synchronized void received(Connection con, Object obj) {                                                     // N
        mUnsyncedObjects.add(new UnsyncedObject(con, obj));
    }                                                                                                                   // N
}                                                                                                                       // N
