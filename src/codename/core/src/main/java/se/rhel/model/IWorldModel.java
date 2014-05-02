package se.rhel.model;

import com.badlogic.gdx.utils.Array;
import se.rhel.model.component.GameObject;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.entity.Player;
import se.rhel.model.physics.BulletWorld;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Grenade;

/**
 * Group: Logic
 * Created by rkh on 2014-03-28.
 */
public interface IWorldModel {

    public Player getPlayer();
    public BulletWorld getBulletWorld();
    public Array<IPlayer> getControlledPlayers();
    public Array<IPlayer> getAllPlayers();
    public void setPlayer(int id, IPlayer player);

    public void update(float delta);
    public void checkShootCollision(RayVector ray);
    public void addGrenade(Grenade g);
    public void checkEntityStatus(GameObject entity);
}
