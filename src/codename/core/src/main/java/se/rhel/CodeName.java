package se.rhel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import se.rhel.screen.LoadingScreen;
import se.rhel.screen.effects.TransitionScreen;


public class CodeName extends Game {

    private TransitionScreen mTransition;
    private boolean mOnce = true;

    @Override
    public void create() {
        setScreen(new LoadingScreen(this));
    }

    @Override
    public void render() {
        super.render();

        if(mTransition != null && mTransition.isTransitionInProgress()) {
            mTransition.render();
        }
    }

    /**
     * Change screen with a transition
     * @param screen
     */
    public void setScreenWithTransition(Screen screen) {
        if(mOnce) {
            mTransition = new TransitionScreen(this, screen);
            mOnce = false;
        }
    }

}
