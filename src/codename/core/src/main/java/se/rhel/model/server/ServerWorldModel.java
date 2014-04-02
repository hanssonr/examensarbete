package se.rhel.model.server;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.Connection;
import se.rhel.model.BaseWorldModel;
import se.rhel.event.Events;
import se.rhel.model.WorldModel;
import se.rhel.model.physics.MyContactListener;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.network.packet.*;
import se.rhel.Server;
import se.rhel.model.Player;
import se.rhel.observer.ServerListener;
import se.rhel.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Group: Mixed
 */
public class ServerWorldModel extends BaseWorldModel implements ServerListener {

    // Linkin ID's and Players
    private HashMap<Integer, ExternalPlayer> mPlayers;
    private Server mServer;

    public ServerWorldModel(Server server) {
        super();

        mServer = server;
        mServer.addListener(this);
        mPlayers = new HashMap<>();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        for(ExternalPlayer p : mPlayers.values()) {
            p.update(delta);
        }
    }

    private void addPlayer(Integer id, ExternalPlayer player) {
        mPlayers.put(id, player);
    }

    private ExternalPlayer getPlayer(int id) {
        return mPlayers.get(id);
    }

    private int getPlayerId(Player p) {
        Iterator it = mPlayers.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry<Integer, Player> pairs = (Map.Entry)it.next();
            if (pairs.getValue().equals(p)) {
                return pairs.getKey();
            }
        }

        throw new IllegalArgumentException("Player could not be found");
    }

    /**
     * Gets all the players except the one with the id specified
     * @param id of Player to not be in Array
     * @return Array with Player objects
     */
    private Array<ExternalPlayer> getPlayersExcept(int id) {
        Array<ExternalPlayer> toReturn = new Array<>();

        for(Map.Entry<Integer, ExternalPlayer> entry : mPlayers.entrySet()) {
            if(entry.getKey() != id) {
                toReturn.add(entry.getValue());
            }
        }

        return toReturn;
    }

    @Override
    public void connected(Connection con) {
        Log.debug("ServerWorldModel", "Some one connected to the server with id: " + con.getId());

        // Meaning, a new player should be added on the server
        addPlayer(con.getId(), new ExternalPlayer(con.getId(), new Vector3(0, 10, 0), getBulletWorld()));
        // And sending to all clients except the one joined
        mServer.sendToAllTCPExcept(new PlayerPacket(con.getId(), 0f, 10f, 0f), con);
    }

    @Override
    public void disconnected(Connection con) {

    }

    @Override
    public void received(Connection con, Object obj) {

        if(obj instanceof RequestInitialStatePacket) {
            Log.debug("ServerWorldModel", "Initial state requested from clientId: " + con.getId());

            // Sending all the players to the client requested, except self
            for(Integer i : mPlayers.keySet()) {
                if(i != con.getId()) {
                    Log.debug("ServerWorldModel", "Sending player with id: " + i + " to " + con.getId());
                    PlayerPacket pp = new PlayerPacket(i, 0f, 10f, 0f);
                    mServer.sendTCP(pp, con);
                }
            }
        }
        else if(obj instanceof PlayerMovePacket) {
            PlayerMovePacket pmp = (PlayerMovePacket)obj;

            // Set the position
            ExternalPlayer p = getPlayer(pmp.clientId);
            if(p == null) return;

            p.setPosition(pmp.mPosition);

            // Notify the other clients, if any
            Vector3 tmp = p.getPosition();
            mServer.sendToAllUDPExcept(new PlayerMovePacket(pmp.clientId, pmp.mPosition, pmp.mRotX), con);
        }
        else if (obj instanceof ShootPacket) {
            Log.debug("ServerWorldModel", "ShotPacket received on server");
            ShootPacket sp = (ShootPacket)obj;

            Vector3 to = sp.vTo.cpy();
            Vector3 to2 = sp.vTo2.cpy();
            Vector3 from = sp.vFrom.cpy();
            Vector3 from2 = sp.vFrom2.cpy();
            Vector3 dir = to.cpy().sub(from.cpy()).nor();

            // But what should we do on the server, eh?
            MyContactListener.CollisionObject co = MyContactListener.checkShootCollision(getBulletWorld().getCollisionWorld(), new Vector3[]{sp.mFrom, sp.mTo});

            if (co != null) {

                if(co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
                    // World hit

                    to = co.hitPoint.cpy();
                    to2 = to.cpy();
                    mServer.sendToAllTCPExcept(new BulletHolePacket(co.hitPoint, co.hitNormal), con);
                } else {
                    // Entity hit
                    if(co.entity instanceof  ExternalPlayer) {
                        ExternalPlayer ep = (ExternalPlayer) co.entity;
                        // Damage player on server
                        ep.damageEntity(25);
                        Log.debug("ServerWorldModel", "ServerHit: " + co.entity.getHealth());

                        // Notify clients
                        mServer.sendToAllTCP(new DamagePacket(ep.getClientId(), 25));

                        // If the entity died
                        if(!co.entity.isAlive()) {
                            mDestroy.add(co.entity);

                            // .. also notify clients
                            mServer.sendToAllTCP(new DeadEntityPacket(ep.getClientId()));
                        }
                    }
                }
            }

            // Resend to other clients that a player has shot for visual feedback
            ShootPacket sendP = new ShootPacket(sp.clientId, sp.mFrom, sp.mTo, from.add(dir), to.add(dir), from2.add(dir), to2.add(dir));
            mServer.sendToAllUDPExcept(sendP, con);
        }
        else if (obj instanceof GrenadeCreatePacket) {
            Log.debug("ServerWorldModel", "GrenadeCreatePacket received on server");
            GrenadeCreatePacket gcp = (GrenadeCreatePacket) obj;

            // A player wants to throw a grenade!
            ExternalPlayer ep = getPlayer(gcp.clientId);
            if(ep != null) {
                ep.grenadeThrow();
            }


            // mServer.sendToAllTCPExcept(new GrenadeCreatePacket(0, gcp.position, gcp.direction), con);
        }
    }
}
