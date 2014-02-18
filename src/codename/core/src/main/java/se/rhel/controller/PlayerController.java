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

    int xRot = 0, yRot = 0;

    public enum MapKeys {
        LEFT, RIGHT, UP, DOWN, JUMP, ROT_LEFT, ROT_RIGHT;
    }

    private static final Map<MapKeys, Boolean> mKeys = new HashMap<MapKeys, Boolean>();
    static {
        mKeys.put(MapKeys.LEFT, false);
        mKeys.put(MapKeys.RIGHT, false);
        mKeys.put(MapKeys.UP, false);
        mKeys.put(MapKeys.DOWN, false);
        mKeys.put(MapKeys.JUMP, false);
        mKeys.put(MapKeys.ROT_LEFT, false);
        mKeys.put(MapKeys.ROT_RIGHT, false);
    }


    public PlayerController(FPSCamera camera, Player player) {
        mCamera = camera;
        mPlayer = player;

        Gdx.input.setCursorCatched(true);
    }

    public void processCurrentInput(float delta) {
        mCamera.rotate(new Quaternion().setEulerAngles(-xRot * 5 * delta, 0, 0));
        xRot = yRot = 0;


        //zero out movement
        movement.set(0, 0, 0);

        if(mKeys.get(MapKeys.LEFT)) {
            movement.add(mCamera.up.cpy().crs(mCamera.direction));
        }

        if(mKeys.get(MapKeys.RIGHT)) {
            movement.add(mCamera.direction.cpy().crs(mCamera.up));
        }

        if(mKeys.get(MapKeys.UP)) {
            movement.add(mCamera.direction.cpy());
        }

        if(mKeys.get(MapKeys.DOWN)) {
            movement.add(mCamera.direction.cpy().scl(-1));
        }

        mPlayer.move(movement.nor());

        if(mKeys.get(MapKeys.ROT_LEFT)) {
            mCamera.rotate(new Quaternion().setEulerAngles(1,0,0));
        }

        if(mKeys.get(MapKeys.ROT_RIGHT)) {
            mCamera.rotate(new Quaternion().setEulerAngles(-1,0,0));
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

            case Keys.O:
                mKeys.get(mKeys.put(MapKeys.ROT_LEFT, true));
                break;
            case Keys.P:
                mKeys.get(mKeys.put(MapKeys.ROT_RIGHT, true));
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
                mKeys.get(mKeys.put(MapKeys.UP, false));
                break;
            case Keys.DOWN:
            case Keys.S:
                mKeys.get(mKeys.put(MapKeys.DOWN, false));
                break;

            case Keys.O:
                mKeys.get(mKeys.put(MapKeys.ROT_LEFT, false));
                break;
            case Keys.P:
                mKeys.get(mKeys.put(MapKeys.ROT_RIGHT, false));
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
        if (Gdx.input.isCursorCatched()) {
            if (Gdx.input.getDeltaX() != 0)
                //xRot = Gdx.input.getDeltaX() / Math.abs(Gdx.input.getDeltaX());
                xRot = Gdx.input.getDeltaX();

            if (Gdx.input.getDeltaY() != 0)
                //yRot = Gdx.input.getDeltaY() / Math.abs(Gdx.input.getDeltaY());
                yRot = Gdx.input.getDeltaY();
        }

        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
