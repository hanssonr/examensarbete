package se.rhel.screen;

import com.badlogic.gdx.Screen;
import se.rhel.CodeName;

public abstract class BaseScreen implements Screen {

    private CodeName mGame;

    public BaseScreen(CodeName game) {
        if(game instanceof CodeName) {
            mGame = (CodeName) game;
        } else {
            // TODO: Throw some kind of error
        }
    }

    public CodeName getGame() { return mGame; }

    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
    }

    public abstract void update(float delta);
    public abstract void draw(float delta);

    @Override
    public void resize(int width, int height) {}

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {}
}
