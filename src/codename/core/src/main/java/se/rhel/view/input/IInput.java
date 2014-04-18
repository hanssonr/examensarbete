package se.rhel.view.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by rkh on 2014-04-17.
 */
public interface IInput extends InputProcessor {

    public void processCurrentInput(float delta);
    public Vector3 getDirection();
    public Vector2 getRotation();
}
