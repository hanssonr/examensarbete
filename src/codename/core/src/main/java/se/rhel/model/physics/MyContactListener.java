package se.rhel.model.physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import se.rhel.model.Player;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.weapon.IExplodable;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.view.BulletHoleRenderer;

import java.util.ArrayList;

/**
 * Group Logic
 *
 * Created by Emil on 2014-03-21.
 */
public class MyContactListener extends ContactListener {

    // Represents a shot hit by player
    public static class CollisionObject {
        public enum CollisionType {
            ENTITY, WORLD;
        }

        public CollisionType type;
        public DamageAbleEntity entity;
        public Vector3 hitPoint, hitNormal;

        private CollisionObject(DamageAbleEntity dae) {
            entity = dae;
            type = CollisionType.ENTITY;
        }

        private CollisionObject(Vector3 hitPoint, Vector3 hitNormal) {
            this.hitNormal = hitNormal;
            this.hitPoint = hitPoint;
            type = CollisionType.WORLD;
        }

        public static CollisionObject construct(DamageAbleEntity dae) {
            return new CollisionObject(dae);
        }

        public static CollisionObject construct(Vector3 hitPoint, Vector3 hitNormal) {
            return new CollisionObject(hitPoint, hitNormal);
        }
    }

    /**
     * Does a ray-test against collision world
     * @param world the bullet collision world
     * @param ray RayVector obj containing from and to vector3
     * @return CollisionObject of type DamageAbleEntity or a world hit point
     */
    public CollisionObject checkShootCollision(btCollisionWorld world, RayVector ray) {
        //Create ray
        ClosestRayResultCallback res = new ClosestRayResultCallback(ray.getFrom(), ray.getTo());

        //Check if it collides with anything that could loose health
        world.rayTest(ray.getFrom(), ray.getTo(), res);

        if(res.hasHit()) {
            CollisionObject returnVal = null;
            btCollisionObject obj = res.getCollisionObject();

            if(obj.isStaticOrKinematicObject()) {
                btVector3 v = res.getHitPointWorld();
                btVector3 t = res.getHitNormalWorld();
                returnVal = CollisionObject.construct(new Vector3(v.getX(), v.getY(), v.getZ()), new Vector3(t.getX(), t.getY(), t.getZ()).nor());
                v.dispose();
                t.dispose();
            }

            Object hit = obj.userData;
            if(hit instanceof DamageAbleEntity) {
                returnVal = CollisionObject.construct((DamageAbleEntity)hit);
            }

            return returnVal;
        }
        return null;
    }



    public ArrayList<DamageAbleEntity> checkExplosionCollision(btCollisionWorld world, IExplodable explosion) {
        ArrayList<DamageAbleEntity> ret = new ArrayList<>();
        btCollisionObjectArray objs = world.getCollisionObjectArray();

        for (int i = 0; i < objs.size(); i++) {
            btCollisionObject obj = objs.at(i);

            if(obj.userData instanceof DamageAbleEntity) {
                DamageAbleEntity de = (DamageAbleEntity) obj.userData;

                double dist = RayVector.getDistance(de.getPosition(), explosion.getPosition());
                if(dist < explosion.getExplosionRadius()) {
                    ret.add(de);
                }
            }
        }

        return ret;
    }

    @Override
    public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {

        if(colObj0.userData instanceof Player) {
            if(colObj1.userData instanceof ExternalPlayer) {
                System.out.println("KARSTEN, VI HAR KONTAKT!!1");
            }
        }

        if(colObj1.userData instanceof Player) {
            if(colObj0.userData instanceof ExternalPlayer) {
                System.out.println("KARSTEN, VI HAR KONTAKT!!1");
            }
        }

    }
}
