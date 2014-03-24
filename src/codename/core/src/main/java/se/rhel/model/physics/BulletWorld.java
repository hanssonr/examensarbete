package se.rhel.model.physics;

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
    public Array<ModelInstance> levelInstance = new Array<>();
    public ModelInstance fpsModel;
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
        mBodyInfos.add(levelInfo);
        ModelInstance level = new ModelInstance(Resources.INSTANCE.levelModelVisual);
        levelInstance.add(level);
        btDefaultMotionState levelMotionState = new btDefaultMotionState();
        levelMotionState.setWorldTransform(level.transform);
        mMotionStates.add(levelMotionState);
        btRigidBody levelBody = new btRigidBody(levelInfo);
        levelBody.setMotionState(levelMotionState);
        mBodies.add(levelBody);
        mCollisionWorld.addRigidBody(levelBody);

        //ddSpheres();
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
        for(float x = -5f; x <= 5f; x += 2f) {
            for(float y = 10f; y <= 16f; y += 2f) {
                for(float z = -5f; z <= 5f; z+= 2f) {

                    ModelInstance sphere = new ModelInstance(sphereModel);
                    sphere.materials.get(0).set(ColorAttribute.createDiffuse(x+0.5f* MathUtils.random(), y+0.5f*MathUtils.random(), z+0.5f*MathUtils.random(), 1));
                    instances.add(sphere);
                    sphere.transform.trn(x+0.1f* MathUtils.random(), y+0.1f*MathUtils.random(), z+0.1f*MathUtils.random());
                    btDefaultMotionState sphereMotionState = new btDefaultMotionState();
                    sphereMotionState.setWorldTransform(sphere.transform);
                    mMotionStates.add(sphereMotionState);
                    btRigidBody sphereBody = new btRigidBody(sphereInfo);
                    sphereBody.userData = 99;
                    sphereBody.setMotionState(sphereMotionState);
                    mBodies.add(sphereBody);
                    mCollisionWorld.addRigidBody(sphereBody);

                }
            }
        }
    }

    public void addToWorld(btCollisionShape shape, btRigidBodyConstructionInfo info, btDefaultMotionState motionState, ModelInstance instance, btRigidBody body) {
        instances.add(instance);
        this.addToWorld(shape, info, motionState, body);
    }

    public void addToWorld(btCollisionShape shape, btRigidBodyConstructionInfo info, btDefaultMotionState motionState, btRigidBody body) {
        mShapes.add(shape);
        mBodyInfos.add(info);

        mMotionStates.add(motionState);

        mBodies.add(body);
        mCollisionWorld.addRigidBody(body);
    }

    public void removeInstance(ModelInstance instance) {
        instances.removeValue(instance, true);
    }

    @Override
    public void update(float delta) {

        PERFORMANCE_COUNTER.tick();
        PERFORMANCE_COUNTER.start();
                ((btDynamicsWorld) mCollisionWorld).stepSimulation(delta, 5);
        PERFORMANCE_COUNTER.stop();

        //int c = mMotionStates.size;
//        int c = instances.size;
//        for (int i = 0; i < c; i++) {
//          mMotionStates.get(i).getWorldTransform(instances.get(i).transform);
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
