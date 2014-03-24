package se.rhel.model;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import se.rhel.model.entity.DamageableEntity;
import se.rhel.model.entity.DummyEntity;
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
    private ArrayList<DamageableEntity> mDestroy = new ArrayList<>();

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

    public void checkShootCollision(Vector3[] fromTo) {
        Vector3 from = fromTo[0];
        Vector3 to = fromTo[1];

        //Create ray
        ClosestRayResultCallback res = new ClosestRayResultCallback(from, to);

        //Check if it collides with anything that could loose health
        getBulletWorld().getCollisionWorld().rayTest(from, to, res);

        if(res.hasHit()) {
            btCollisionObject obj = res.getCollisionObject();

            if(obj.isStaticOrKinematicObject()) {
                btVector3 v = res.getHitPointWorld();
                btVector3 t = res.getHitNormalWorld();
                BulletHoleRenderer.addBullethole(new Vector3(v.getX(), v.getY(), v.getZ()), new Vector3(t.getX(), t.getY(), t.getZ()).nor());
                v.dispose();
                t.dispose();
            }

            Object hit = obj.userData;

            if(hit instanceof DamageableEntity) {
                ((DamageableEntity) hit).damageEntity(25);
                System.out.println("HIT: " + ((DamageableEntity) hit).getHealth());
                if(!((DamageableEntity) hit).isAlive()) mDestroy.add((DamageableEntity) hit);

                //mShootCollisions.add(obj);
            }
//            else if (obj.userData == 99) {
//                final btRigidBody body = (btRigidBody)(obj);
//                body.activate();
//                Vector3 dir = to.cpy().sub(from);
//                dir.nor();
//                body.applyCentralImpulse(dir.scl(20f));
//            }
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
