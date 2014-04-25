package se.rhel.network.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import se.rhel.model.IWorldModel;
import se.rhel.model.entity.IPlayer;

/**
 * Created by rkh on 2014-04-17.
 */
public interface INetworkWorldModel extends IWorldModel {

    public void addPlayer(int id, ExternalPlayer player);
    public ExternalPlayer getExternalPlayer(int id);
    public IPlayer getPlayerEntity(int id);
    public void damageEntity(int id, int amount);
    public void killEntity(int id);
    public void transformEntity(int clientId, Vector3 position, Vector2 rotation);
}
