package se.rhel;

import com.badlogic.gdx.Game;
import se.rhel.screen.GameScreen;


public class CodeName extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }
}
