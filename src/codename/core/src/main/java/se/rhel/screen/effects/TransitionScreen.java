package se.rhel.screen.effects;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Stage;
import se.rhel.CodeName;

/**
 * Created by Emil on 2014-02-18.
 * assigned to libgdx-gradle-template in se.rhel.screen.effects
 */
public class TransitionScreen {

    private CodeName mGame;
    private TweenManager mManager;
    private Color mColor = new Color(0, 0, 0, 0);
    private Matrix4 mNormal;
    private TweenCallback mTransitionComplete, mTimeToChangeScreen;
    private ShapeRenderer mShapeRenderer;
    private Screen mToScreen;
    private boolean mTransitionInProgress;

    public TransitionScreen(CodeName game, Screen toScreen) {
        mGame = game;
        mToScreen = toScreen;

        mNormal = new Matrix4();
        mNormal.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        mShapeRenderer = new ShapeRenderer();

        mTransitionInProgress = true;
        show();
    }

    public void render() {
        mManager.update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glEnable(GL10.GL_BLEND);
        Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        mShapeRenderer.setProjectionMatrix(mNormal);
        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        mShapeRenderer.setColor(mColor.r, mColor.g, mColor.b, mColor.a);
        mShapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        mShapeRenderer.end();
    }

    /**
     * Init the Tween-specific values
     */
    public void show() {
        mManager = new TweenManager();
        Tween.registerAccessor(Color.class, new ColorAccessor());

        mTransitionComplete = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                mTransitionInProgress = false;
            }
        };

        mTimeToChangeScreen = new TweenCallback() {
           @Override
           public void onEvent(int type, BaseTween<?> source) {
               mGame.setScreen(mToScreen);
           }
       };

        Timeline.createSequence()
                .push(Tween.to(mColor, ColorAccessor.ALPHA, 2f)
                        .target(1f)
                        .setCallback(mTimeToChangeScreen)
                        .setCallbackTriggers(TweenCallback.COMPLETE))
                .push(Tween.to(mColor, ColorAccessor.ALPHA, 2f)
                        .target(0f))
                .setCallback(mTransitionComplete)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(mManager);
    }

    public boolean isTransitionInProgress() {
        return mTransitionInProgress;
    }
}
