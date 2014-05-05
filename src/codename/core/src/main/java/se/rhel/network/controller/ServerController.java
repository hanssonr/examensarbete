package se.rhel.network.controller;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import se.rhel.Connection;
import se.rhel.Server;
import se.rhel.event.*;
import se.rhel.model.component.GameObject;
import se.rhel.model.component.NetworkComponent;
import se.rhel.model.entity.DummyEntity;
import se.rhel.model.physics.RayVector;
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
        mEvents.listen(ServerModelEvents.GameObjectEvent.class, this);
        mEvents.listen(ServerModelEvents.GrenadeEvent.class, this);
        mEvents.listen(ServerModelEvents.ServerWorldCollision.class, this);
        mEvents.listen(ServerModelEvents.ShootEvent.class, this);
    }

    public void update(float delta) {
        mSyncedUpdate.update();
        mServerWorldModel.update(delta);
    }

    @Override
    public void gameObjectEvent(EventType type, GameObject gObj) {
        switch(type) {
            case SERVER_WORLD_COLLISION:
                Vector3 hitPoint = ((Vector3)objs[0]);
                Vector3 hitNormal =  ((Vector3)objs[1]);
                mServer.sendToAllUDP(new BulletHolePacket(hitPoint, hitNormal));
                break;
            case DAMAGE:
                ControlledPlayer cp = (ControlledPlayer) objs[0];
                mServerWorldModel.checkEntityStatus(cp);
                NetworkComponent nc = (NetworkComponent) cp.getComponent(NetworkComponent.class);
                mServer.sendToAllTCP(new DamagePacket(nc.getID(), 25));
                break;
            case SERVER_DEAD_ENTITY: // [0] = GameObject
                mServer.sendToAllUDP(new DeadEntityPacket(((NetworkComponent) ((GameObject) objs[0]).getComponent(NetworkComponent.class)).getID()));
                break;
            case SHOOT: // [0] = RayVector, [1] = GameObject
                mServerWorldModel.checkShootCollision((RayVector)objs[0], (GameObject) objs[1]);
                mServer.sendToAllUDP(new ShootPacket(
                        ((NetworkComponent)((GameObject)objs[1]).getComponent(NetworkComponent.class)).getID(),
                        ((RayVector)objs[0]).getFrom(),
                        ((RayVector)objs[0]).getTo()));
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
                mServer.sendToAllUDP(new PlayerMovePacket(id, ((ControlledPlayer)objs[0]).getPosition(), ((ControlledPlayer)objs[0]).getRotation()));
                break;
            default:
                break;
        }
    }

    @Override
    public void serverWorldCollision(Vector3 hitPoint, Vector3 hitNormal) {
        mServer.sendToAllUDP(new BulletHolePacket(hitPoint, hitNormal));
    }

    @Override
    public void shootEvent(RayVector ray, IPlayer player) {
        mServerWorldModel.checkShootCollision(ray);
        mServer.sendToAllUDP(new ShootPacket(
                ((NetworkComponent)(((GameObject)player).getComponent(NetworkComponent.class))).getID(),
                ray.getFrom(),
                ray.getTo()));
    }

    @Override
    public void grenadeEvent(Grenade g, boolean isAlive) {
        Quaternion q = new Quaternion();
        q = g.getTransformation().getRotation(q);
        mServer.sendToAllUDP(new GrenadeUpdatePacket(((NetworkComponent)g.getComponent(NetworkComponent.class)).getID(), g.getPosition(), q, isAlive));
    }
}
