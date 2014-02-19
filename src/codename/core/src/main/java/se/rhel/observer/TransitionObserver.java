package se.rhel.observer;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Emil on 2014-02-19.
 * assigned to libgdx-gradle-template in se.rhel.observer
 */
public class TransitionObserver {

    private Array<TransitionListener> mListeners = new Array<>();

    public void add(TransitionListener toAdd) {
        mListeners.add(toAdd);
    }

    public void change(Screen toScreen) {
        for (TransitionListener transitionListener : mListeners) {
            transitionListener.change(toScreen);
        }
    }


    public void done() {
        for (TransitionListener transitionListener : mListeners) {
            transitionListener.done();
        }
    }

    public interface TransitionListener {
        public void change(Screen toScreen);
        public void done();
    }
}
