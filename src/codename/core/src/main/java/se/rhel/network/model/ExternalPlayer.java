package se.rhel.network.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import se.rhel.model.entity.DummyEntity;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.BulletWorld;

/**
 * Group: Multiplayer
 *
 * Created by Emil on 2014-03-06.
 * assigned to libgdx-gradle-template in se.rhel.model
 */
public class ExternalPlayer extends DummyEntity implements IPlayer {

    private static Vector2 mPlayersize = new Vector2(0.6f, 1.5f);
    private static int MAX_HEALTH = 100;
    private int mClientId;

    public ExternalPlayer(int clientId, Vector3 position, BulletWorld world) {
        super(world, mPlayersize.x, mPlayersize.y, MAX_HEALTH, 7, position);
        mClientId = clientId;
        getTransformation().setTranslation(position);
    }

    public int getClientId() {
        return mClientId;
    }
}
