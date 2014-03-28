package se.rhel.model;

import com.badlogic.gdx.math.Vector3;
import se.rhel.event.Events;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.entity.DummyEntity;
import se.rhel.model.physics.BulletWorld;
import se.rhel.model.physics.MyContactListener;
import se.rhel.view.BulletHoleRenderer;

import java.util.ArrayList;

/**
 * Group: Logic
 *
 * Created by rkh on 2014-03-21.
 */
public class WorldModel implements BaseModel {

    private FPSCamera mCamera;
    private Player mPlayer;
    private BulletWorld mBulletWorld;
    private DummyEntity dummyplayer;

    protected ArrayList<DamageAbleEntity> mDestroy = new ArrayList<>();

    public WorldModel() {
        mBulletWorld = new BulletWorld();
        //create();
    }

    @Override
    public void create() {
        mCamera = new FPSCamera(75, 0.1f, 1000f);
        mPlayer = new Player(new Vector3(0, 20, 0), mBulletWorld);
        mPlayer.attachCamera(mCamera);

        //Dummyplayer
        //dummyplayer = new DummyEntity(mBulletWorld, 0.6f, 1.5f, new ModelInstance(Resources.INSTANCE.playerModelAnimated), 100, 7f, new Vector3(10, 10, 15));
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float delta) {
        mBulletWorld.update(delta);
        mPlayer.update(delta);
        //dummyplayer.update(delta);

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
            BulletHoleRenderer.addBullethole(co.hitPoint, co.hitNormal);
        }
    }

    public BulletWorld getBulletWorld() {
        return mBulletWorld;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public FPSCamera getCamera() {
        return mCamera;
    }

}
