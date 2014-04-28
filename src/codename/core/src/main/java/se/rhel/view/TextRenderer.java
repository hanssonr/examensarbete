package se.rhel.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import se.rhel.model.IWorldModel;
import se.rhel.res.Resources;

/**
 * Group: Logic
 *
 * Created by Emil on 2014-02-18.
 *
 */
public class TextRenderer extends A2DView {

    private static final Vector2 FPS_POS = new Vector2(10, Gdx.graphics.getHeight() - 10);

    private Vector2 mPosition;
    private String mText;
    private BitmapFont mFont;
    private boolean mFps = false;

    // Used for rendering fps only
    private TextRenderer(IWorldModel model, SpriteBatch batch) {
        this("", FPS_POS, model, batch);
        mFps = true;
    }

    // Normal constructor
    public TextRenderer(String text, Vector2 pos, IWorldModel model, SpriteBatch batch) {
        super(model, batch);
        mPosition = pos;
        mText = text;

        // DaFont
        mFont = Resources.INSTANCE.hudFont;
        mFont.setColor(Color.WHITE);
    }

    public void setText(String text) {
        mText = text;
    }

    @Override
    public void draw(float delta) {
        smSpriteBatch.begin();
        smSpriteBatch.setProjectionMatrix(smNormalProjection);

        if(mFps) {
            mFont.draw(smSpriteBatch, "FPS: " + Integer.toString(Gdx.graphics.getFramesPerSecond()), mPosition.x, mPosition.y);
        } else {
            mFont.draw(smSpriteBatch, mText, mPosition.x, mPosition.y);
        }

        smSpriteBatch.end();
    }

    @Override
    public void dispose() {
        mFont.dispose();
    }

    // Helper for not calling wrong constructor
    public static TextRenderer FPS(IWorldModel model, SpriteBatch batch) {
        return new TextRenderer(model, batch);
    }
}
