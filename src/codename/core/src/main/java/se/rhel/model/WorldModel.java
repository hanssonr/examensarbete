package se.rhel.model;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.event.*;
import se.rhel.model.component.*;
import se.rhel.model.entity.DummyEntity;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.entity.Player;
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
        updatePlayer(delta);

        for(int i = 0; i < mPlayers.size; i++) {
            DummyEntity de = (DummyEntity)mPlayers.get(i);
            de.update(delta);

            IActionable ac = (IActionable) de.getComponent(ActionComponent.class);
            if(ac.hasShoot()) {
                mEvents.notify(new ModelEvent(EventType.SHOOT, de));
            }
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

    private void updatePlayer(float delta) {
        mPlayer.update(delta);

        if(mPlayer.wantToShoot()) {
            mEvents.notify(new ModelEvent(EventType.SHOOT, mPlayer));
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

    public void handleExplosion(ArrayList<GameObject> hit, IExplodable exp) {
        for(GameObject obj : hit) {
            IDamageable entity = (IDamageable) obj.getComponent(DamageComponent.class);
            entity.damageEntity(exp.getExplosionDamage());
            mEvents.notify(new ModelEvent(EventType.DAMAGE, obj));
        }
    }

    public void checkEntityStatus(GameObject entity) {
        IDamageable da = (IDamageable) entity.getComponent(DamageComponent.class);
        if(da.isAlive() && da.getHealth() <= 0) {
            da.setAlive(false);

            Explosion exp = new Explosion(entity.getPosition(), 15, 250);
            mEvents.notify(new ModelEvent(EventType.EXPLOSION, exp.getPosition()));
            handleExplosion(getAffectedByExplosion(exp), exp);
        }
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public Array<IPlayer> getExternalPlayers() {
        return mPlayers;
    }

}
