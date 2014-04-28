package se.rhel.model.physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PerformanceCounter;
import se.rhel.model.BaseModel;
import se.rhel.res.Resources;

/**
 * Group: Logic
 *
 * Created by Emil on 2014-02-20.
 * assigned to libgdx-gradle-template in se.rhel.model
 */
public class BulletWorld implements BaseModel {

    private static PerformanceCounter PERFORMANCE_COUNTER = new PerformanceCounter("BulletWorld");
    public static String PERFORMANCE;

    private btCollisionConfiguration mCollisionConfig;
    private btCollisionDispatcher mDispatcher;
    private btBroadphaseInterface mBroadphase;
    private btConstraintSolver mSolver;
    private btDynamicsWorld mCollisionWorld;

    private Array<btCollisionShape> mShapes = new Array<>();
    private Array<btRigidBodyConstructionInfo> mBodyInfos = new Array<>();
    private Array<btRigidBody> mBodies = new Array<>();
    private Array<btDefaultMotionState> mMotionStates = new Array<>();

    private Vector3 mGravity = new Vector3(0, -9.87f, 0);
    private final float FIXED_TIMESTEP = 1f/60f;

    public BulletWorld() {
        create();
    }

    @Override
    public void create() {
        // Create the bullet world
        mCollisionConfig = new btDefaultCollisionConfiguration();
        mDispatcher = new btCollisionDispatcher(mCollisionConfig);
        mBroadphase = new btDbvtBroadphase();
        mSolver = new btSequentialImpulseConstraintSolver();
        mCollisionWorld = new btDiscreteDynamicsWorld(mDispatcher, mBroadphase, mSolver, mCollisionConfig);
        mCollisionWorld.setGravity(mGravity);

        // Level
        btBvhTriangleMeshShape levelShape = new btBvhTriangleMeshShape(Resources.INSTANCE.levelModelPhysics.meshParts);
        mShapes.add(levelShape);
        btRigidBodyConstructionInfo levelInfo = new btRigidBodyConstructionInfo(0f, null, levelShape, Vector3.Zero);
        levelInfo.setRestitution(5f);
        levelInfo.setFriction(5f);
        mBodyInfos.add(levelInfo);
        btDefaultMotionState levelMotionState = new btDefaultMotionState();
        mMotionStates.add(levelMotionState);
        btRigidBody levelBody = new btRigidBody(levelInfo);
        levelBody.setMotionState(levelMotionState);
        mBodies.add(levelBody);
        mCollisionWorld.addRigidBody(levelBody);
    }

    public void addToWorld(btCollisionShape shape, btRigidBodyConstructionInfo info, btDefaultMotionState motionState, btRigidBody body) {
        mShapes.add(shape);
        mBodyInfos.add(info);
        mMotionStates.add(motionState);
        mBodies.add(body);
        mCollisionWorld.addRigidBody(body);
    }

    @Override
    public void update(float delta) {
        PERFORMANCE_COUNTER.tick();
        PERFORMANCE_COUNTER.start();
        mCollisionWorld.stepSimulation(delta, 8, FIXED_TIMESTEP);
        PERFORMANCE_COUNTER.stop();
        PERFORMANCE = "Bullet: " + (int)(PERFORMANCE_COUNTER.load.value*100f) + " %";
    }

    @Override
    public void dispose() {
        mCollisionWorld.dispose();
        mSolver.dispose();
        mBroadphase.dispose();
        mDispatcher.dispose();
        mCollisionConfig.dispose();

        for(btRigidBody body : mBodies)
            body.dispose();
        mBodies.clear();

        for(btDefaultMotionState motionState : mMotionStates)
            motionState.dispose();
        mMotionStates.clear();

        for(btCollisionShape shape : mShapes)
            shape.dispose();
        mShapes.clear();

        for(btRigidBodyConstructionInfo info : mBodyInfos)
            info.dispose();
        mBodyInfos.clear();
    }

    public btCollisionWorld getCollisionWorld() {
        return mCollisionWorld;
    }
}
