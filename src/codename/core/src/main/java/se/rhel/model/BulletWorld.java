package se.rhel.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btTransformFloatData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PerformanceCounter;
import se.rhel.res.Resources;

/**
 * Created by Emil on 2014-02-20.
 * assigned to libgdx-gradle-template in se.rhel.model
 */
public class BulletWorld implements BaseModel {

    private final float FIXED_TIMESTEP = 1f/60f;

    private static PerformanceCounter PERFORMANCE_COUNTER = new PerformanceCounter("BulletWorld");
    public static String PERFORMANCE;

    private ModelBuilder mModelBuilder = new ModelBuilder();

    private btCollisionConfiguration mCollisionConfig;
    private btCollisionDispatcher mDispatcher;
    private btBroadphaseInterface mBroadphase;
    private btConstraintSolver mSolver;
    private btDynamicsWorld mCollisionWorld;

    private Array<Model> mModels = new Array<>();
    public Array<ModelInstance> instances = new Array<ModelInstance>();
    private Array<btCollisionShape> mShapes = new Array<>();
    private Array<btRigidBodyConstructionInfo> mBodyInfos = new Array<>();
    private Array<btRigidBody> mBodies = new Array<>();
    private Array<btDefaultMotionState> mMotionStates = new Array<>();

    private Vector3 mGravity = new Vector3(0, -9.18f, 0);
    private Vector3 mTempVector = new Vector3();

    public BulletWorld() {
        create();
    }

    @Override
    public void create() {

        // Create some basic models
//        final Model groundModel = mModelBuilder.createRect(20f, 0f, -20f, -20f, 0f, -20f, -20f, 0f, 20f, 20f, 0f, 20f, 0, 1, 0,
//                new Material(ColorAttribute.createDiffuse(Color.BLUE), ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(16f)),
//                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
//        mModels.add(groundModel);

        // Bullet is init from Resources

        // Create the bullet world
        mCollisionConfig = new btDefaultCollisionConfiguration();
        mDispatcher = new btCollisionDispatcher(mCollisionConfig);
        mBroadphase = new btDbvtBroadphase();
        mSolver = new btSequentialImpulseConstraintSolver();
        mCollisionWorld = new btDiscreteDynamicsWorld(mDispatcher, mBroadphase, mSolver, mCollisionConfig);
        mCollisionWorld.setGravity(mGravity);

        // Create the shapes and body construction infos
        /*
        btCollisionShape groundShape = new btBoxShape(mTempVector.set(20, 0, 20));
        mShapes.add(groundShape);
        btRigidBodyConstructionInfo groundInfo = new btRigidBodyConstructionInfo(0f, null, groundShape, Vector3.Zero);
        mBodyInfos.add(groundInfo);

        // Create the ground
        ModelInstance ground = new ModelInstance(groundModel);
        instances.add(ground);
        btDefaultMotionState groundMotionState = new btDefaultMotionState();
        groundMotionState.setWorldTransform(ground.transform);
        mMotionStates.add(groundMotionState);

        btRigidBody groundBody = new btRigidBody(groundInfo);
        groundBody.setMotionState(groundMotionState);
        mBodies.add(groundBody);

        mCollisionWorld.addRigidBody(groundBody);
        */

        // Level
        btBvhTriangleMeshShape levelShape = new btBvhTriangleMeshShape(Resources.INSTANCE.levelModel.meshParts);
        mShapes.add(levelShape);
        btRigidBodyConstructionInfo levelInfo = new btRigidBodyConstructionInfo(0f, null, levelShape, Vector3.Zero);
        mBodyInfos.add(levelInfo);
        ModelInstance level = new ModelInstance(Resources.INSTANCE.levelModel);
        instances.add(level);
        btDefaultMotionState levelMotionState = new btDefaultMotionState();
        levelMotionState.setWorldTransform(level.transform);
        mMotionStates.add(levelMotionState);
        btRigidBody levelBody = new btRigidBody(levelInfo);
        levelBody.setMotionState(levelMotionState);
        mBodies.add(levelBody);
        mCollisionWorld.addRigidBody(levelBody);

        // btCollisionShape pShape = new btBoxShape(new Vector3(1f, 1f, 1f));
        btBvhTriangleMeshShape pShape = new btBvhTriangleMeshShape(Resources.INSTANCE.playerModel.meshParts);
        mShapes.add(pShape);
        btRigidBodyConstructionInfo pInfo = new btRigidBodyConstructionInfo(0f, null, pShape, Vector3.Zero);
        mBodyInfos.add(pInfo);
        ModelInstance p = new ModelInstance(Resources.INSTANCE.playerModel);
        instances.add(p);
        btDefaultMotionState pMotionState = new btDefaultMotionState();

        p.transform.trn(0f, 12f, 1f);
        pMotionState.setWorldTransform(p.transform);
        mMotionStates.add(pMotionState);
        btRigidBody pBody = new btRigidBody(pInfo);
        pBody.setMotionState(pMotionState);
        mBodies.add(pBody);
        mCollisionWorld.addRigidBody(pBody);

        addSpheres();
    }

