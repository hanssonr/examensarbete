package se.rhel.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import se.rhel.model.Entity.DynamicEntity;
import se.rhel.res.Resources;
import se.rhel.view.BulletHoleRenderer;

public class Player extends DynamicEntity {

    public enum PLAYERSTATE {
            idle, running
        }

        private FPSCamera mCamera;

        private PLAYERSTATE mState;

        private float bobPower = 0.7f;
        private float bobTimer = 0f;
        private Vector3 bobVector = new Vector3();

        private BulletWorld mWorld;
        private btRigidBody mBody;

        // Testing
        private ClosestRayResultCallback rayTestCB;
        public boolean hasShot = false;

        public Vector3 from = new Vector3();
        public Vector3 to = new Vector3();

        public Vector3 fromGround = new Vector3();
        public Vector3 toGround = new Vector3();

        private float deltaShoot;

        //weapon
        ModelInstance weapon;
        Matrix4 weaponWorld = new Matrix4().idt();
        Vector3 weaponOffset = new Vector3();

        public boolean mOnGround = false;
        private static Vector2 mPlayersize = new Vector2(0.6f, 1.5f);

        public Player(Vector3 position, BulletWorld world) {
            super(7f);
            mWorld = world;

            getTransformation().setTranslation(position);
            createPyshicsBody();
            weapon = new ModelInstance(Resources.INSTANCE.fpsWeaponModel);
            mWorld.fpsModel = weapon;

            mState = PLAYERSTATE.idle;
        }

    private void createPyshicsBody() {
        btCollisionShape playerShape = new btCapsuleShape(mPlayersize.x, mPlayersize.y);
        btRigidBodyConstructionInfo playerInfo = new btRigidBodyConstructionInfo(5f, null, playerShape, Vector3.Zero);
        btDefaultMotionState playerMotionState = new btDefaultMotionState(getTransformation());

        mBody = new btRigidBody(playerInfo);
        mBody.setMotionState(playerMotionState);
        mBody.setGravity(Vector3.Zero);

        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);

        mWorld.addToWorld(playerShape,
                playerInfo,
                playerMotionState,
                mBody);
    }

    public void update(float delta) {
        mBody.setGravity(Vector3.Zero);
        mTransformation.set(mBody.getCenterOfMassTransform());
        updateCamera(delta);
        updateWeapon();
        checkOnGround();

        // Update shooting for uneccesary drawing / spam shooting
        if(hasShot) {
            deltaShoot += delta;
            if(deltaShoot > 0.3f) {
                hasShot = false;
                deltaShoot = 0f;
            }
        }
    }

    private void updateWeapon() {
        weaponWorld.set(mCamera.view.cpy().inv());
        weaponWorld.getTranslation(weaponOffset);
        weaponOffset.sub(mCamera.up.cpy().scl(0.7f));
        weaponOffset.add(mCamera.direction);
        weaponOffset.add(mCamera.getRight());

        if(mState == PLAYERSTATE.running) {
            weaponOffset.add(bobVector.cpy().scl(bobPower));
        }

        weaponWorld.setTranslation(weaponOffset);
        weapon.transform.set(weaponWorld);
    }

    public void shoot() {

        if(hasShot)
            return;

        hasShot = true;

        // We want a ray from middle of screen as basis of hit detection
        Ray ray = mCamera.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        ray = ray.cpy();

        // For debugging purposes
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
                BulletHoleRenderer.addBullethole(new Vector3(v.getX(), v.getY(), v.getZ()), new Vector3(t.getX(), t.getY(), t.getZ()).nor());
                v.dispose();
                t.dispose();
            }
        }
    }

    private void updateCamera(float delta) {
        if(mCamera != null) {
            bobTimer+=delta;

            mTransformation.getTranslation(mCamera.position);
            mCamera.position.add(mCamera.getOffset());

            //bobbing
            if(mState == PLAYERSTATE.running) {
                Vector3 dir = mCamera.getRight().cpy();
                dir.y = 1f;
                float x = (float)Math.sin(bobTimer*10)*0.05f;
                float y = (float)Math.cos(bobTimer * 20)*0.03f;
                float z = (float)Math.sin(bobTimer*10)*0.05f;
                bobVector.set(x, y, z);
                mCamera.position.add(bobVector.scl(dir)).cpy().scl(bobPower);
            }

            mCamera.update();
        }
    }

    public void attachCamera(FPSCamera camera) {
        this.mCamera = camera;
    }

    private void checkOnGround() {
        mOnGround = false;
        fromGround.set(getPosition());
        toGround.set(new Vector3(fromGround.x, fromGround.y - mPlayersize.y, fromGround.z));

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

    public boolean isGrounded() {
        return mOnGround;
    }

    public float getMoveSpeed() {
        return mMovespeed;
    }

}





















