package se.rhel.model;

import com.badlogic.gdx.utils.Array;
import se.rhel.model.entity.IEntity;
import se.rhel.model.physics.BulletWorld;

/**
 * Created by rkh on 2014-03-28.
 */
public interface IWorldModel {

    public Player getPlayer();
    public BulletWorld getBulletWorld();
    public Array<IEntity> getExternalPlayers();
}
