package se.rhel.model.entity;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import se.rhel.model.physics.BulletWorld;
import se.rhel.res.Resources;

/**
 * Group: Logic
 *
 * Created by rkh on 2014-03-24.
 */
public class DummyEntity extends DamageAbleEntity {

    public static final String ANIMATION_IDLE = "idle";
    public static final String ANIMATION_WALK = "walk";

    private Vector2 mSize;
    private AnimationController mAnimationController;
    private ModelInstance mAnimated;
    private BoundingBox mBox;

    private Vector3 mLastKnownPosition, mCurrentPosition;

    public DummyEntity(BulletWorld world, float radius, float height, ModelInstance instance, int maxHealth, float movespeed, Vector3 position) {
        super(world, maxHealth, movespeed);
        mLastKnownPosition = new Vector3();
        mCurrentPosition = new Vector3();
        mAnimated = instance;
        mSize = new Vector2(radius, height);
        mBox = mAnimated.model.calculateBoundingBox(new BoundingBox());

        getTransformation().setTranslation(position);
        createPhysicBody();
    }

    public void createPhysicBody() {
        btCollisionShape shape = new btCapsuleShape(mSize.x, mSize.y);
        btRigidBodyConstructionInfo info = new btRigidBodyConstructionInfo(5f, null, shape, Vector3.Zero);
        btDefaultMotionState motionstate = new btDefaultMotionState(getTransformation());

        mAnimationController = new AnimationController(mAnimated);
        mAnimationController.setAnimation(ANIMATION_IDLE, -1);

        super.createPhysicBody(shape, info, motionstate, this);
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

    public void destory() {
        Resources.INSTANCE.modelInstanceArray.removeValue(mAnimated, true);
    }
}
