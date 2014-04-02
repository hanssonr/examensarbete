package se.rhel.model;

import com.badlogic.gdx.math.Vector3;
import se.rhel.model.entity.GameObject;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.physics.BulletWorld;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.util.Utils;
import se.rhel.model.weapon.Grenade;
import se.rhel.view.BulletHoleRenderer;

import java.util.ArrayList;

/**
 * Created by rkh on 2014-03-28.
 */
public class BaseWorldModel {

    private BulletWorld mBulletWorld;

    protected ArrayList<GameObject> mDestroy = new ArrayList<>();
    protected ArrayList<Grenade> mGrenades = new ArrayList<>();
    private volatile ArrayList<Grenade> mToAdd = new ArrayList<>();

    public BaseWorldModel() {
        mBulletWorld = new BulletWorld();
    }

    public BulletWorld getBulletWorld() {
        return mBulletWorld;
    }

    public void update(float delta) {
        mBulletWorld.update(delta);

        if(mToAdd.size() > 0) {
            System.out.println("    >ToAdd is > 0, size: " + mToAdd.size());
            for(Grenade g : mToAdd) {
                g.createPhysicBody();
                mGrenades.add(g);
            }
            mToAdd.clear();
        }

        for (int i = 0; i < mGrenades.size(); i++) {
            Grenade g = mGrenades.get(i);

            g.update(delta);

            if(!g.isAlive()) {
                mDestroy.add(g);
                mGrenades.remove(i);
            }
        }

        for (int i = 0; i < mDestroy.size(); i++) {
            mDestroy.get(i).destroy();
            mDestroy.remove(i);
        }
    }

    public void checkShootCollision(Vector3[] rays) {
        // Check shoot collision local
        MyContactListener.CollisionObject co = MyContactListener.checkShootCollision(getBulletWorld().getCollisionWorld(), rays);
        // If we have hit the world, just draw a bullethole (it doesn't matter if the server says otherwise)
        if(co != null && co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
            // Draw bullethole
            EventHandler.events.notify(new ModelEvent(EventType.BULLET_HOLE, co.hitPoint, co.hitNormal));
        }
    }

    public Grenade addGrenade(Vector3 position, Vector3 direction) {
        Grenade g = new Grenade(getBulletWorld(), position, direction);
        g.setId(Utils.getInstance().generateUniqueId());
        mToAdd.add(g);
        return g;
    }

    public ArrayList<Grenade> getGrenades() {
        return mGrenades;
    }
}
