package se.rhel.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import se.rhel.model.Entity.DynamicEntity;

public class Player extends DynamicEntity {

    Model mModel;

    public Player(Vector3 position) {
        super(position, new Vector3(0,0,0), 4f);

        mModel = Bodybuilder.INSTANCE.createBox(1f, 2f, 1f, new Material(ColorAttribute.createDiffuse(Color.BLUE)));
    }

    public void update(float delta) {
        super.update(delta);
    }

}
