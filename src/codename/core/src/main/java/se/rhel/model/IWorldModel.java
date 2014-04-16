package se.rhel.model;

import com.badlogic.gdx.utils.Array;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.BulletWorld;
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

    Array<Grenade> getGrenades();
}
