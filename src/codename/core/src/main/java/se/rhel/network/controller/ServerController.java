package se.rhel.network.controller;

import com.badlogic.gdx.math.Vector3;
import se.rhel.Connection;
import se.rhel.Server;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.event.ModelListener;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.network.model.ServerWorldModel;
import se.rhel.network.packet.BulletHolePacket;
import se.rhel.network.packet.DamagePacket;
import se.rhel.network.packet.DeadEntityPacket;

/**
 * Group: Multiplayer
 * Created by Emil on 2014-04-02.
 */
public class ServerController implements ModelListener {

    private ServerWorldModel mServerWorldModel;
    private ServerSynchronizedUpdate mSyncedUpdate;
    private Server mServer;

    public ServerController(Server server) {
        mServer = server;
        mServerWorldModel = new ServerWorldModel();
        mSyncedUpdate = new ServerSynchronizedUpdate(mServerWorldModel, server);
        server.addListener(mSyncedUpdate);

        // Listen to pure ServerModelEvents
        EventHandler.events.listen(ModelEvent.class, this);
    }

    public void update(float delta) {
        mSyncedUpdate.update();
        mServerWorldModel.update(delta);
    }

    @Override
    public void modelEvent(EventType type, Object... objs) {
        switch (type) {
            case SERVER_WORLD_COLLISION:
                Vector3 hitPoint = ((Vector3)objs[0]);
                Vector3 hitNormal =  ((Vector3)objs[1]);
                Connection con = ((Connection)objs[2]);
                mServer.sendToAllUDPExcept(new BulletHolePacket(hitPoint, hitNormal), con);
                break;
            case SERVER_DAMAGED_ENTITY:
                mServerWorldModel.checkEntityStatus((ExternalPlayer)objs[0]);
                mServer.sendToAllTCP(new DamagePacket(((ExternalPlayer)objs[0]).getClientId(), 25));
                break;
            case SERVER_DEAD_ENTITY:
                mServer.sendToAllTCP(new DeadEntityPacket(((ExternalPlayer)objs[0]).getClientId()));
                break;
            //This event also happen on client :'(  bad?
            case DAMAGE:
                mServerWorldModel.checkEntityStatus((DamageAbleEntity)objs[0]);
                break;
            default:
                break;
        }
    }
}
