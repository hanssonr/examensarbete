package se.rhel.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import se.rhel.model.BulletTest.DebugDrawer;
import se.rhel.model.BulletWorld;
import se.rhel.model.WorldModel;
import se.rhel.res.Resources;

public class WorldView {

    private TextRenderer mFPSRenderer;
    private TextRenderer mBulletLoadRenderer;

    private TextRenderer mTestTextRenderer;
    private SpriteBatch mSpriteBatch;
    private ModelBatch mModelBatch;

    private WorldModel mWorldModel;
    private Environment mEnvironment;

    public WorldView(WorldModel worldModel) {
        mWorldModel = worldModel;
        mSpriteBatch = new SpriteBatch();
        mModelBatch = new ModelBatch();

        mFPSRenderer = TextRenderer.FPS(worldModel, mSpriteBatch);

        mTestTextRenderer = new TextRenderer("HELLO WORLD!!", new Vector2(100, 100), worldModel, mSpriteBatch);
        mBulletLoadRenderer = new TextRenderer("Bullet init", new Vector2(10, Gdx.graphics.getHeight() - 30), worldModel, mSpriteBatch);

        mEnvironment = new Environment();
        mEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1.0f));
        mEnvironment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        mEnvironment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f));
    }

    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        mModelBatch.begin(mWorldModel.getCamera());
        mModelBatch.render(Resources.INSTANCE.modelInstanceArray, mEnvironment);
        mModelBatch.render(mWorldModel.getBulletWorld().instances, mEnvironment);
        mModelBatch.end();

        mWorldModel.getBulletWorld().getCollisionWorld().debugDrawWorld();

        mFPSRenderer.draw(delta);
        mBulletLoadRenderer.setText(BulletWorld.PERFORMANCE + "\n test");
        mBulletLoadRenderer.draw(delta);
        mTestTextRenderer.draw(delta);
    }

    public void dispose() {
        mModelBatch.dispose();
    }
}