    public void addSpheres() {

        final Model sphereModel = mModelBuilder.createSphere(1f, 1f, 1f, 10, 10,
                new Material(ColorAttribute.createDiffuse(Color.RED), ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        mModels.add(sphereModel);

        btCollisionShape sphereShape = new btSphereShape(0.5f);
        mShapes.add(sphereShape);
        sphereShape.calculateLocalInertia(1f, mTempVector);
        btRigidBodyConstructionInfo sphereInfo = new btRigidBodyConstructionInfo(1f, null, sphereShape, mTempVector);
        mBodyInfos.add(sphereInfo);

        // Create the spheres
        for(float x = -10f; x <= 10f; x += 5f) {
            for(float y = 5f; y <= 15f; y += 2f) {
                for(float z = 0f; z <= 0f; z+= 2f) {

                    ModelInstance sphere = new ModelInstance(sphereModel);
                    sphere.materials.get(0).set(ColorAttribute.createDiffuse(x+0.5f* MathUtils.random(), y+0.5f*MathUtils.random(), z+0.5f*MathUtils.random(), 1));
                    instances.add(sphere);
                    sphere.transform.trn(x+0.1f* MathUtils.random(), y+0.1f*MathUtils.random(), z+0.1f*MathUtils.random());
                    btDefaultMotionState sphereMotionState = new btDefaultMotionState();
                    sphereMotionState.setWorldTransform(sphere.transform);
                    mMotionStates.add(sphereMotionState);
                    btRigidBody sphereBody = new btRigidBody(sphereInfo);
                    sphereBody.setMotionState(sphereMotionState);
                    mBodies.add(sphereBody);
                    mCollisionWorld.addRigidBody(sphereBody);

                }
            }
        }
    }

    public void addToWorld(btCollisionShape shape, btRigidBodyConstructionInfo info, btDefaultMotionState motionState, ModelInstance instance, btRigidBody body) {
        mShapes.add(shape);
        mBodyInfos.add(info);

        instances.add(instance);
        mMotionStates.add(motionState);

        mBodies.add(body);
        mCollisionWorld.addRigidBody(body);
    }

    @Override
    public void update(float delta) {

        PERFORMANCE_COUNTER.tick();
        PERFORMANCE_COUNTER.start();
                ((btDynamicsWorld) mCollisionWorld).stepSimulation(FIXED_TIMESTEP, 5);
        PERFORMANCE_COUNTER.stop();

        int c = mMotionStates.size;
        for (int i = 0; i < c; i++) {
            mMotionStates.get(i).getWorldTransform(instances.get(i).transform);
        }

//        for(btRigidBody b : mBodies) {
//            //b.applyCentralForce(new Vector3(5, 0, 0));
//            // b.getMotionState().
//            // b.setLinearVelocity(new Vector3(10, 0 ,0));
//            // b.applyGravity();
//        }

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
