package se.rhel.model;


import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

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
        return mBuilder.createBox(w, h, d, material, VertexAttributes.Usage.Position);
    }
}
