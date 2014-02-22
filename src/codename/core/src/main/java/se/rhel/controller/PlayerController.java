package se.rhel.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import se.rhel.model.FPSCamera;
import se.rhel.model.Player;
import com.badlogic.gdx.Input.Keys;
import java.util.HashMap;
import java.util.Map;

public class PlayerController implements InputProcessor {

    public static boolean DRAW_DEBUG = false;
    public static boolean DRAW_MESH = true;
    public static boolean DRAW_DEBUG_INFO = true;
    public static boolean DRAW_SHOOT_DEBUG = false;

    FPSCamera mCamera;
    Player mPlayer;

    Vector3 movement = new Vector3();
    Vector3 tmp = new Vector3();

    //Finals
    final float MOUSE_SPEED = 5f;
    final float MAX_YROT = 80f;
    final float MIN_YROT = -80f;

    //Rotation
    float xRot = 0, yRot = 0, currentRot = 0;

    public enum MapKeys {
        LEFT, RIGHT, FORWARD, BACK, JUMP
    }

    private static final Map<MapKeys, Boolean> mKeys = new HashMap<MapKeys, Boolean>();
    static {
        mKeys.put(MapKeys.LEFT, false);
        mKeys.put(MapKeys.RIGHT, false);
        mKeys.put(MapKeys.FORWARD, false);
        mKeys.put(MapKeys.BACK, false);
        mKeys.put(MapKeys.JUMP, false);
    }


    public PlayerController(FPSCamera camera, Player player) {
        mCamera = camera;
        mPlayer = player;

        Gdx.input.setCursorCatched(true);
    }

    public void processCurrentInput(float delta) {
        //Get the amount of rotation during last frame
        xRot = -Gdx.input.getDeltaX() * MOUSE_SPEED * delta;
        yRot = -Gdx.input.getDeltaY() * MOUSE_SPEED * delta;

        //Y-rotation
        currentRot += yRot;
        currentRot = MathUtils.clamp(currentRot, MIN_YROT, MAX_YROT);

        if (currentRot > MIN_YROT && currentRot < MAX_YROT && yRot != 0) {
            mCamera.rotate(mCamera.direction.cpy().crs(FPSCamera.UP), yRot);
        }

        //X-rotation
        if (xRot != 0) {
            mCamera.rotate(FPSCamera.UP, xRot);
            mPlayer.rotate(FPSCamera.UP, xRot);
        }

        //Zero out movement
        movement.set(0, 0, 0);

        //Calculate movement
        if(mKeys.get(MapKeys.LEFT)) {
            movement.add(FPSCamera.UP.cpy().crs(mCamera.direction));
        }
        if(mKeys.get(MapKeys.RIGHT)) {
            movement.add(mCamera.direction.cpy().crs(FPSCamera.UP));
        }

        if(mKeys.get(MapKeys.FORWARD)) {
            movement.add(tmp.set(mCamera.direction.x, 0, mCamera.direction.z));
        }
        if(mKeys.get(MapKeys.BACK)) {
            movement.sub(tmp.set(mCamera.direction.x, 0, mCamera.direction.z));
        }

        mPlayer.move(movement.nor().scl(delta));
    }

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Keys.LEFT:
            case Keys.A:
                mKeys.get(mKeys.put(MapKeys.LEFT, true));
                break;
            case Keys.RIGHT:
            case Keys.D:
                mKeys.get(mKeys.put(MapKeys.RIGHT, true));
                break;
            case Keys.UP:
            case Keys.W:
                mKeys.get(mKeys.put(MapKeys.FORWARD, true));
                break;
            case Keys.DOWN:
            case Keys.S:
                mKeys.get(mKeys.put(MapKeys.BACK, true));
                break;

            case Keys.ESCAPE:
                Gdx.app.exit();
                break;

            case Keys.F1:
                Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
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
                mKeys.get(mKeys.put(MapKeys.LEFT, false));
                break;
            case Keys.RIGHT:
            case Keys.D:
                mKeys.get(mKeys.put(MapKeys.RIGHT, false));
                break;
            case Keys.UP:
            case Keys.W:
                mKeys.get(mKeys.put(MapKeys.FORWARD, false));
                break;
            case Keys.DOWN:
            case Keys.S:
                mKeys.get(mKeys.put(MapKeys.BACK, false));
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
        Ray r = mCamera.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);

        mPlayer.shoot(r);
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
