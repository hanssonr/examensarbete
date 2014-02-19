package se.rhel.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector2;
import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;
import se.rhel.model.WorldModel;
import se.rhel.res.Resources;

public class WorldView {

    private TextRenderer mFPSRenderer;
    private TextRenderer mTestTextRenderer;
    private SpriteBatch mSpriteBatch;
    private ModelBatch mModelBatch;

    private WorldModel mWorldModel;
    private Environment mEnvironment;

    public WorldView(WorldModel worldModel) {
        mWorldModel = worldModel;
        mModelBatch = new ModelBatch();
        mSpriteBatch = new SpriteBatch();

        mFPSRenderer = TextRenderer.FPS(worldModel, mSpriteBatch);
        mTestTextRenderer = new TextRenderer("HELLO WORLD!!", new Vector2(100, 100), worldModel, mSpriteBatch);

        mEnvironment = new Environment();
        mEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
        mEnvironment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        mModelBatch.begin(mWorldModel.getCamera());
        mModelBatch.render(Resources.INSTANCE.modelInstanceArray, mEnvironment);
        mModelBatch.end();

        mFPSRenderer.draw(delta);
        mTestTextRenderer.draw(delta);
    }

    public void dispose() {
        mModelBatch.dispose();
    }
}
