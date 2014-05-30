package se.rhel.network.event;

import com.badlogic.gdx.math.Vector3;
import se.rhel.event.EventType;
import se.rhel.event.GameEvent;
import se.rhel.model.component.GameObject;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Grenade;

/**
 * Created by Emil on 2014-05-05.
 */
public class ServerModelEvents {

    public static class GrenadeEvent extends ServerModelEvent {
        Grenade g;
        boolean isAlive;

        public GrenadeEvent(Grenade g, boolean isAlive) {
            this.g = g;
            this.isAlive = isAlive;
        }

        @Override
        public void notify(ServerModelListener listener) {
            listener.grenadeEvent(this.g, this.isAlive);
        }
    }

    public static class ShootEvent extends ServerModelEvent {
        RayVector ray;
        IPlayer player;

        public ShootEvent(RayVector ray, IPlayer player) {
            this.ray = ray;
            this.player = player;
        }

        @Override
        public void notify(ServerModelListener listener) {
            listener.shootEvent(this.ray, this.player);
        }
    }

    public static class GameObjectEvent extends ServerModelEvent {
        EventType type;
        GameObject gObj;

        public GameObjectEvent(EventType type, GameObject gameObject) {
            this.type = type;
            this.gObj = gameObject;
        }

        @Override
        public void notify(ServerModelListener listener) {
            listener.gameObjectEvent(this.type, this.gObj);
        }
    }

    public static class ServerWorldCollision extends ServerModelEvent {
        Vector3 hitPoint;
        Vector3 hitNormal;

        public ServerWorldCollision(Vector3 hitPoint, Vector3 hitNormal) {
            this.hitNormal = hitNormal;
            this.hitPoint = hitPoint;
        }

        @Override
        public void notify(ServerModelListener listener) {
            listener.serverWorldCollision(this.hitPoint, this.hitNormal);
        }
    }

    public static class RespawnEvent extends ServerModelEvent {
        int id;
        Vector3 pos;

        public RespawnEvent(int id, Vector3 position) {
            this.id = id;
            this.pos = position;
        }

        @Override
        public void notify(ServerModelListener listener) {
            listener.respawnEvent(this.id, this.pos);
        }
    }
}
