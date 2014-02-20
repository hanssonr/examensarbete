package se.rhel.model.BulletTest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Emil on 2014-02-20.
 * assigned to libgdx-gradle-template in se.rhel.model.BulletTest
 */
public abstract class BaseEntity implements Disposable {

    public Matrix4 transform;
    public ModelInstance modelInstance;
    private Color color = new Color(1f, 1f, 1f, 1f);
    public Color getColor () {
        return color;
    }
    public void setColor (Color color) {
        setColor(color.r, color.g, color.b, color.a);
    }
    public void setColor (float r, float g, float b, float a) {
        color.set(r, g, b, a);
        if (modelInstance != null) {
            for (Material m : modelInstance.materials) {
                ColorAttribute ca = (ColorAttribute)m.get(ColorAttribute.Diffuse);
                if (ca != null)
                    ca.color.set(r, g, b, a);
            }
        }
    }
}
