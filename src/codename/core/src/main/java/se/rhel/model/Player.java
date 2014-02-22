package se.rhel.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btQuaternion;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;
import se.rhel.model.Entity.DynamicEntity;
import se.rhel.res.Resources;

public class Player extends DynamicEntity {

    private BulletWorld mWorld;
    private btRigidBody mBody;

    public boolean mOnGround = false;
    private static Vector2 mPlayersize = new Vector2(0.3f, 1f);

    public Player(Vector3 position, BulletWorld world) {
        super(7f);
        mWorld = world;

        getTransformation().setTranslation(position);

        btCollisionShape playerShape = new btCapsuleShape(mPlayersize.x, mPlayersize.y);
        btRigidBodyConstructionInfo playerInfo = new btRigidBodyConstructionInfo(5f, null, playerShape, Vector3.Zero);
        btDefaultMotionState playerMotionState = new btDefaultMotionState(getTransformation());


        mBody = new btRigidBody(playerInfo);
        mBody.setMotionState(playerMotionState);
        mBody.setGravity(new Vector3(0,0,0));

        mWorld.addToWorld(playerShape,
                playerInfo,
                playerMotionState,
                new ModelInstance(Bodybuilder.INSTANCE.createCapsule(mPlayersize.x, mPlayersize.y), position),
                mBody);
    }

    public void update(float delta) {
        mTransformation.set(mBody.getCenterOfMassTransform().cpy());

        checkOnGround();
    }

    private void checkOnGround() {
        mOnGround = false;
        Vector3 from = mBody.getCenterOfMassPosition().cpy();
        Vector3 to = new Vector3(from.x, from.y - 1f, from.z);

        ClosestRayResultCallback cb = new ClosestRayResultCallback(from, to);
        cb.setCollisionObject(null);

        mWorld.getCollisionWorld().rayTest(from, to, cb);

        if(cb.hasHit()) {
            final btCollisionObject obj = cb.getCollisionObject();
            if (obj.isStaticObject()) {
                mOnGround = true;
            }
        }
    }

    @Override
    public void move(Vector3 direction) {
        mBody.activate(true);
        mBody.setLinearVelocity(direction);
    }

    @Override
    public void rotate(Vector3 axis, float angle) {
       Quaternion quat = new Quaternion().setFromAxis(axis, angle);
       mBody.setCenterOfMassTransform(mBody.getWorldTransform().rotate(quat));
    }

    public Vector3 getVelocity() {
        return mBody.getLinearVelocity().cpy();
    }

    public boolean isGrounded() {
        return mOnGround;
    }

    public float getMoveSpeed() {
        return mMovespeed;
    }
}
