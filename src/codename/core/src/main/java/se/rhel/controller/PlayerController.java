package se.rhel.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import se.rhel.model.FPSCamera;
import se.rhel.model.Player;
import com.badlogic.gdx.Input.Keys;
import java.util.HashMap;
import java.util.Map;

public class PlayerController implements InputProcessor {

    FPSCamera mCamera;
    Player mPlayer;

    Vector3 movement = new Vector3();
    final float MOUSE_SPEED = 3f;
    final Vector3 ALWAYS_UP = new Vector3(0,1,0);

    int xRot = 0, yRot = 0;

    public enum MapKeys {
        LEFT, RIGHT, UP, DOWN, JUMP;
    }

    private static final Map<MapKeys, Boolean> mKeys = new HashMap<MapKeys, Boolean>();
    static {
        mKeys.put(MapKeys.LEFT, false);
        mKeys.put(MapKeys.RIGHT, false);
        mKeys.put(MapKeys.UP, false);
        mKeys.put(MapKeys.DOWN, false);
        mKeys.put(MapKeys.JUMP, false);
    }


    public PlayerController(FPSCamera camera, Player player) {
        mCamera = camera;
        mPlayer = player;

        Gdx.input.setCursorCatched(true);
    }

    public void processCurrentInput(float delta) {
        xRot = -Gdx.input.getDeltaX();
        yRot = Gdx.input.getDeltaY();

        mCamera.rotate(mCamera.direction.cpy().crs(ALWAYS_UP), -yRot * MOUSE_SPEED * delta);
        mCamera.rotate(ALWAYS_UP, xRot * MOUSE_SPEED * delta);

        //TODO: gör inte såhär, kom på något bättre sätt
        mPlayer.rotateBody(xRot * MOUSE_SPEED * delta);


        //zero out movement
        movement.set(0, 0, 0);

        if(mKeys.get(MapKeys.LEFT)) {
            movement.add(ALWAYS_UP.cpy().crs(mCamera.direction));
        }

        if(mKeys.get(MapKeys.RIGHT)) {
            movement.add(mCamera.direction.cpy().crs(ALWAYS_UP));
        }

        if(mKeys.get(MapKeys.UP)) {
            movement.add(new Vector3(mCamera.direction.x, 0, mCamera.direction.z));
        }

        if(mKeys.get(MapKeys.DOWN)) {
            movement.sub(new Vector3(mCamera.direction.x, 0, mCamera.direction.z));
        }

        mPlayer.move(movement.nor());
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
                mKeys.get(mKeys.put(MapKeys.UP, true));
                break;
            case Keys.DOWN:
            case Keys.S:
                mKeys.get(mKeys.put(MapKeys.DOWN, true));
                break;

            case Keys.ESCAPE:
                Gdx.app.exit();
                break;

            case Keys.F1:
                Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
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
                mKeys.get(mKeys.put(MapKeys.UP, false));
                break;
            case Keys.DOWN:
            case Keys.S:
                mKeys.get(mKeys.put(MapKeys.DOWN, false));
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
        return false;
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
