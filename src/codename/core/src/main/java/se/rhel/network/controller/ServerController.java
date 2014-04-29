package se.rhel.network.controller;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import se.rhel.Connection;
import se.rhel.Server;
import se.rhel.event.*;
import se.rhel.model.component.GameObject;
import se.rhel.model.component.NetworkComponent;
import se.rhel.model.entity.DummyEntity;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.network.model.ServerWorldModel;
import se.rhel.network.packet.*;

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
                DummyEntity de = (DummyEntity) objs[0];
                mServerWorldModel.checkEntityStatus(de);
                NetworkComponent nc = (NetworkComponent) de.getComponent(NetworkComponent.class);
                mServer.sendToAllTCP(new DamagePacket(nc.getID(), 25));
                break;
            case SERVER_DEAD_ENTITY:
                mServer.sendToAllTCP(new DeadEntityPacket(((NetworkComponent)((GameObject)objs[0]).getComponent(NetworkComponent.class)).getID()));
                break;
            case SHOOT:
                break;
            case JUMP:
                break;
            case GRENADE:
                // Low freq update from the WorldModel, should be synched with client
                Grenade g = (Grenade) objs[0];
                boolean isAlive = (boolean) objs[1];
                Quaternion q = new Quaternion();
                q = g.getTransformation().getRotation(q);
                mServer.sendToAllUDP(new GrenadeUpdatePacket(((NetworkComponent)((GameObject)objs[0]).getComponent(NetworkComponent.class)).getID(), g.getPosition(), q, isAlive));
                break;
            case PLAYER_MOVE:
                int id = ((NetworkComponent)((GameObject)objs[0]).getComponent(NetworkComponent.class)).getID();
                mServer.sendToAllUDP(new PlayerMovePacket(id, ((DummyEntity)objs[0]).getPosition(), ((DummyEntity)objs[0]).getRotation()));
                break;
            default:
                break;
        }
    }
}
