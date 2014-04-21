package se.rhel.model.weapon;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by rkh on 2014-04-18.
 */
public class Explosion implements IExplodable {

    private float mExplosionRadius;
    private Vector3 mPosition;
    private int mDamage;

    public Explosion(Vector3 positon, float radius, int damageAmount) {
        mPosition = positon;
        mExplosionRadius = radius;
        mDamage = damageAmount;
    }

    @Override
    public Vector3 getPosition() {
        return mPosition;
    }

    @Override
    public float getExplosionRadius() {
        return mExplosionRadius;
    }

    @Override
    public int getExplosionDamage() {
        return mDamage;
    }
}
