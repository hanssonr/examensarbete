package se.rhel.network.model;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.softbody.btSoftBodySolverOutput;
import com.badlogic.gdx.utils.Array;
import se.rhel.Connection;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.event.ServerModelEvent;
import se.rhel.model.BaseWorldModel;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.physics.RayVector;
import se.rhel.Server;
import se.rhel.model.weapon.Explosion;
import se.rhel.network.controller.ServerSynchronizedUpdate;

import java.util.HashMap;

/**
 * Group: Multiplayer
 */
public class ServerWorldModel extends BaseWorldModel {

    // Linkin ID's and Players
    private HashMap<Integer, ExternalPlayer> mPlayers;
    private Array<ConnectionWrappedObject> mUnsolvedCollisions = new Array<>();

    public ServerWorldModel() {
        super();
        mPlayers = new HashMap<>();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        for(ExternalPlayer p : mPlayers.values()) {
            p.update(delta);
        }

        for(ConnectionWrappedObject co : mUnsolvedCollisions) {
            handleCollision((MyContactListener.CollisionObject)co.getObject(), co.getConnection());
        }

        mUnsolvedCollisions.clear();
    }

    public void checkShootCollision(RayVector ray, Connection con) {
        MyContactListener.CollisionObject co = super.getShootCollision(ray);
        mUnsolvedCollisions.add(new ConnectionWrappedObject(con, co));
    }

    public void handleCollision(MyContactListener.CollisionObject co, Connection con) {
        if(co == null) return;

        if(co.type == MyContactListener.CollisionObject.CollisionType.ENTITY) {
            if(co.entity instanceof DamageAbleEntity) {
                damageEntity(co.entity, 25);
            }
        }
        else if(co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
            EventHandler.events.notify(new ServerModelEvent(EventType.SERVER_WORLD_COLLISION, co.hitPoint, co.hitNormal, con));
        }
    }

    public void checkEntityStatus(DamageAbleEntity entity) {
        if(entity.isAlive() && entity.getHealth() <= 0) {
            entity.setAlive(false);

            Explosion exp = new Explosion(entity.getPosition(), 15, 250);
            handleExplosion(exp);
            EventHandler.events.notify(new ServerModelEvent(EventType.SERVER_DEAD_ENTITY, entity));
        }
    }

    public void damageEntity(DamageAbleEntity entity, int amount) {
        entity.damageEntity(amount);

        EventHandler.events.notify(new ServerModelEvent(EventType.DAMAGE, entity));
    }

    public HashMap<Integer, ExternalPlayer> getPlayers() {
        return mPlayers;
    }

    public void addPlayer(int id, ExternalPlayer player) {
        mPlayers.put(id, player);
    }

    public ExternalPlayer getExternalPlayer(int id) {
        return mPlayers.get(id);
    }
}
