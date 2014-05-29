package se.rhel.screen;

import com.badlogic.gdx.Screen;
import se.rhel.CodeName;
import se.rhel.network.controller.LobbyScreen;
import se.rhel.screen.scene.MainMenu;


/**
 * Group: Logic
 */
public abstract class AbstactController implements Screen {

    private CodeName mGame;

    public AbstactController(CodeName game) {
        if(game instanceof CodeName) {
            mGame = (CodeName) game;
        } else {
            // TODO: Throw some kind of error
        }
    }

    public AbstactController() {

    }

    public CodeName getGame() { return mGame; }

    @Override
    public void render(float delta) {

        // TODO: Should we add prio for update? ATM this works fine for separation
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
