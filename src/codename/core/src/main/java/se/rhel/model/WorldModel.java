package se.rhel.model;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.event.*;
import se.rhel.model.entity.DummyEntity;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Explosion;
import se.rhel.model.weapon.Grenade;
import se.rhel.model.weapon.IExplodable;

import java.util.ArrayList;

/**
 * Group: Logic
 *
 * Created by rkh on 2014-03-21.
 */
public class WorldModel extends BaseWorldModel implements IWorldModel {

    private Player mPlayer;
    private Array<IPlayer> mPlayers = new Array<>();

    public WorldModel(Events events) {
        super(events);
        mPlayer = new Player(new Vector3(0, 20, 0), getBulletWorld());

        mPlayers.add(new DummyEntity(getBulletWorld(), 0.7f, 1.6f, 100, 7f, new Vector3(10, 5, 0)));
        mPlayers.add(new DummyEntity(getBulletWorld(), 0.7f, 1.6f, 100, 7f, new Vector3(0, 5, 0)));
        mPlayers.add(new DummyEntity(getBulletWorld(), 0.7f, 1.6f, 100, 7f, new Vector3(10, 5, 10)));
        mPlayers.add(new DummyEntity(getBulletWorld(), 0.7f, 1.6f, 100, 7f, new Vector3(0, 5, 10)));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        mPlayer.update(delta);

        for(int i = 0; i < mPlayers.size; i++) {
            DummyEntity de = (DummyEntity)mPlayers.get(i);
            de.update(delta);
        }

        for (int i = 0; i < mGrenades.size; i++) {
            Grenade g = mGrenades.get(i);

            g.update(delta);

            if(!g.isAlive()) {
                mEvents.notify(new ModelEvent(EventType.EXPLOSION, g.getPosition()));
                handleExplosion(getAffectedByExplosion(g), g);
                destroyGameObject(g);
                mGrenades.removeIndex(i);
            }
        }
    }

    @Override
    public void checkShootCollision(RayVector ray) {
        MyContactListener.CollisionObject co = super.getShootCollision(ray);

        if(co != null) {
            if(co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
                mEvents.notify(new ModelEvent(EventType.BULLET_HOLE, co.hitPoint, co.hitNormal));
            }
            else if(co.type == MyContactListener.CollisionObject.CollisionType.ENTITY) {
                damageEntity(co.entity, 25);
                mEvents.notify(new ModelEvent(EventType.DAMAGE, co.entity));
            }

            ray.setTo(co.hitPoint);
        }
    }

    public void handleExplosion(ArrayList<DamageAbleEntity> hit, IExplodable exp) {
        for(DamageAbleEntity entity : hit) {
            entity.damageEntity(exp.getExplosionDamage());
            mEvents.notify(new ModelEvent(EventType.DAMAGE, entity));
        }
    }

    public void checkEntityStatus(DamageAbleEntity entity) {
        if(entity.isAlive() && entity.getHealth() <= 0) {
            Vector3 tmp = entity.getPosition().cpy();
            entity.setAlive(false);

            Explosion exp = new Explosion(tmp, 15, 250);
            mEvents.notify(new ModelEvent(EventType.EXPLOSION, exp.getPosition()));
            handleExplosion(getAffectedByExplosion(exp), exp);
        }
    }

    public void shoot() {
        if(getPlayer().canShoot()) {
            mEvents.notify(new ModelEvent(EventType.SHOOT));
        }
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public Array<IPlayer> getExternalPlayers() {
        return mPlayers;
    }

}
