package se.rhel.screen.effects;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.Color;

/**
 * Group: Logic
 *
 * Created by Emil on 2014-02-18.
 * assigned to libgdx-gradle-template in se.rhel.screen.effects
 */
public class ColorAccessor implements TweenAccessor<Color> {

    public static final int ALPHA = 1;

    @Override
    public int getValues(Color target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case ALPHA:
            default:
                returnValues[0] = target.a;
                return 1;
        }
    }

    @Override
    public void setValues(Color target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case ALPHA:
            default:
                target.a = newValues[0];
                break;
        }
    }
}
