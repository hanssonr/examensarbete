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
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btQuaternion;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import se.rhel.model.Entity.DynamicEntity;
import se.rhel.res.Resources;
import se.rhel.view.DecalRenderer;

public class Player extends DynamicEntity {

    private BulletWorld mWorld;
    private btRigidBody mBody;

    // Testing
    private ClosestRayResultCallback rayTestCB;
    public boolean hasShot = false;
    public Vector3 from = new Vector3();
    public Vector3 to = new Vector3();
    public Vector3 fromGround = new Vector3();
    public Vector3 toGround = new Vector3();

    public boolean mOnGround = false;
    private static Vector2 mPlayersize = new Vector2(0.3f, 1f);

    public Player(Vector3 position, BulletWorld world) {
        super(7f);
        mWorld = world;

        getTransformation().setTranslation(position);
        createPyshicsBody();
    }

    private void createPyshicsBody() {
        btCollisionShape playerShape = new btCapsuleShape(mPlayersize.x, mPlayersize.y);
        btRigidBodyConstructionInfo playerInfo = new btRigidBodyConstructionInfo(5f, null, playerShape, Vector3.Zero);
        btDefaultMotionState playerMotionState = new btDefaultMotionState(getTransformation());

        mBody = new btRigidBody(playerInfo);
        mBody.setMotionState(playerMotionState);
        mBody.setGravity(new Vector3(0, 0, 0));
        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);

        mWorld.addToWorld(playerShape,
                playerInfo,
                playerMotionState,
                new ModelInstance(Resources.INSTANCE.firstPersonWeaponModel, getPosition()),
                mBody);
    }

    public void update(float delta) {
        mTransformation.set(mBody.getCenterOfMassTransform().cpy());

        checkOnGround();

    }

    public void shoot(Ray ray) {
        hasShot = true;
        from.set(ray.origin);
        to.set(ray.direction).scl(50f).add(from);

        rayTestCB.setCollisionObject(null);
        rayTestCB.setClosestHitFraction(1f);
        rayTestCB.getRayFromWorld().setValue(from.x, from.y, from.z);
        rayTestCB.getRayToWorld().setValue(to.x, to.y, to.z);

        mWorld.getCollisionWorld().rayTest(from, to, rayTestCB);

        if (rayTestCB.hasHit()) {
            to.set(new Vector3(rayTestCB.getHitPointWorld().getX(), rayTestCB.getHitPointWorld().getY(), rayTestCB.getHitPointWorld().getZ()));
            final btCollisionObject obj = rayTestCB.getCollisionObject();
            if (!obj.isStaticOrKinematicObject()) {
                final btRigidBody body = (btRigidBody)(obj);
                body.activate();
                body.applyCentralImpulse(Vector3.tmp2.set(ray.direction).scl(20f));
            }

            if(obj.isStaticOrKinematicObject()) {
                btVector3 v = rayTestCB.getHitPointWorld();
                btVector3 t = rayTestCB.getHitNormalWorld();
                DecalRenderer.addBullethole(new Vector3(v.getX(), v.getY(), v.getZ()), new Vector3(t.getX(), t.getY(), t.getZ()).nor());
                v.dispose();
                t.dispose();
            }
        }
    }

    private void checkOnGround() {
        mOnGround = false;
        fromGround.set(getPosition());
        toGround.set(new Vector3(fromGround.x, fromGround.y - 1f, fromGround.z));
        ClosestRayResultCallback cb = new ClosestRayResultCallback(fromGround, toGround);
        cb.setCollisionObject(null);

        mWorld.getCollisionWorld().rayTest(fromGround, toGround, cb);

        if(cb.hasHit()) {
            final btCollisionObject obj = cb.getCollisionObject();
            if (obj.isStaticObject()) {
                mOnGround = true;
            }
        }
    }

    public void move(Vector3 direction) {
        mBody.activate(true);
        direction.x *= mMovespeed;
        direction.z *= mMovespeed;
        mBody.setLinearVelocity(direction);
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

    public boolean isGrounded() {
        return mOnGround;
    }

    public float getMoveSpeed() {
        return mMovespeed;
    }

}





















