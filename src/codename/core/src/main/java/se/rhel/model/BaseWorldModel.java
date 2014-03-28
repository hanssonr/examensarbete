package se.rhel.model;

import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.physics.BulletWorld;

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
}
