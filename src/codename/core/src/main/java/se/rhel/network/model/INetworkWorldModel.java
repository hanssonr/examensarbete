package se.rhel.network.model;

import se.rhel.model.IWorldModel;

/**
 * Created by rkh on 2014-04-17.
 */
public interface INetworkWorldModel extends IWorldModel {

    public void addPlayer(int id, ExternalPlayer player);
    public ExternalPlayer getExternalPlayer(int id);
    public void damageEntity(int id, int amount);
    public void killEntity(int id);
}
