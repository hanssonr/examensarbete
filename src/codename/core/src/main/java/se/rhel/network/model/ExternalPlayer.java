package se.rhel.network.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import se.rhel.model.component.*;
import se.rhel.model.entity.DummyEntity;
import se.rhel.model.physics.BulletWorld;

/**
 * Group: Multiplayer
 *
 * Created by Emil on 2014-03-06.
 * assigned to libgdx-gradle-template in se.rhel.model
 */
public class ExternalPlayer extends DummyEntity {

    private static Vector2 PLAYERSIZE = new Vector2(0.6f, 1.2f);
    private static int MAX_HEALTH = 100;

    private INetwork mNetworkComponent;

    public ExternalPlayer(int clientId, Vector3 position, BulletWorld world) {
        super(world, PLAYERSIZE.x, PLAYERSIZE.y, MAX_HEALTH, 7f, position);

        mNetworkComponent = createNetworkComponent(clientId);
        getTransformation().setTranslation(position);
    }

    public int getNetworkID() {
        return mNetworkComponent.getID();
    }

    public void update(float delta) {
        mPhysicsComponent.getBody().setCenterOfMassTransform(getTransformation());
    }
}
