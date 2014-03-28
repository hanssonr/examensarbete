package se.rhel.view.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Input.Keys;

import java.util.HashMap;
import java.util.Map;


/**
 * Group: Logic
 */
public class PlayerInput implements InputProcessor {

    public static boolean DRAW_DEBUG = false;
    public static boolean DRAW_MESH = true;
    public static boolean DRAW_DEBUG_INFO = true;
    public static boolean DRAW_SHOOT_DEBUG = false;

    private boolean mPlayerShot = false;
    private boolean mPlayerJump = false;
    private Vector2 mRotation = Vector2.Zero;
    private Vector3 mDirection = Vector3.Zero;

    //Finals
    final float MOUSE_SPEED = 5f;
    final float MAX_YROT = 60f;
    final float MIN_YROT = -60f;

    //Rotation
    float currentRot = 0;

    public enum MapKeys {
        LEFT, RIGHT, FORWARD, BACK, JUMP, SHOOT
    }

    private static final Map<MapKeys, Boolean> mKeys = new HashMap<MapKeys, Boolean>();
    static {
        mKeys.put(MapKeys.LEFT, false);
        mKeys.put(MapKeys.RIGHT, false);
        mKeys.put(MapKeys.FORWARD, false);
        mKeys.put(MapKeys.BACK, false);
        mKeys.put(MapKeys.JUMP, false);
        mKeys.put(MapKeys.SHOOT, false);
    }

    public PlayerInput() {
        Gdx.input.setCursorCatched(true);
    }

    public Vector2 getRotation() {
        return mRotation;
    }

    public Vector3 getDirection() {
        return mDirection;
    }

    public boolean isShooting() {
        return mPlayerShot;
    }

    public boolean isJumping() {
        return mPlayerJump;
    }

    public void processCurrentInput(float delta) {
        if(Gdx.input.isCursorCatched()) {

            // ROTATION

            //Get the amount of rotation during last frame
            mRotation.x = -Gdx.input.getDeltaX() * MOUSE_SPEED * delta;
            mRotation.y = -Gdx.input.getDeltaY() * MOUSE_SPEED * delta;

            currentRot += mRotation.y;

            //prevent deadlock
            if (currentRot < MIN_YROT || currentRot > MAX_YROT) {
                mRotation.y = 0;
            }

            currentRot = MathUtils.clamp(currentRot, MIN_YROT, MAX_YROT);


            // MOVEMENT
            mDirection.set(0,0,0);

            if(mKeys.get(MapKeys.FORWARD)) {
                mDirection.z = 1;
            }
            if(mKeys.get(MapKeys.BACK)) {
                mDirection.z = -1;
            }

            if(mKeys.get(MapKeys.LEFT)) {
                mDirection.x = -1;
            }
            if(mKeys.get(MapKeys.RIGHT)) {
                mDirection.x = 1;
            }

            mDirection.nor();


            // JUMP
            mPlayerJump = false;
            if(mKeys.get(MapKeys.JUMP)) {
                mPlayerJump = true;
            }

            // SHOOT
            mPlayerShot = false;
            if(mKeys.get(MapKeys.SHOOT)) {
                mPlayerShot = true;

                mKeys.put(MapKeys.SHOOT, false);
            }

        }
    }

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Keys.LEFT:
            case Keys.A:
                mKeys.put(MapKeys.LEFT, true);
                break;
            case Keys.RIGHT:
            case Keys.D:
                mKeys.put(MapKeys.RIGHT, true);
                break;
            case Keys.UP:
            case Keys.W:
                mKeys.put(MapKeys.FORWARD, true);
                break;
            case Keys.DOWN:
            case Keys.S:
                mKeys.put(MapKeys.BACK, true);
                break;

            case Keys.ESCAPE:
                Gdx.app.exit();
                break;

            case Keys.F1:
                Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
                break;
            case Keys.SPACE:
                mKeys.put(MapKeys.JUMP, true);
                break;
            case Keys.F2:
                DRAW_DEBUG = !DRAW_DEBUG;
                break;
            case Keys.F3:
                DRAW_MESH = !DRAW_MESH;
                break;
            case Keys.F4:
                DRAW_DEBUG_INFO = !DRAW_DEBUG_INFO;
                break;
            case Keys.F5:
                DRAW_SHOOT_DEBUG = !DRAW_SHOOT_DEBUG;
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch(keycode) {
            case Keys.LEFT:
            case Keys.A:
                mKeys.put(MapKeys.LEFT, false);
                break;
            case Keys.RIGHT:
            case Keys.D:
                mKeys.put(MapKeys.RIGHT, false);
                break;
            case Keys.UP:
            case Keys.W:
                mKeys.put(MapKeys.FORWARD, false);
                break;
            case Keys.DOWN:
            case Keys.S:
                mKeys.put(MapKeys.BACK, false);
                break;
            case Keys.SPACE:
                mKeys.put(MapKeys.JUMP, false);
                break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        switch(button) {
            case Input.Buttons.LEFT:
                mKeys.put(MapKeys.SHOOT, true);
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
