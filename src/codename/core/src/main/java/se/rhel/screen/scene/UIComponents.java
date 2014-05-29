package se.rhel.screen.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import java.awt.*;

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

    public static TextField getDefaultTextField(String messagetext) {
        TextField tf = new TextField("", mDefaultSkin);
        tf.setMessageText(messagetext);
        return tf;
    }

    public static Label getErrorLabel(String text) {
        Label lb = new Label(text, mDefaultSkin);
        lb.setColor(255, 0, 0, 1);
        return lb;
    }

    public static Label getDefaultLabel(String text) {
        Label lb = new Label(text, mDefaultSkin);
        lb.setColor(Color.valueOf("272B30"));
        return lb;
    }

    // Class should not be initialized
    private UIComponents() {}
}
