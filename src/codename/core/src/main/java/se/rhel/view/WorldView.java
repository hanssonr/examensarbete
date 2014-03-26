package se.rhel.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import se.rhel.Client;
import se.rhel.graphics.FrontFaceDepthShaderProvider;
import se.rhel.model.client.ClientWorldModel;
import se.rhel.model.physics.BulletWorld;
import se.rhel.model.FPSCamera;
import se.rhel.model.WorldModel;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.res.Resources;
import se.rhel.view.input.PlayerInput;
import se.rhel.view.sfx.SoundManager;

import java.math.BigDecimal;

/**
 * Group: Logic
 */
public class WorldView {

    private FPSCamera weaponCam;

    private TextRenderer mFPSRenderer;
    private TextRenderer mBulletLoadRenderer;
    private TextRenderer mPlayerPosRenderer;
    private LaserView mLaserView;

    // Networking stats
    private TextRenderer mLatencyRenderer;

    private SpriteBatch mSpriteBatch;
    private ModelBatch mModelBatch;
    private ShapeRenderer mCrosshairRenderer;
    private BulletHoleRenderer mBulletHoleRenderer;
    private EntitySystemRenderer mEntitySystem;
    private DecalRenderer mDecalRenderer;

    private WorldModel mWorldModel;

    private Environment mEnvironment;

    private DebugDrawer mDebugDrawer;

    private DebugDrawer mAimDebugDrawer;

    private AnimationController mAnimationController;

    private FrameBuffer buffer1 = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    private FrameBuffer depthFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

    private ShaderProgram outLineShader = new ShaderProgram(Gdx.files.internal("shader/outline.vertex.glsl"), Gdx.files.internal("shader/outline.fragment.glsl"));
    private ShaderProgram toonShader = new ShaderProgram(Gdx.files.internal("shader/celshading.vertex.glsl"), Gdx.files.internal("shader/celshading.fragment.glsl"));

    private Mesh fullscreenQuad = createFullScreenQuad();
    private FrontFaceDepthShaderProvider depthShaderProvider = new FrontFaceDepthShaderProvider();
    private ModelBatch depthModelBatch = new ModelBatch(depthShaderProvider);

    private ParticleRenderer particleRenderer;
    private int mPreviousHealth = 100;

    public WorldView(WorldModel worldModel) {
        weaponCam = new FPSCamera(68, 0.1f, 5);
        mWorldModel = worldModel;
        mSpriteBatch = new SpriteBatch();
        mModelBatch = new ModelBatch();

        mCrosshairRenderer = new ShapeRenderer();
        mBulletHoleRenderer = new BulletHoleRenderer(mWorldModel.getCamera());
        mEntitySystem = new EntitySystemRenderer();
        mDecalRenderer = new DecalRenderer(mWorldModel.getCamera());

        mAimDebugDrawer = new DebugDrawer();
        mDebugDrawer = new DebugDrawer();
        mWorldModel.getBulletWorld().getCollisionWorld().setDebugDrawer(mDebugDrawer);
        mDebugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawWireframe);

        mFPSRenderer = TextRenderer.FPS(mWorldModel, mSpriteBatch);
        mBulletLoadRenderer = new TextRenderer("Bullet init", new Vector2(10, Gdx.graphics.getHeight() - 30), mWorldModel, mSpriteBatch);
        mPlayerPosRenderer = new TextRenderer("Player init", new Vector2(10, Gdx.graphics.getHeight() - 60), mWorldModel, mSpriteBatch);
        mLatencyRenderer = new TextRenderer("Latency init", new Vector2(Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight() - 10), mWorldModel, mSpriteBatch);

