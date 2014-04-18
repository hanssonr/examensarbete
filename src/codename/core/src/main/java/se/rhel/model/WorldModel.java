package se.rhel.model;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.model.entity.DummyEntity;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Explosion;
import se.rhel.model.weapon.Grenade;

import java.util.ArrayList;

/**
 * Group: Logic
 *
 * Created by rkh on 2014-03-21.
 */
public class WorldModel extends BaseWorldModel implements IWorldModel {

    private Player mPlayer;
    private Array<IPlayer> mPlayers = new Array<>();

    public WorldModel() {
        super();
        mPlayer = new Player(new Vector3(0, 20, 0), getBulletWorld());

        mPlayers.add(new DummyEntity(getBulletWorld(), 0.7f, 1.6f, 100, 7f, new Vector3(0, 5, 0)));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        mPlayer.update(delta);

        for(int i = 0; i < mPlayers.size; i++) {
            DummyEntity de = (DummyEntity)mPlayers.get(i);
            de.update(delta);
        }
    }

    @Override
    public void checkShootCollision(RayVector ray) {
        MyContactListener.CollisionObject co = super.getShootCollision(ray);

        if(co != null) {
            if(co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
                EventHandler.events.notify(new ModelEvent(EventType.BULLET_HOLE, co.hitPoint, co.hitNormal));
            }
            else if(co.type == MyContactListener.CollisionObject.CollisionType.ENTITY) {
                damageEntity(co.entity, 25);
            }
        }
    }

    public void checkEntityStatus(DamageAbleEntity entity) {
        if(entity.isAlive() && entity.getHealth() < 0) {
            entity.setAlive(false);

            Explosion exp = new Explosion(entity.getPosition(), 15, 25);
            EventHandler.events.notify(new ModelEvent(EventType.EXPLOSION, exp));
            handleExplosion(exp);
        }
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public Array<IPlayer> getExternalPlayers() {
        return mPlayers;
    }

}
