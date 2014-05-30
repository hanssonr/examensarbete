package se.rhel.view;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import se.rhel.res.Resources;

/**
 * Created by rkh on 2014-04-09.
 */
public class LevelRenderer {

    ModelInstance mWorld;
    ModelInstance mSky;

    public LevelRenderer() {
        mWorld = new ModelInstance(Resources.INSTANCE.levelModelVisual);
        mSky = new ModelInstance(Resources.INSTANCE.space);
    }

    public void render(ModelBatch batch, Environment env) {
       batch.render(mSky);
       batch.render(mWorld, env);
    }
}
