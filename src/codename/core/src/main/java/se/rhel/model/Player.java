package se.rhel.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
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

    private Vector3 mDirection = new Vector3();

    public Player(Vector3 position, BulletWorld world) {
        super(position,
                new ModelInstance(Bodybuilder.INSTANCE.createBox(1f, 1f, 1f,
                        new Material(ColorAttribute.createDiffuse(Color.ORANGE))), position), 10f);

        mWorld = world;

        getInstance().transform.setTranslation(position);
        btCollisionShape playerShape = new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f));
        btRigidBodyConstructionInfo playerInfo = new btRigidBodyConstructionInfo(1f, null, playerShape, Vector3.Zero);
        btDefaultMotionState playerMotionState = new btDefaultMotionState();
        playerMotionState.setWorldTransform(getInstance().transform);
        mBody = new btRigidBody(playerInfo);
        mBody.setMotionState(playerMotionState);

        mWorld.addToWorld(playerShape, playerInfo, playerMotionState, getInstance(), mBody);

        rayTestCB = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
    }

    public void update(float delta) {
        // Gdx.app.log("BodyPos", "" + mBody.getCenterOfMassPosition());

        Vector3 desiredVel = mDirection.scl(mMovespeed);
        Vector3 imp = desiredVel.mul(mBody.getInvMass());
        // mBody.applyCentralForce(imp);
        mBody.applyCentralImpulse(imp);

        // mBody.setLinearVelocity(desiredVel);
        // mBody.setLinearVelocity(getPosition().add(direction.scl(mMovespeed)));
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
                DecalRenderer.addDecalAt(new Vector3(v.getX(), v.getY(), v.getZ()), new Vector3(t.getX(), t.getY(), t.getZ()).nor());
                v.dispose();
                t.dispose();
            }
        }

        /*

        btCollisionShape pShape = new btBoxShape(new Vector3(0.1f, 0.1f, 0.1f));
        btRigidBodyConstructionInfo pInfo = new btRigidBodyConstructionInfo(1f, null, pShape, Vector3.Zero);
        ModelInstance p = new ModelInstance(Bodybuilder.INSTANCE.createBox(0.1f, 0.1f, 0.1f));
        btDefaultMotionState pMotionState = new btDefaultMotionState();

        p.transform.trn(from);

        // pMotionState.setWorldTransform(p.transform);
        btRigidBody pBody = new btRigidBody(pInfo);
        // pBody.setMotionState(pMotionState);

        Gdx.app.log("Body pos", ""+pBody.getCenterOfMassPosition());

        mWorld.addToWorld(pShape, pInfo, pMotionState, p, pBody);
        // Vector3 dir = ray.getEndPoint(1f).nor();
        pBody.applyCentralImpulse(to.scl(3f));
        // pBody.setLinearVelocity(to.scl(30f));
        */
    }

    @Override
    public void move(Vector3 direction) {
        // super.move(direction);
        mDirection = direction;
        // mBody.translate(getPosition()); // .transform.setTranslation(getPosition());
    }

    @Override
    public void rotate(Vector3 axis, float angle) {
        super.rotate(axis, angle);
    }

    @Override
    public Vector3 getPosition() {
        return mBody.getCenterOfMassPosition();
    }
}
