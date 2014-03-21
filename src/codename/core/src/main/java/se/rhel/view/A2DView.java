package se.rhel.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import se.rhel.model.WorldModel;
import se.rhel.model.client.ClientWorldModel;

/**
 * Group: Logic
 *
 * Created by Emil on 2014-02-18.
 * Abstract baseview for all 2D renderers
 */
public abstract class A2DView {

    protected WorldModel smWorldModel;
    protected SpriteBatch smSpriteBatch;
    protected Matrix4 smNormalProjection;

    public A2DView(WorldModel model, SpriteBatch batch) {
        smWorldModel = model;
        smSpriteBatch = batch;

        // Init a normalprojection
        smNormalProjection = new Matrix4();
        smNormalProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public abstract void draw(float delta);
    public abstract void dispose();

}
