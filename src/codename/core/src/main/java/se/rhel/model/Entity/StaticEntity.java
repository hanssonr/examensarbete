package se.rhel.model.Entity;


import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public abstract class StaticEntity extends GameObject {

    protected StaticEntity(Vector3 position, ModelInstance instance) {
        super(position, instance);
    }

}
