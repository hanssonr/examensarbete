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
import se.rhel.controller.PlayerController;
import se.rhel.graphics.FrontFaceDepthShaderProvider;
import se.rhel.model.BulletTest.DebugDrawer;
import se.rhel.model.BulletWorld;
import se.rhel.model.FPSCamera;
import se.rhel.model.client.ClientWorldModel;
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
    private BulletHoleRenderer mBulletHoleRenderer;
    private LaserRenderer mLaserRenderer;
    private EntitySystemRenderer mEntitySystem;

    private ClientWorldModel mServerWorldModel;

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

    private LaserMeshTestRenderer lmtr;

    public WorldView(ClientWorldModel clientWorldModel) {
        weaponCam = new FPSCamera(68, 0.1f, 5);
        mServerWorldModel = clientWorldModel;
        mSpriteBatch = new SpriteBatch();
        mModelBatch = new ModelBatch();
        lmtr = new LaserMeshTestRenderer(clientWorldModel.getCamera());

        mCrosshairRenderer = new ShapeRenderer();
        mBulletHoleRenderer = new BulletHoleRenderer(mServerWorldModel.getCamera());
        mEntitySystem = new EntitySystemRenderer();

        mAimDebugDrawer = new DebugDrawer();
        mDebugDrawer = new DebugDrawer();
        mServerWorldModel.getBulletWorld().getCollisionWorld().setDebugDrawer(mDebugDrawer);
        mDebugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawWireframe);

        mFPSRenderer = TextRenderer.FPS(clientWorldModel, mSpriteBatch);
        mBulletLoadRenderer = new TextRenderer("Bullet init", new Vector2(10, Gdx.graphics.getHeight() - 30), clientWorldModel, mSpriteBatch);
        mPlayerPosRenderer = new TextRenderer("Player init", new Vector2(10, Gdx.graphics.getHeight() - 60), clientWorldModel, mSpriteBatch);

        mEnvironment = new Environment();
        mEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.0f));
        mEnvironment.add(
                new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f),
                new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f)
        );

        mLaserRenderer = new LaserRenderer(mServerWorldModel.getCamera());

        mAnimationController = new AnimationController(Resources.INSTANCE.playerModelInstanceAnimated);
        mAnimationController.setAnimation("walk", -1);
        mServerWorldModel.getBulletWorld().levelInstance.add(Resources.INSTANCE.playerModelInstanceAnimated);
    }

    public void render(float delta) {

        mAnimationController.update(delta);

        if(PlayerController.DRAW_MESH) {
            // Cel-shading
            buffer1.begin();
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClearColor(0, 1, 1, 1);
            Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            Gdx.gl.glCullFace(GL20.GL_BACK);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
            Gdx.gl.glDepthMask(true);

            mModelBatch.begin(mServerWorldModel.getCamera());
            mModelBatch.render(Resources.INSTANCE.modelInstanceArray);
            mModelBatch.render(mServerWorldModel.getBulletWorld().instances, mEnvironment);
            mModelBatch.render(mServerWorldModel.getBulletWorld().levelInstance, mEnvironment);
            mModelBatch.end();
            buffer1.end();

            buffer1.getColorBufferTexture().bind();
            // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
            toonShader.begin();
            fullscreenQuad.render(toonShader, GL20.GL_TRIANGLE_STRIP, 0, 4);
            toonShader.end();


//            depthFrameBuffer.begin();
//            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//            Gdx.gl.glClearColor(0, 1, 1, 1);
//            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//            Gdx.gl.glCullFace(GL20.GL_BACK);
//            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
//            Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
//            Gdx.gl.glDepthMask(true);
//
//            depthModelBatch.begin(mServerWorldModel.getCamera());
//            depthModelBatch.render(mServerWorldModel.getBulletWorld().levelInstance, mEnvironment);
//            depthModelBatch.end();
//            depthFrameBuffer.end();
//
//            depthFrameBuffer.getColorBufferTexture().bind();
//            outLineShader.begin();
//            Gdx.gl.glEnable(GL10.GL_BLEND);
//            Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
//            // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//
//            fullscreenQuad.render(outLineShader, GL20.GL_TRIANGLE_STRIP, 0, 4);
//            outLineShader.end();


        } else {
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClearColor(0, 1, 1, 1);
            Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            mModelBatch.begin(mServerWorldModel.getCamera());
            mModelBatch.render(Resources.INSTANCE.modelInstanceArray);
            mModelBatch.render(mServerWorldModel.getBulletWorld().instances, mEnvironment);
            // mModelBatch.render(mServerWorldModel.getBulletWorld().fpsModel, mEnvironment);
            mModelBatch.render(mServerWorldModel.getBulletWorld().levelInstance, mEnvironment);
            mModelBatch.end();
        }



        if(PlayerController.DRAW_DEBUG_INFO) {
            mFPSRenderer.draw(delta);
            mBulletLoadRenderer.setText(BulletWorld.PERFORMANCE + "\n test");
            mBulletLoadRenderer.draw(delta);
            float x = round(mServerWorldModel.getPlayer().getPosition().x, 3);
            float y = round(mServerWorldModel.getPlayer().getPosition().y, 3);
            float z = round(mServerWorldModel.getPlayer().getPosition().z, 3);
            mPlayerPosRenderer.setText("X: " + x + ", Y: " + y + ", Z: " + z);
            mPlayerPosRenderer.draw(delta);
        }

        if(PlayerController.DRAW_DEBUG) {
            mDebugDrawer.lineRenderer.setProjectionMatrix(mServerWorldModel.getCamera().combined);
            mDebugDrawer.begin();
            mServerWorldModel.getBulletWorld().getCollisionWorld().debugDrawWorld();
            mDebugDrawer.end();
        }

        // Ray
        if(PlayerController.DRAW_SHOOT_DEBUG) {
            if(mServerWorldModel.getPlayer().hasShot) {

                btVector3 from = new btVector3(mServerWorldModel.getPlayer().from.x, mServerWorldModel.getPlayer().from.y, mServerWorldModel.getPlayer().from.z);
                btVector3 to = new btVector3(mServerWorldModel.getPlayer().to.x, mServerWorldModel.getPlayer().to.y, mServerWorldModel.getPlayer().to.z);
                btVector3 c = new btVector3(1f, 1f, 1f);
                mAimDebugDrawer.lineRenderer.setProjectionMatrix(mServerWorldModel.getCamera().combined);
                mAimDebugDrawer.begin();
                mAimDebugDrawer.drawLine(from, to, c);
                mAimDebugDrawer.end();

                from.dispose();
                to.dispose();
                c.dispose();
            }
        }

        if(mServerWorldModel.getPlayer().hasShot) {
            // mLaserRenderer.shoot();
            lmtr.render();
        }
        // mLaserRenderer.draw(delta);

        // "Crosshair"
        mCrosshairRenderer.begin(ShapeRenderer.ShapeType.Line);
        mCrosshairRenderer.setColor(Color.RED);
        mCrosshairRenderer.circle(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 5f);
        mCrosshairRenderer.end();

        mBulletHoleRenderer.draw(delta);

        mModelBatch.begin(mServerWorldModel.getCamera());
        Gdx.gl.glClear(GL10.GL_DEPTH_BUFFER_BIT);
        mModelBatch.render(mServerWorldModel.getBulletWorld().fpsModel, mEnvironment);
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
