package se.rhel.view;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import se.rhel.model.WorldModel;

public class WorldView {

    private TextRenderer mFPSRenderer;
    private TextRenderer mTestTextRenderer;
    private SpriteBatch mSpriteBatch;

    public WorldView(WorldModel worldModel) {
        mSpriteBatch = new SpriteBatch();

        mFPSRenderer = TextRenderer.FPS(worldModel, mSpriteBatch);
        mTestTextRenderer = new TextRenderer("HELLO WORLD!!", new Vector2(100, 100), worldModel, mSpriteBatch);
    }

    public void render(float delta) {
//        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        Gdx.gl.glClearColor(1, 1, 1, 1);
//        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        mFPSRenderer.draw(delta);
        mTestTextRenderer.draw(delta);
    }
}
