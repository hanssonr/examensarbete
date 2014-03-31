package se.rhel.model;

import com.badlogic.gdx.math.Vector3;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.physics.BulletWorld;
import se.rhel.model.physics.MyContactListener;
import se.rhel.view.BulletHoleRenderer;

import java.util.ArrayList;

/**
 * Created by rkh on 2014-03-28.
 */
public class BaseWorldModel {

    private BulletWorld mBulletWorld;
    protected ArrayList<DamageAbleEntity> mDestroy = new ArrayList<>();


    public BaseWorldModel() {
        mBulletWorld = new BulletWorld();
    }

    public BulletWorld getBulletWorld() {
        return mBulletWorld;
    }

    public void update(float delta) {
        mBulletWorld.update(delta);

        for (int i = 0; i < mDestroy.size(); i++) {
            mDestroy.get(i).destroy();
            i++;
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
}
