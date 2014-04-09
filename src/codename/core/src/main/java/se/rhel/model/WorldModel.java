package se.rhel.model;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.entity.DummyEntity;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.entity.DamageAbleEntity;

import java.util.ArrayList;

/**
 * Group: Logic
 *
 * Created by rkh on 2014-03-21.
 */
public class WorldModel extends BaseWorldModel implements BaseModel, IWorldModel {

    private Player mPlayer;
    protected ArrayList<DamageAbleEntity> mDestroy = new ArrayList<>();
    private Array<IPlayer> mPlayers = new Array<>();

    public WorldModel() {
        super();
        mPlayer = new Player(new Vector3(0, 20, 0), getBulletWorld());

        mPlayers.add(new DummyEntity(getBulletWorld(), 0.7f, 1.6f, 100, 7f, new Vector3(5, 20, 10)));
        mPlayers.add(new DummyEntity(getBulletWorld(), 0.7f, 1.6f, 100, 7f, new Vector3(20, 10, -10)));
        mPlayers.add(new DummyEntity(getBulletWorld(), 0.7f, 1.6f, 100, 7f, new Vector3(-10, 50, -10)));
        mPlayers.add(new DummyEntity(getBulletWorld(), 0.7f, 1.6f, 100, 7f, new Vector3(-15, 5, 15)));
        mPlayers.add(new DummyEntity(getBulletWorld(), 0.7f, 1.6f, 100, 7f, new Vector3(0, 100, -5)));
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

        for(int i = 0; i < mPlayers.size; i++) {
            DummyEntity de = (DummyEntity)mPlayers.get(i);
            de.update(delta);
        }
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public Array<IPlayer> getExternalPlayers() {
        return mPlayers;
    }

}