        mEnvironment = new Environment();
        mEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.0f));
        mEnvironment.add(
                new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f),
                new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f)
        );

        // mAnimationController = new AnimationController(Resources.INSTANCE.playerModelInstanceAnimated);
        // mAnimationController.setAnimation("walk", -1);
        // mServerWorldModel.getBulletWorld().levelInstance.add(Resources.INSTANCE.playerModelInstanceAnimated);
        // Resources.INSTANCE.modelInstanceArray.add

        /*
        mEnvironment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        mEnvironment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f));
        */
        mLaserView = new LaserView(worldModel);
        SoundManager.INSTANCE.playMusic(true, .2f);
        mDecalRenderer = new DecalRenderer(mWorldModel.getCamera());

        particleRenderer = new ParticleRenderer(mWorldModel, mSpriteBatch, mWorldModel.getCamera());
    }

    public void render(float delta) {

        // mAnimationController.update(delta);
        weaponCam.position.set(mWorldModel.getCamera().position);

        if(PlayerInput.DRAW_MESH) {
            // Cel-shading
            buffer1.begin();
                Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                Gdx.gl.glClearColor(0, 1, 1, 1);
                Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

                mModelBatch.begin(mWorldModel.getCamera());
                    mModelBatch.render(mWorldModel.getBulletWorld().levelInstance, mEnvironment);
                    mModelBatch.render(Resources.INSTANCE.modelInstanceArray);
                    mModelBatch.render(mWorldModel.getBulletWorld().instances, mEnvironment);
                mModelBatch.end();

                mBulletHoleRenderer.draw(delta);

            buffer1.end();

            buffer1.getColorBufferTexture().bind();
            toonShader.begin();
                fullscreenQuad.render(toonShader, GL20.GL_TRIANGLE_STRIP, 0, 4);
            toonShader.end();

        } else {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClearColor(0, 1, 1, 1);
            Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            mModelBatch.begin(mWorldModel.getCamera());
            mModelBatch.render(Resources.INSTANCE.modelInstanceArray);
            mModelBatch.render(mWorldModel.getBulletWorld().instances, mEnvironment);
            // mModelBatch.render(mServerWorldModel.getBulletWorld().fpsModel, mEnvironment);
            mModelBatch.render(mWorldModel.getBulletWorld().levelInstance, mEnvironment);
            mModelBatch.end();
        }

        if(PlayerInput.DRAW_DEBUG_INFO) {
            mFPSRenderer.draw(delta);
            mBulletLoadRenderer.setText(BulletWorld.PERFORMANCE + "\n test");
            mBulletLoadRenderer.draw(delta);
            float x = round(mWorldModel.getPlayer().getPosition().x, 3);
            float y = round(mWorldModel.getPlayer().getPosition().y, 3);
            float z = round(mWorldModel.getPlayer().getPosition().z, 3);
            mPlayerPosRenderer.setText("X: " + x + ", Y: " + y + ", Z: " + z);
            mPlayerPosRenderer.draw(delta);

            if(Client.getLatency() != -1L) {
                mLatencyRenderer.setText(Client.getLatency() + " ms");
                mLatencyRenderer.draw(delta);
            }

        }

        if(PlayerInput.DRAW_DEBUG) {
            mDebugDrawer.lineRenderer.setProjectionMatrix(mWorldModel.getCamera().combined);
            mDebugDrawer.begin();
            mWorldModel.getBulletWorld().getCollisionWorld().debugDrawWorld();
            mDebugDrawer.end();
        }

        // Ray
        if(PlayerInput.DRAW_SHOOT_DEBUG) {
            if(mWorldModel.getPlayer().mHasShot) {

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

        if(mWorldModel.getPlayer().mHasShot) {
            mWorldModel.getPlayer().mHasShot = false;
            mLaserView.add(mWorldModel.getPlayer().getVisualRepresentationShoot());
            SoundManager.INSTANCE.playSound(SoundManager.SoundType.LASER);
        }

        // Check if any external player has shot
        if(mWorldModel instanceof  ClientWorldModel) {
            for(int i = 0; i < ((ClientWorldModel)mWorldModel).getExternalPlayers().size; i++) {
                if(((ClientWorldModel)mWorldModel).getExternalPlayers().get(i).hasShot()) {
                    ((ClientWorldModel)mWorldModel).getExternalPlayers().get(i).setShot(false);

                    mLaserView.add(((ClientWorldModel)mWorldModel).getExternalPlayers().get(i).getVisualShootRepresentation());
                    SoundManager.INSTANCE.playSound(SoundManager.SoundType.LASER);
                }
            }
        }


        mLaserView.render(delta);

        // mLaserRenderer.draw(delta);

        // "Crosshair"
        mCrosshairRenderer.begin(ShapeRenderer.ShapeType.Line);
        mCrosshairRenderer.setColor(Color.RED);
        mCrosshairRenderer.circle(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 5f);
        mCrosshairRenderer.end();

        mModelBatch.begin(mWorldModel.getCamera());
        Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
        mModelBatch.render(mWorldModel.getBulletWorld().fpsModel, mEnvironment);
        mModelBatch.end();

        // External stuff
        if(mWorldModel instanceof ClientWorldModel) {
            for(int i = 0; i < ((ClientWorldModel)mWorldModel).getExternalPlayers().size; i++) {
                ExternalPlayer ep = ((ClientWorldModel)mWorldModel).getExternalPlayers().get(i);

                mDecalRenderer.draw(delta, ep.getPosition());

                if(ep.getHealth() < mPreviousHealth) {
                    particleRenderer.addEffect(ep.getPosition());
                    mPreviousHealth = ep.getHealth();
                }
            }
            particleRenderer.draw(delta);
        }

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

    public Mesh createFullScreenQuad() {
        float[] verts = new float[20];
        int i = 0;

        verts[i++] = 1f; // x2
        verts[i++] = -1; // y2
        verts[i++] = 0;
        verts[i++] = 1f; // u2
        verts[i++] = 0f; // v2

        verts[i++] = -1; // x1
        verts[i++] = -1; // y1
        verts[i++] = 0;
        verts[i++] = 0f; // u1
        verts[i++] = 0f; // v1

        verts[i++] = 1f; // x3
        verts[i++] = 1f; // y2
        verts[i++] = 0;
        verts[i++] = 1f; // u3
        verts[i++] = 1f; // v3

        verts[i++] = -1; // x4
        verts[i++] = 1f; // y4
        verts[i++] = 0;
        verts[i++] = 0f; // u4
        verts[i++] = 1f; // v4


        Mesh mesh = new Mesh( true, 4, 0,  // static mesh with 4 vertices and no indices
                new VertexAttribute( VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE ),
                new VertexAttribute( VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0" ) );

        mesh.setVertices( verts );
        return mesh;
    }

    public void dispose() {
        mModelBatch.dispose();
    }
}
