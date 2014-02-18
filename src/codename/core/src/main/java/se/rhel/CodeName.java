package se.rhel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import se.rhel.screen.GameScreen;
import se.rhel.screen.LoadingScreen;


public class CodeName extends Game {

    @Override
    public void create() {
        setScreen(new LoadingScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    public void setScreenWithTransition(Screen screen) {

    }
}
