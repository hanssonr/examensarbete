package se.rhel.model.BulletTest;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.FloatCounter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.PerformanceCounter;
import com.badlogic.gdx.utils.SharedLibraryLoader;

/** @author xoppa */
public class BulletTest implements ApplicationListener, InputProcessor, GestureListener {
    public StringBuilder performance = new StringBuilder();
    public String instructions = "Tap to shoot\nLong press to toggle debug mode\nSwipe for next test\nCtrl+drag to rotate\nScroll to zoom";
    public PerformanceCounter performanceCounter = new PerformanceCounter(this.getClass().getSimpleName());
    public FloatCounter fpsCounter = new FloatCounter(5);
    public PerspectiveCamera camera;

    @Override
    public boolean keyDown (int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        return false;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled (int amount) {
        return false;
    }

    @Override
    public void create () {
    }

    @Override
    public void resize (int width, int height) {
    }

    @Override
    public void render () {
    }

    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void dispose () {
    }

    @Override
    public boolean touchDown (float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap (float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress (float x, float y) {
        return false;
    }

    @Override
    public boolean fling (float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan (float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop (float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom (float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch (Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
}
