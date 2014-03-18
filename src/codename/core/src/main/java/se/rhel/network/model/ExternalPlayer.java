package se.rhel.network.model;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import se.rhel.model.BulletWorld;
import se.rhel.model.entity.DynamicEntity;
import se.rhel.res.Resources;

/**
 * Group: Network
 *
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
    private ModelInstance mAnimated;
    private Quaternion mRotation;

    private int mClientId;

    private static Vector2 mPlayersize = new Vector2(0.6f, 1.5f);

    public ExternalPlayer(int clientId, Vector3 position, BulletWorld world) {
        super(7f);
        mWorld = world;
        mClientId = clientId;
        mRotation = new Quaternion();
        mAnimated = new ModelInstance(Resources.INSTANCE.playerModelAnimated);

        getTransformation().setTranslation(position);
        createPyshicsBody();


        mState = PLAYERSTATE.idle;
    }

    private void createPyshicsBody() {
        btCollisionShape playerShape = new btCapsuleShape(mPlayersize.x, mPlayersize.y);
        btRigidBodyConstructionInfo playerInfo = new btRigidBodyConstructionInfo(5f, null, playerShape, Vector3.Zero);
        btDefaultMotionState playerMotionState = new btDefaultMotionState(getTransformation());

        mAnimationController = new AnimationController(mAnimated);
        mAnimationController.setAnimation("walk", -1);

        mBody = new btRigidBody(playerInfo);
        mBody.setMotionState(playerMotionState);
        mBody.setGravity(Vector3.Zero);

        mWorld.addToWorld(playerShape,
                playerInfo,
                playerMotionState,
                mAnimated,
                mBody);
    }

    public void update(float delta) {
        mAnimationController.update(delta);
        // mBody.setGravity(Vector3.Zero);
        // mTransformation.set(mBody.getCenterOfMassTransform());
        // mAnimated.transform.rotate(mRotation);
        mAnimated.transform.set(mBody.getCenterOfMassTransform());
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

    public void setPosition(float x, float y, float z, float rX, float rY, float rZ, float rW) {
        Vector3 toPos = new Vector3(x, y, z);
        Vector3 scale = new Vector3();
        double mag = Math.sqrt(rW*rW + rY * rY);
        mRotation = new Quaternion(0, (float)(rY/mag), 0, (float)(rW/mag));
        mBody.getWorldTransform().getScale(scale);
        Matrix4 m = new Matrix4(toPos, mRotation, scale);
        mBody.setCenterOfMassTransform(m);
    }

    public float getMoveSpeed() {
        return mMovespeed;
    }

    public int getClientId() {
        return mClientId;
    }
}
