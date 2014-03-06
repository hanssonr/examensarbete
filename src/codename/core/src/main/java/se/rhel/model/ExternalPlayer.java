package se.rhel.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import se.rhel.model.Entity.DynamicEntity;
import se.rhel.res.Resources;
import se.rhel.view.BulletHoleRenderer;

/**
 * Created by Emil on 2014-03-06.
 * assigned to libgdx-gradle-template in se.rhel.model
 */
public class ExternalPlayer extends DynamicEntity {

    public enum PLAYERSTATE {
        idle, running
    }

    private PLAYERSTATE mState;

    private BulletWorld mWorld;
    private btRigidBody mBody;
    private AnimationController mAnimationController;

    private static Vector2 mPlayersize = new Vector2(0.6f, 1.5f);

    public ExternalPlayer(Vector3 position, BulletWorld world) {
        super(7f);
        mWorld = world;

        getTransformation().setTranslation(position);
        createPyshicsBody();

        mState = PLAYERSTATE.idle;
    }

    private void createPyshicsBody() {
        btCollisionShape playerShape = new btCapsuleShape(mPlayersize.x, mPlayersize.y);
        btRigidBodyConstructionInfo playerInfo = new btRigidBodyConstructionInfo(5f, null, playerShape, Vector3.Zero);
        btDefaultMotionState playerMotionState = new btDefaultMotionState(getTransformation());

        mAnimationController = new AnimationController(Resources.INSTANCE.playerModelInstanceAnimated);
        mAnimationController.setAnimation("walk", -1);

        mBody = new btRigidBody(playerInfo);
        mBody.setMotionState(playerMotionState);
        mBody.setGravity(Vector3.Zero);

        mWorld.addToWorld(playerShape,
                playerInfo,
                playerMotionState,
                Resources.INSTANCE.playerModelInstanceAnimated,
                mBody);
    }

    public void update(float delta) {
        mAnimationController.update(delta);
        mBody.setGravity(Vector3.Zero);
        mTransformation.set(mBody.getCenterOfMassTransform());

    }

    public void move(Vector3 direction) {
        mBody.activate(true);
        direction.x *= mMovespeed;
        direction.z *= mMovespeed;
        mBody.setLinearVelocity(direction);


        if(Math.abs(direction.x) > 0 || Math.abs(direction.z) > 0) {
            mState = PLAYERSTATE.running;
        } else {
            mState = PLAYERSTATE.idle;
        }
    }

    public void rotate(Vector3 axis, float angle) {
        Quaternion quat = new Quaternion().setFromAxis(axis, angle);
        mBody.setCenterOfMassTransform(mBody.getWorldTransform().rotate(quat));
    }

    public Vector3 getPosition() {
        return mBody.getCenterOfMassPosition();
    }

    public Vector3 getVelocity() {
        return mBody.getLinearVelocity().cpy();
    }

    public float getMoveSpeed() {
        return mMovespeed;
    }
}
