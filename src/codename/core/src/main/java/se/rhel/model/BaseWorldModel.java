package se.rhel.model;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.entity.GameObject;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.model.physics.BulletWorld;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Grenade;
import se.rhel.model.weapon.IExplodable;

import java.util.ArrayList;

/**
 * Group: Logic
 * Created by rkh on 2014-03-28.
 */
public class BaseWorldModel {

    private final int MAX_GRENADES = 25;

    private BulletWorld mBulletWorld;
    private MyContactListener mContactListener = new MyContactListener();

    protected ArrayList<GameObject> mDestroy = new ArrayList<>();
    protected Array<Grenade> mGrenades = new Array<Grenade>(true, MAX_GRENADES);

    public BaseWorldModel() {
        mBulletWorld = new BulletWorld();
    }

    public BulletWorld getBulletWorld() {
        return mBulletWorld;
    }
    public MyContactListener getContactListener() { return mContactListener; }

    public void update(float delta) {
        mBulletWorld.update(delta);

        for (int i = 0; i < mGrenades.size; i++) {
            Grenade g = mGrenades.get(i);

            g.update(delta);

            if(!g.isAlive()) {
                EventHandler.events.notify(new ModelEvent(EventType.EXPLOSION, g.getPosition()));
                handleExplosion(g);
                g.destroy();
                mGrenades.removeIndex(i);
            }
        }

        for (int i = 0; i < mDestroy.size(); i++) {
            mDestroy.get(i).destroy();
            mDestroy.remove(i);
        }
    }

//    public void checkShootCollision(RayVector ray) {
//        MyContactListener.CollisionObject co = mContactListener.checkShootCollision(getBulletWorld().getCollisionWorld(), ray);
//
//        //this could hit nothing e.g the sky
//        if (co != null) {
//            if(co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
//                // Draw bullethole
//                EventHandler.events.notify(new ModelEvent(EventType.BULLET_HOLE, co.hitPoint, co.hitNormal));
//            }
//            else if (co.type == MyContactListener.CollisionObject.CollisionType.ENTITY) {
//                co.entity.damageEntity(25);
//                EventHandler.events.notify(new ModelEvent(EventType.DAMAGE, co.entity));
//            }
//        }
//    }

    public MyContactListener.CollisionObject getShootCollision(RayVector ray) {
        return mContactListener.checkShootCollision(getBulletWorld().getCollisionWorld(), ray);
    }

    public void handleExplosion(IExplodable explosion) {
        mContactListener.checkExplosionCollision(getBulletWorld().getCollisionWorld(), explosion);
    }

    public void addGrenade(Grenade g) {
        mGrenades.add(g);
    }

    public void damageEntity(DamageAbleEntity entity, int amount) {
        entity.damageEntity(amount);
    }

    public Array<Grenade> getGrenades() {
        return mGrenades;
    }
}
