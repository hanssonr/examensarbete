package se.rhel;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import se.rhel.observer.TransitionObserver;
import se.rhel.screen.GameScreen;
import se.rhel.screen.LoadingScreen;
import se.rhel.screen.effects.TransitionScreen;


public class CodeName extends Game implements TransitionObserver.TransitionListener {

    public static final int RUNMODE_MENU = 0;
    public static final int RUNMODE_NO_MENU = 1;

    private TransitionScreen mTransition;
    private boolean mOnce = true;
    private static int mRunMode = 0;

    public CodeName() {
        this(RUNMODE_MENU);
    }

    public CodeName(int mode) {
        mRunMode = mode;
    }

    @Override
    public void create() {
        setScreen(new LoadingScreen(this));
    }

    @Override
    public void render() {
        super.render();

        if(mTransition != null) {
            mTransition.render();
        }
    }

    /**
     * Change screen with a transition
     * @param screen
     */
    public void setScreenWithTransition(Screen screen) {
        if(mTransition == null && mOnce) {
            mTransition = new TransitionScreen(this, screen);
            mOnce = false;
        }
    }

    public static boolean isInMenuMode() {
        return mRunMode == RUNMODE_MENU;
    }

    @Override
    public void done() {
        mTransition = null;
        mOnce = true;
    }

    @Override
    public void change(Screen toScreen) {
        setScreen(toScreen);
    }
}
