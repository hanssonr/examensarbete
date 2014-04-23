package se.rhel.network.model;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
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
import se.rhel.model.weapon.Grenade;
import se.rhel.model.weapon.IExplodable;
import se.rhel.network.controller.ServerSynchronizedUpdate;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Group: Multiplayer
 */
public class ServerWorldModel extends BaseWorldModel {

    // Linkin ID's and Players
    private HashMap<Integer, ExternalPlayer> mPlayers;

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

        //Update grenades
        for (int i = 0; i < mGrenades.size; i++) {
            Grenade g = mGrenades.get(i);

            g.update(delta);

            if(!g.isAlive()) {
                handleExplosion(getAffectedByExplosion(g), g);
                g.destroy();
                mGrenades.removeIndex(i);
            }
        }
    }

    public RayVector checkShootCollision(RayVector ray, Connection con) {
        MyContactListener.CollisionObject co = super.getShootCollision(ray);
        RayVector ret = new RayVector(ray.getFrom(), ray.getTo());

        if(co != null) {
            if(co.type == MyContactListener.CollisionObject.CollisionType.ENTITY) {
                if(co.entity instanceof DamageAbleEntity) {
                    damageEntity(co.entity, 25);
                    ret.setTo(co.entity.getPosition());
                }
            }
            else if(co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
                ret.setTo(co.hitPoint);
                EventHandler.events.notify(new ServerModelEvent(EventType.SERVER_WORLD_COLLISION, co.hitPoint, co.hitNormal, con));
            }
        }

        return ret;
    }

    public void handleExplosion(ArrayList<DamageAbleEntity> hit, IExplodable exp) {
        for(DamageAbleEntity entity : hit) {
            entity.damageEntity(exp.getExplosionDamage());
            EventHandler.events.notify(new ServerModelEvent(EventType.DAMAGE, entity));
        }
    }

    public void checkEntityStatus(DamageAbleEntity entity) {
        if(entity.isAlive() && entity.getHealth() <= 0) {
            entity.setAlive(false);

            Explosion exp = new Explosion(entity.getPosition(), 15, 250);
            handleExplosion(getAffectedByExplosion(exp), exp);
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
