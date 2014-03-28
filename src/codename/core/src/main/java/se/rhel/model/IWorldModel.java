package se.rhel.model;

import com.badlogic.gdx.utils.Array;
import se.rhel.model.physics.BulletWorld;
import se.rhel.network.model.ExternalPlayer;

/**
 * Created by rkh on 2014-03-28.
 */
public interface IWorldModel {

    public Player getPlayer();
    public BulletWorld getBulletWorld();
    public Array<ExternalPlayer> getExternalPlayers();
}
