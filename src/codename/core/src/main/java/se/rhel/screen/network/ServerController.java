package se.rhel.screen.network;

import se.rhel.Server;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.event.ModelListener;
import se.rhel.model.server.ServerWorldModel;

/**
 * Group: Mixed
 * Created by Emil on 2014-04-02.
 */
public class ServerController implements ModelListener {                // Network

    private Server mServer; // Network
    private ServerWorldModel mServerWorldModel; // Logik
    private ServerSynchronizedUpdate mSyncedUpdate; // Logik

    public ServerController(Server server) { // Network
        mServer = server; // Network
        mServerWorldModel = new ServerWorldModel(server); // Logik
        mSyncedUpdate = new ServerSynchronizedUpdate(mServerWorldModel, mServer); // Logik
        mServer.addListener(mSyncedUpdate); // Logik

        // Listen to pure ServerModelEvents
        EventHandler.events.listen(ModelEvent.class, this); // Logik
    } // Network

    public void update(float delta) { // Logik
        mSyncedUpdate.update(); // Logik
        mServerWorldModel.update(delta); // Logik
    } // Logik

    @Override // Logik
    public void modelEvent(EventType type, Object... objs) { // Logik
        switch (type) { // Logik
            default: // Logik
                break; // Logik
        } // Logik
    } // Logik
/*
    @Override
    public void connected(Connection con) {
        Log.debug("ServerWorldModel", "Some one connected to the server with id: " + con.getId());

        // Meaning, a new player should be added on the server
        mServerWorldModel.addPlayer(con.getId(), new ExternalPlayer(con.getId(), new Vector3(0, 10, 0), mServerWorldModel.getBulletWorld()));
        // And sending to all clients except the one joined
        mServer.sendToAllTCPExcept(new PlayerPacket(con.getId(), 0f, 10f, 0f), con);
    }

    @Override
    public void disconnected(Connection con) {

    }

    @Override
    public void received(Connection con, Object obj) {
        mSyncedUpdate.addObject(con, obj);

        if(obj instanceof RequestInitialStatePacket) {
            Log.debug("ServerWorldModel", "Initial state requested from clientId: " + con.getId());

            // Sending all the players to the client requested, except self
            for(Integer i : mServerWorldModel.getPlayers().keySet()) {
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
            ExternalPlayer p = mServerWorldModel.getPlayer(pmp.clientId);
            if(p == null) return;

            p.setPositionAndRotation(pmp.mPosition, new Vector2(pmp.mRotX, pmp.mRotY));
            //p.setPosition(pmp.mPosition);

            // Notify the other clients, if any
            //Vector3 tmp = p.getPosition();
            mServer.sendToAllUDPExcept(new PlayerMovePacket(pmp.clientId, pmp.mPosition, pmp.mRotX, pmp.mRotY), con);
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
            MyContactListener.CollisionObject co = mServerWorldModel.getContactListener().checkShootCollision(mServerWorldModel.getBulletWorld().getCollisionWorld(), new Vector3[]{sp.mFrom, sp.mTo});

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

            //Add info to create grenade
            Grenade g = new Grenade(getBulletWorld(), gcp.position, gcp.direction);
            g.setId(Utils.getInstance().generateUniqueId());
            mToCreate.add(g);

            // A player wants to throw a grenade!
            ExternalPlayer ep = getPlayer(gcp.clientId);
            if(ep != null) {
                ep.grenadeThrow();
            }

            mServer.sendToAllTCP(new GrenadeCreatePacket(g.getId(), ep.getPosition(), ep.getDirection()));
        }
    }*/
}
