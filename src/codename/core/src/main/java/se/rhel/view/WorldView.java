package se.rhel.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import se.rhel.controller.PlayerController;
import se.rhel.model.BulletTest.DebugDrawer;
import se.rhel.model.BulletWorld;
import se.rhel.model.FPSCamera;
import se.rhel.model.WorldModel;
import se.rhel.res.Resources;

import java.math.BigDecimal;

public class WorldView {

    private FPSCamera weaponCam;

    private TextRenderer mFPSRenderer;
    private TextRenderer mBulletLoadRenderer;
    private TextRenderer mPlayerPosRenderer;

    private SpriteBatch mSpriteBatch;
    private ModelBatch mModelBatch;
    private ShapeRenderer mCrosshairRenderer;
    private DecalRenderer mDecalRenderer;

    private WorldModel mWorldModel;

    private Environment mEnvironment;

    private DebugDrawer mDebugDrawer;

    private DebugDrawer mAimDebugDrawer;

    private AnimationController mAnimationController;

    public WorldView(WorldModel worldModel) {
        weaponCam = new FPSCamera(68, 0.1f, 5);
        mWorldModel = worldModel;
        mSpriteBatch = new SpriteBatch();
        mModelBatch = new ModelBatch();

        mCrosshairRenderer = new ShapeRenderer();
        mDecalRenderer = new DecalRenderer(mWorldModel.getCamera());

        mAimDebugDrawer = new DebugDrawer();
        mDebugDrawer = new DebugDrawer();
        mWorldModel.getBulletWorld().getCollisionWorld().setDebugDrawer(mDebugDrawer);
        mDebugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawWireframe);

        mFPSRenderer = TextRenderer.FPS(worldModel, mSpriteBatch);
        mBulletLoadRenderer = new TextRenderer("Bullet init", new Vector2(10, Gdx.graphics.getHeight() - 30), worldModel, mSpriteBatch);
        mPlayerPosRenderer = new TextRenderer("Player init", new Vector2(10, Gdx.graphics.getHeight() - 60), worldModel, mSpriteBatch);

        mEnvironment = new Environment();
        mEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.0f));
        mEnvironment.add(
                new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f),
                new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f)
        );

        mAnimationController = new AnimationController(Resources.INSTANCE.playerModelInstanceAnimated);
        mAnimationController.setAnimation("walk", -1);
        Resources.INSTANCE.modelInstanceArray.add(Resources.INSTANCE.playerModelInstanceAnimated);

        /*
        mEnvironment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        mEnvironment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f));
        */
    }

    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        mAnimationController.update(delta);

        weaponCam.position.set(mWorldModel.getCamera().position);

        if(PlayerController.DRAW_MESH) {
            mModelBatch.begin(mWorldModel.getCamera());
            mModelBatch.render(Resources.INSTANCE.modelInstanceArray);
            mModelBatch.render(mWorldModel.getBulletWorld().instances, mEnvironment);
            mModelBatch.end();
        }

        if(PlayerController.DRAW_DEBUG_INFO) {
            mFPSRenderer.draw(delta);
            mBulletLoadRenderer.setText(BulletWorld.PERFORMANCE + "\n test");
            mBulletLoadRenderer.draw(delta);
            float x = round(mWorldModel.getPlayer().getPosition().x, 3);
            float y = round(mWorldModel.getPlayer().getPosition().y, 3);
            float z = round(mWorldModel.getPlayer().getPosition().z, 3);
            mPlayerPosRenderer.setText("X: " + x + ", Y: " + y + ", Z: " + z);
            mPlayerPosRenderer.draw(delta);
        }

        if(PlayerController.DRAW_DEBUG) {
            mDebugDrawer.lineRenderer.setProjectionMatrix(mWorldModel.getCamera().combined);
            mDebugDrawer.begin();
            mWorldModel.getBulletWorld().getCollisionWorld().debugDrawWorld();
            mDebugDrawer.end();
        }

        // Ray
        if(PlayerController.DRAW_SHOOT_DEBUG) {
            if(mWorldModel.getPlayer().hasShot) {
                btVector3 from = new btVector3(mWorldModel.getPlayer().from.x, mWorldModel.getPlayer().from.y, mWorldModel.getPlayer().from.z);
                btVector3 to = new btVector3(mWorldModel.getPlayer().to.x, mWorldModel.getPlayer().to.y, mWorldModel.getPlayer().to.z);
                btVector3 c = new btVector3(1f, 1f, 1f);
                mAimDebugDrawer.lineRenderer.setProjectionMatrix(mWorldModel.getCamera().combined);
                mAimDebugDrawer.begin();
                mAimDebugDrawer.drawLine(from, to, c);
                mAimDebugDrawer.end();
                from.dispose();
                to.dispose();
                c.dispose();
            }
        }

        // "Crosshair"
        mCrosshairRenderer.begin(ShapeRenderer.ShapeType.Line);
        mCrosshairRenderer.setColor(Color.RED);
        mCrosshairRenderer.circle(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 5f);
        mCrosshairRenderer.end();

        mDecalRenderer.draw(delta);

        mModelBatch.begin(mWorldModel.getCamera());
        Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
        mModelBatch.render(mWorldModel.getBulletWorld().fpsModel, mEnvironment);
        mModelBatch.end();
    }

    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public void dispose() {
        mModelBatch.dispose();
    }
}
