package se.rhel.network.model;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.model.entity.DummyEntity;
import se.rhel.model.entity.IEntity;
import se.rhel.model.physics.BulletWorld;
import se.rhel.res.Resources;

/**
 * Group: Network
 *
 * Created by Emil on 2014-03-06.
 * assigned to libgdx-gradle-template in se.rhel.model
 */
public class ExternalPlayer extends DummyEntity implements IEntity {

    private static Vector2 mPlayersize = new Vector2(0.6f, 1.5f);
    private static int MAX_HEALTH = 100;
    private int mClientId;

    public ExternalPlayer(int clientId, Vector3 position, BulletWorld world) {
        super(world, mPlayersize.x, mPlayersize.y, MAX_HEALTH, 7, position);
        mClientId = clientId;
        getTransformation().setTranslation(position);
    }

    public void update(float delta) {
        super.update(delta);
    }

    public void setPositionAndRotation(Vector3 pos, Vector2 rotation) {
        getRotation().set(rotation);
        setPosition(pos);
        calculateDirection();

        super.setPositionAndRotation(pos, rotation.x);
    }

    public int getClientId() {
        return mClientId;
    }
}
