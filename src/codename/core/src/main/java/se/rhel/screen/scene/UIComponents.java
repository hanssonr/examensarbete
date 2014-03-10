package se.rhel.screen.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Group: Logic
 *
 * Created by Emil on 2014-02-19.
 * assigned to libgdx-gradle-template in se.rhel.screen.scene
 */
public class UIComponents {

    private static Skin mDefaultSkin;

    static {
        mDefaultSkin = new Skin(Gdx.files.internal("data/skins/uiskin.json"));
    }

    public static TextButton getDefaultTextButton(String text, float w, float h) {
        TextButton tb = new TextButton(text, mDefaultSkin);
        tb.setWidth(w);
        tb.setHeight(h);
        return tb;
    }

    public static CheckBox getDefaultCheckBox(String text) {
        CheckBox cb = new CheckBox(text, mDefaultSkin);
        return cb;
    }

    // Class should not be initialized
    private UIComponents() {}
}
