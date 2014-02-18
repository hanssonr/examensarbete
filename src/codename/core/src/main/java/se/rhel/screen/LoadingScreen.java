package se.rhel.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import se.rhel.CodeName;
import se.rhel.res.Resources;

/**
 * Created by Emil on 2014-02-18.
 * assigned to libgdx-gradle-template in se.rhel.screen
 */
public class LoadingScreen implements Screen {

    private CodeName mGame;

    public LoadingScreen(CodeName game) {
        mGame = game;

        // Then start loading the main assets, which may take some time
        Resources.INSTANCE.load();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        if(Resources.INSTANCE.getAssetManager().update()) {
            Resources.INSTANCE.setInstances();

            mGame.setScreen(new GameScreen(mGame));
        }
        int progress = (int) (100 * Resources.INSTANCE.getAssetManager().getProgress());
        Gdx.app.log("Loading", "" + progress + "%");
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
