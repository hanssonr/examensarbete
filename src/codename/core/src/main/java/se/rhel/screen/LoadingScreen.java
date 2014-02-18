package se.rhel.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import se.rhel.CodeName;
import se.rhel.res.Resources;
import se.rhel.screen.scene.MainMenu;

/**
 * Created by Emil on 2014-02-18.
 * assigned to libgdx-gradle-template in se.rhel.screen
 */
public class LoadingScreen implements Screen {

    private CodeName mGame;
    private BitmapFont mFont;
    private Matrix4 mNormalProjection;
    private SpriteBatch mSpriteBatch;

    public LoadingScreen(CodeName game) {
        mGame = game;
        mFont = new BitmapFont(Gdx.files.internal("data/fonts/hud.fnt"));
        mFont.setColor(Color.BLACK);
        mFont.scale(1.5f);
        mNormalProjection = new Matrix4();
        mNormalProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mSpriteBatch = new SpriteBatch();

        // Then start loading the main assets, which may take some time
        Resources.INSTANCE.load();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        if(Resources.INSTANCE.getAssetManager().update()) {
            Resources.INSTANCE.setInstances();

            mGame.setScreenWithTransition(new MainMenu(mGame));
            // mGame.setScreenWithTransition(new GameScreen(mGame));
        }

        mSpriteBatch.begin();
        mSpriteBatch.setProjectionMatrix(mNormalProjection);
        int progress = (int) (100 * Resources.INSTANCE.getAssetManager().getProgress());
        mFont.draw(mSpriteBatch, "Loading " + progress + "%", Gdx.graphics.getWidth() / 2 - (200), Gdx.graphics.getHeight() / 2);
        mSpriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
