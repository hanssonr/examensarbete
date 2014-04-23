package se.rhel.model;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.BulletWorld;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Grenade;

import java.util.ArrayList;

/**
 * Group: Logic
 * Created by rkh on 2014-03-28.
 */
public interface IWorldModel {

    public Player getPlayer();
    public BulletWorld getBulletWorld();
    public Array<IPlayer> getExternalPlayers();

    public void update(float delta);
    public void checkShootCollision(RayVector ray);
    public void addGrenade(Grenade g);
    public void checkEntityStatus(DamageAbleEntity entity);

    Array<Grenade> getGrenades();
}
