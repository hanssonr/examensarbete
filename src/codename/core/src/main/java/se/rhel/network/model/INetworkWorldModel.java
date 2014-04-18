package se.rhel.network.model;

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
}
