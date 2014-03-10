package se.rhel.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Group: Logic
 *
 * Created by Emil on 2014-02-19.
 * assigned to libgdx-gradle-template in se.rhel.utils
 */
public enum Options {
    INSTANCE;

    private static final String OPTIONS_PREFERENCES = "OptPref";

    private static final String OPT_FULLSCREEN = "fullScreen";

    private static final boolean DEFAULT_FULLSCREN = false;

    private static Preferences mOptionsPrefs;
    static {
        mOptionsPrefs = Gdx.app.getPreferences(OPTIONS_PREFERENCES);
    }

    public void save() {
        mOptionsPrefs.flush();
    }

    public void setFullScreen(boolean val) {
        mOptionsPrefs.putBoolean(OPT_FULLSCREEN, val);
    }

    public boolean getFullScreen() {
        return mOptionsPrefs.getBoolean(OPT_FULLSCREEN, DEFAULT_FULLSCREN);
    }

}
