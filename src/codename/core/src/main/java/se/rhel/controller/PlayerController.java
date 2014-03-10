package se.rhel.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import se.rhel.client.ClientController;
import se.rhel.model.FPSCamera;
import se.rhel.model.Player;
import com.badlogic.gdx.Input.Keys;
import se.rhel.model.client.ClientWorldModel;

import java.util.HashMap;
import java.util.Map;


/**
 * Group: Mixed
 */
public class PlayerController extends ClientController implements InputProcessor {

    public static boolean DRAW_DEBUG = false;
    public static boolean DRAW_MESH = true;
    public static boolean DRAW_DEBUG_INFO = true;
    public static boolean DRAW_SHOOT_DEBUG = false;

    FPSCamera mCamera;
    Player mPlayer;

    Quaternion rQuat = new Quaternion();
    Vector3 movement = new Vector3();
    Vector3 tmp = new Vector3();

    //Finals
    final float JUMP_HEIGHT = 7f;
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


    public PlayerController(FPSCamera camera, ClientWorldModel model) {
        super();

        super.addListener(model);
        mCamera = camera;
        mPlayer = model.getPlayer();

        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void send() {
        // super.send();
    }

    public void processCurrentInput(float delta) {
        if(Gdx.input.isCursorCatched()) {
            //Get the amount of rotation during last frame
            xRot = -Gdx.input.getDeltaX() * MOUSE_SPEED * delta;
            yRot = -Gdx.input.getDeltaY() * MOUSE_SPEED * delta;

            //Y-rotation
            currentRot += yRot;
            currentRot = MathUtils.clamp(currentRot, MIN_YROT, MAX_YROT);

            if (currentRot > MIN_YROT && currentRot < MAX_YROT && yRot != 0) {
                mCamera.rotate(mCamera.getRight(), yRot);
            }

            //X-rotation
            if (xRot != 0) {
                mCamera.rotate(FPSCamera.UP, xRot);
                //mPlayer.rotate(FPSCamera.UP, xRot);
            }

            //Zero out movement
            tmp.set(Vector3.Zero);

            //Calculate movement
            if(mKeys.get(MapKeys.FORWARD)) {
               tmp.add(mCamera.getForward());
            }
            if(mKeys.get(MapKeys.BACK)) {
                tmp.sub(mCamera.getForward());
            }

            if(mKeys.get(MapKeys.LEFT)) {
                tmp.sub(mCamera.getRight());
            }
            if(mKeys.get(MapKeys.RIGHT)) {
                tmp.add(mCamera.getRight());
            }

            tmp.nor();
            movement.x = tmp.x;
            movement.z = tmp.z;

            if(!mPlayer.isGrounded()) {
                movement.y -=  15 * delta;
            } else {
                if(movement.y < -10) movement.y = -10;
                movement.y += 15 * delta;
                if(movement.y > 0) movement.y = 0;
            }

            if(mKeys.get(MapKeys.JUMP) && mPlayer.isGrounded()) {
                movement.y = JUMP_HEIGHT;
            }

            mPlayer.move(movement);
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
                break;
            case Keys.SPACE:
                mKeys.get(mKeys.put(MapKeys.JUMP, true));
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
            case Keys.SPACE:
                mKeys.get(mKeys.put(MapKeys.JUMP, false));
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
        mPlayer.shoot();
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
