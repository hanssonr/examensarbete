package se.rhel.network.controller;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import se.rhel.Server;
import se.rhel.event.*;
import se.rhel.model.component.GameObject;
import se.rhel.model.component.NetworkComponent;
import se.rhel.model.entity.ControlledPlayer;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.event.ServerModelListener;
import se.rhel.network.event.ServerModelEvents;
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
        mSyncedUpdate = new ServerSynchronizedUpdate();
        server.addListener(mSyncedUpdate);

        // Listen to pure ServerModelEvents
        mEvents.listen(ServerModelEvents.GameObjectEvent.class, this);
        mEvents.listen(ServerModelEvents.GrenadeEvent.class, this);
        mEvents.listen(ServerModelEvents.ServerWorldCollision.class, this);
        mEvents.listen(ServerModelEvents.ShootEvent.class, this);
    }

    public void update(float delta) {
        mSyncedUpdate.update(mServerWorldModel, mServer);
        mServerWorldModel.update(delta);
    }

    @Override
    public void gameObjectEvent(EventType type, GameObject gObj) {
        switch(type) {
            case DAMAGE:
                ControlledPlayer cp = (ControlledPlayer) gObj;
                mServerWorldModel.checkEntityStatus(cp);
                NetworkComponent nc = (NetworkComponent) cp.getComponent(NetworkComponent.class);
                mServer.sendToAllTCP(new DamagePacket(nc.getID(), 25));
                break;
            case SERVER_DEAD_ENTITY: // [0] = GameObject
                mServer.sendToAllUDP(new DeadEntityPacket(((NetworkComponent) ((GameObject) gObj).getComponent(NetworkComponent.class)).getID()));
                break;
            case PLAYER_MOVE:
                int id = ((NetworkComponent)(gObj).getComponent(NetworkComponent.class)).getID();
                mServer.sendToAllUDP(new PlayerMovePacket(id, ((ControlledPlayer)gObj).getPosition(), ((ControlledPlayer)gObj).getRotation()));
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
        mServerWorldModel.checkShootCollision(ray, (GameObject)player);
        mServer.sendToAllUDP(new ShootPacket(
                ((NetworkComponent) (((GameObject) player).getComponent(NetworkComponent.class))).getID(),
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
