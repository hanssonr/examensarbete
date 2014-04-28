package se.rhel.view.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Input.Keys;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.Events;
import se.rhel.event.ViewEvent;

import java.util.HashMap;
import java.util.Map;


/**
 * Group: Logic
 */
public class PlayerInput implements IInput {

    public static boolean DRAW_DEBUG = false;
    public static boolean DRAW_MESH = true;
    public static boolean DRAW_DEBUG_INFO = true;
    public static boolean DRAW_SHOOT_DEBUG = false;

    public static boolean CLIENT_INTERPOLATION = true;

    private Vector2 mRotation = Vector2.Zero;
    private Vector3 mDirection = Vector3.Zero;

    private Events mEvents;

    //Finals
    final float MOUSE_SPEED = 5f;

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

    public PlayerInput(Events events) {
        Gdx.input.setCursorCatched(true);
        mEvents = events;
    }

    public Vector2 getRotation() {
        return mRotation;
    }
    public Vector3 getDirection() {
        return mDirection;
    }


    public void processCurrentInput(float delta) {
        if(Gdx.input.isCursorCatched()) {

            // ROTATION

            //Get the amount of rotation during last frame
            mRotation.x = -Gdx.input.getDeltaX() * MOUSE_SPEED * delta;
            mRotation.y = -Gdx.input.getDeltaY() * MOUSE_SPEED * delta;

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
            if(mKeys.get(MapKeys.JUMP)) {
                // TODO: Borde bara skickas en gång
                mEvents.notify(new ViewEvent(EventType.JUMP));
            }

            // SHOOT
            if(mKeys.get(MapKeys.SHOOT)) {
                mKeys.put(MapKeys.SHOOT, false);
                mEvents.notify(new ViewEvent(EventType.SHOOT));
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
            case Keys.F:
                mEvents.notify(new ViewEvent(EventType.GRENADE));
                break;
            case Keys.I:
                CLIENT_INTERPOLATION = !CLIENT_INTERPOLATION;
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
