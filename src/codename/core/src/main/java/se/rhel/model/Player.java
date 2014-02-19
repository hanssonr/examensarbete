package se.rhel.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import se.rhel.model.Entity.DynamicEntity;
import se.rhel.res.Resources;

public class Player extends DynamicEntity {

    Model mModel;
    ModelInstance instance;

    public Player(Vector3 position) {
        super(position, new Vector3(0,0,0), 4f);

        instance = new ModelInstance(Bodybuilder.INSTANCE.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.ORANGE))), position);
        Resources.INSTANCE.modelInstanceArray.add(instance);
    }

    public void update(float delta) {
        super.update(delta);

        instance.transform.setTranslation(getPosition());
    }

    public void rotateBody(float amount) {
        instance.transform.rotate(new Vector3(0,1,0), amount);
    }

}
