package se.rhel.screen;

import com.badlogic.gdx.Screen;
import se.rhel.CodeName;


/**
 * Group: Logic
 */
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

        // TODO: Should we add prio for update? ATM this works fine for seperation
        update(delta);
        draw(delta);

        // Gdx.app.log("Render delta", "Delta: " + delta);
        // Gdx.app.log("Graphics delta", "GDelta: " + Gdx.graphics.getDeltaTime());
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
