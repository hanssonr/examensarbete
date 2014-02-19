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

    ModelInstance pistol;

    public Player(Vector3 position) {
        super(position, new Vector3(0,0,0), 5f);

        instance = new ModelInstance(Bodybuilder.INSTANCE.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.ORANGE))), position);
        //Resources.INSTANCE.modelInstanceArray.add(instance);

        pistol = new ModelInstance(Resources.INSTANCE.getAssetManager().get("obj/beretta/beretta.obj", Model.class), position.x, position.y-0.2f, position.z);
        pistol.transform.rotate(new Vector3(0,1,0), 90);
        pistol.transform.setToTranslation(0, 1, 1);

        Resources.INSTANCE.modelInstanceArray.add(pistol);
    }

    public void update(float delta) {
        super.update(delta);
        instance.transform.setTranslation(getPosition());

        pistol.transform.setToTranslation(getPosition().x, getPosition().y-0.2f, getPosition().z);
        //pistol.transform.setToLookAt(getPosition(), getRotation(), new Vector3(0,1,0));

        //pistol.transform.set(getPosition(), new Quaternion().setEulerAngles(getRotation().x, getRotation().y, getRotation().z), new Vector3(1,1,1));
    }

    public void rotateBody(float amount) {
        instance.transform.rotate(new Vector3(0,1,0), amount);
    }

}
