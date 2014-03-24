package se.rhel.model;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;


/**
 * Group: Logic
 */
public enum Bodybuilder {
    INSTANCE;

    ModelBuilder mBuilder = new ModelBuilder();

    /**
     * Crates a box body
     * @param w = width
     * @param h = height
     * @param d = depth
     * @param material = Material
     */
    public Model createBox(float w, float h, float d, Material material) {
        return mBuilder.createBox(w, h, d, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }

    public Model createBox(Vector3 size, Material material) {
        return mBuilder.createBox(size.x, size.y, size.z, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }

    public Model createBox(Vector3 size, Color color) {
        return mBuilder.createBox(size.x, size.y, size.z, createMaterial(color),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }

    public Model createBox(float w, float h, float d) {
        Material material = new Material(ColorAttribute.createDiffuse(Color.BLUE));
        return createBox(w, h, d, material);
    }

    public Model createCapsule(float radius, float height) {
        return mBuilder.createCapsule(radius, height, 16, createMaterial(Color.MAGENTA), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
    }

    private Material createMaterial(Color color) {
        return new Material(ColorAttribute.createDiffuse(color));
    }
}
