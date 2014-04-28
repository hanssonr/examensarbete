package se.rhel.network.controller;

import com.badlogic.gdx.math.Vector3;
import se.rhel.Connection;
import se.rhel.Server;
import se.rhel.event.*;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.network.model.ServerWorldModel;
import se.rhel.network.packet.BulletHolePacket;
import se.rhel.network.packet.DamagePacket;
import se.rhel.network.packet.DeadEntityPacket;

/**
 * Group: Multiplayer
 * Created by Emil on 2014-04-02.
 */
public class ServerController implements ServerModelListener {

    private ServerWorldModel mServerWorldModel;
    private ServerSynchronizedUpdate mSyncedUpdate;
    private Server mServer;
    private Events mEvents;

    public ServerController(Server server) {
        mServer = server;
        mEvents = new Events();
        mServerWorldModel = new ServerWorldModel(mEvents);
        mSyncedUpdate = new ServerSynchronizedUpdate(mServerWorldModel, server, mEvents);
        server.addListener(mSyncedUpdate);

        // Listen to pure ServerModelEvents
        mEvents.listen(ServerModelEvent.class, this);
    }

    public void update(float delta) {
        mSyncedUpdate.update();
        mServerWorldModel.update(delta);
    }

    @Override
    public void serverModelEvent(EventType type, Object... objs) {
        switch(type) {
            case SERVER_WORLD_COLLISION:
                Vector3 hitPoint = ((Vector3)objs[0]);
                Vector3 hitNormal =  ((Vector3)objs[1]);
                Connection con = ((Connection)objs[2]);
                mServer.sendToAllUDPExcept(new BulletHolePacket(hitPoint, hitNormal), con);
                break;
            case DAMAGE:
                System.out.println("SERVER DAMAGE PACKA");
                mServerWorldModel.checkEntityStatus((ExternalPlayer)objs[0]);
                mServer.sendToAllTCP(new DamagePacket(((ExternalPlayer)objs[0]).getNetworkID(), 25));
                break;
            case SERVER_DEAD_ENTITY:
                mServer.sendToAllTCP(new DeadEntityPacket(((ExternalPlayer)objs[0]).getNetworkID()));
                break;
            default:
                break;
        }

    }
}
