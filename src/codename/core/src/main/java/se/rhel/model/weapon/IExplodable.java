package se.rhel.model.weapon;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by rkh on 2014-04-05.
 */
public interface IExplodable {

    public Vector3 getPosition();
    public float getExplosionRadius();
    public int getExplosionDamage();
}
