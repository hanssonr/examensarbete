package se.rhel.model.BulletTest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.PerformanceCounter;

/**
 * Created by Emil on 2014-02-20.
 * Test of the bullet physics
 */
public class BulletWorld extends BaseWorld<BulletEntity> {

    public DebugDrawer debugDrawer = null;
    public boolean renderMeshes = true;

    public final btCollisionConfiguration collisionConfiguration;
    public final btCollisionDispatcher dispatcher;
    public final btBroadphaseInterface broadphase;
    public final btConstraintSolver solver;
    public final btCollisionWorld collisionWorld;
    public PerformanceCounter performanceCounter;
    public final Vector3 gravity;

    public int maxSubSteps = 5;
    public float fixedTimeStep = 1f / 60f;

    public BulletWorld(final btCollisionConfiguration collisionConfiguration, final btCollisionDispatcher dispatcher,
                       final btBroadphaseInterface broadphase, final btConstraintSolver solver, final btCollisionWorld world,
                       final Vector3 gravity) {
        this.collisionConfiguration = collisionConfiguration;
        this.dispatcher = dispatcher;
        this.broadphase = broadphase;
        this.solver = solver;
        this.collisionWorld = world;
        if (world instanceof btDynamicsWorld)
            ((btDynamicsWorld)this.collisionWorld).setGravity(gravity);
        this.gravity = gravity;
    }

    public BulletWorld(final btCollisionConfiguration collisionConfiguration, final btCollisionDispatcher dispatcher,
                       final btBroadphaseInterface broadphase, final btConstraintSolver solver, final btCollisionWorld world) {
        this(collisionConfiguration, dispatcher, broadphase, solver, world, new Vector3(0, -10, 0));
    }

    public BulletWorld(final Vector3 gravity) {
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadphase = new btDbvtBroadphase();
        solver = new btSequentialImpulseConstraintSolver();
        collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        ((btDynamicsWorld)collisionWorld).setGravity(gravity);
        this.gravity = gravity;
    }

    public BulletWorld() {
        this(new Vector3(0, -10, 0));
    }

    @Override
    public void add(final BulletEntity entity) {
        super.add(entity);
        if (entity.body != null) {
            if (entity.body instanceof btRigidBody)
                ((btDiscreteDynamicsWorld)collisionWorld).addRigidBody((btRigidBody)entity.body);
            else
                collisionWorld.addCollisionObject(entity.body);
            // Store the index of the entity in the collision object.
            entity.body.setUserValue(entities.size-1);
        }
    }

    @Override
    public void update () {
        if (performanceCounter != null) {
            performanceCounter.tick();
            performanceCounter.start();
        }
        if (collisionWorld instanceof btDynamicsWorld)
            ((btDynamicsWorld)collisionWorld).stepSimulation(Gdx.graphics.getDeltaTime(), maxSubSteps, fixedTimeStep);
        if (performanceCounter != null)
            performanceCounter.stop();
    }

    @Override
    public void render (ModelBatch batch, Environment lights, Iterable<BulletEntity> entities) {
        if (debugDrawer != null && debugDrawer.getDebugMode() > 0) {
            debugDrawer.begin();
            collisionWorld.debugDrawWorld();
            debugDrawer.end();
        }
        if (renderMeshes)
            super.render(batch, lights, entities);
    }

    @Override
    public void dispose () {
        for (int i = 0; i < entities.size; i++) {
            btCollisionObject body = entities.get(i).body;
            if (body != null) {
                if (body instanceof btRigidBody)
                    ((btDynamicsWorld)collisionWorld).removeRigidBody((btRigidBody)body);
                else
                    collisionWorld.removeCollisionObject(body);
            }
        }

        super.dispose();

        collisionWorld.dispose();
        if (solver != null)
            solver.dispose();
        if (broadphase != null)
            broadphase.dispose();
        if (dispatcher != null)
            dispatcher.dispose();
        if (collisionConfiguration != null)
            collisionConfiguration.dispose();
    }

    public void setDebugMode(final int mode, final Matrix4 projMatrix) {
        if (mode == btIDebugDraw.DebugDrawModes.DBG_NoDebug && debugDrawer == null)
            return;
        if (debugDrawer == null)
            collisionWorld.setDebugDrawer(debugDrawer = new DebugDrawer());
        debugDrawer.lineRenderer.setProjectionMatrix(projMatrix);
        debugDrawer.setDebugMode(mode);
    }

    public int getDebugMode() {
        return (debugDrawer == null) ? 0 : debugDrawer.getDebugMode();
    }
}
