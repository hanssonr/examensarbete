package se.rhel.network.model;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import se.rhel.model.entity.DummyEntity;
import se.rhel.model.physics.BulletWorld;
import se.rhel.model.entity.DynamicEntity;
import se.rhel.res.Resources;

/**
 * Group: Network
 *
 * Created by Emil on 2014-03-06.
 * assigned to libgdx-gradle-template in se.rhel.model
 */
public class ExternalPlayer extends DummyEntity {

    public static final String ANIMATION_IDLE = "idle";
    public static final String ANIMATION_WALK = "walk";

    private static Vector2 mPlayersize = new Vector2(0.6f, 1.5f);
    private static int MAX_HEALTH = 100;

    public enum PLAYERSTATE {
        idle, running
    }

    private PLAYERSTATE mState;

    private AnimationController mAnimationController;
    private ModelInstance mAnimated;
    private Quaternion mRotation;
    private BoundingBox mBox;

    private Vector3 mLastKnownPosition, mCurrentPosition;

    private int mClientId;

    public ExternalPlayer(int clientId, Vector3 position, BulletWorld world) {
        super(world, mPlayersize.x, mPlayersize.y, new ModelInstance(Resources.INSTANCE.playerModelAnimated), MAX_HEALTH, 7, position);
        mClientId = clientId;
        mRotation = new Quaternion();
        mLastKnownPosition = new Vector3();
        mCurrentPosition = new Vector3();
        mAnimated = getInstance();
        mBox = mAnimated.model.calculateBoundingBox(new BoundingBox());

        getTransformation().setTranslation(position);

        mAnimationController = new AnimationController(mAnimated);
        mAnimationController.setAnimation(ANIMATION_IDLE, -1);

        mState = PLAYERSTATE.idle;
    }

    public void update(float delta) {
        if(mCurrentPosition.dst(mLastKnownPosition) > 0.01f) {
            if(ANIMATION_IDLE.equals(mAnimationController.current.animation.id)) {
                mAnimationController.setAnimation(ANIMATION_WALK, -1, 2f, new AnimationController.AnimationListener() {
                    @Override
                    public void onEnd(AnimationController.AnimationDesc animation) {

                    }

                    @Override
                    public void onLoop(AnimationController.AnimationDesc animation) {
                    }
                });
            }
        } else {
            if(ANIMATION_WALK.equals(mAnimationController.current.animation.id)) {
                mAnimationController.setAnimation(ANIMATION_IDLE, -1);
            }
        }

        mAnimated.transform.getTranslation(mCurrentPosition);
        mAnimationController.update(delta);
        mAnimated.transform.set(getBody().getCenterOfMassTransform());
        mAnimated.transform.setTranslation(getBody().getCenterOfMassPosition().sub(new Vector3(0, mBox.getDimensions().y / 2.0f, 0)));
        mAnimated.transform.getTranslation(mLastKnownPosition);
    }

    public int getClientId() {
        return mClientId;
    }
}
