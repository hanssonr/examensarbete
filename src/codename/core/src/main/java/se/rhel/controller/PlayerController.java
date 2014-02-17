package se.rhel.controller;

import com.badlogic.gdx.InputProcessor;
import se.rhel.model.FPSCamera;
import se.rhel.model.Player;
import com.badlogic.gdx.Input.Keys;
import java.util.HashMap;
import java.util.Map;

public class PlayerController implements InputProcessor {

    FPSCamera mCamera;
    Player mPlayer;

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
    }

    public void processCurrentInput() {
        if(mKeys.get(MapKeys.LEFT)) {

        }

        if(mKeys.get(MapKeys.RIGHT)) {

        }

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
