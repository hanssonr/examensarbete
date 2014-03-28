package se.rhel.model;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.network.model.ExternalPlayer;

/**
 * Group: Logic
 *
 * Created by rkh on 2014-03-21.
 */
public class WorldModel extends BaseWorldModel implements BaseModel, IWorldModel {

    private Player mPlayer;
    private Array<ExternalPlayer> mPlayers = new Array<>();

    public WorldModel() {
        super();
        mPlayer = new Player(new Vector3(0, 20, 0), getBulletWorld());
    }

    @Override
    public void create() {
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float delta) {
        super.update(delta);
        mPlayer.update(delta);
    }

    public Player getPlayer() {
        return mPlayer;
    }

    @Override
    public Array<ExternalPlayer> getExternalPlayers() {
        return mPlayers;
    }

}
