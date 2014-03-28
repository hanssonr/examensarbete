package se.rhel.view;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.FPSCamera;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.res.Resources;

/**
 * Created by rkh on 2014-03-28.
 */
public class ExternalPlayerRenderer {

    //Instance
    ModelInstance mExternalPlayer = new ModelInstance(Resources.INSTANCE.playerModelAnimated);

    Array<ExternalPlayer> mPlayers;

    public ExternalPlayerRenderer(FPSCamera camera, Array<ExternalPlayer> players) {
        mPlayers = players;
    }

    public void draw(ModelBatch batch, Environment env) {

    }

    public void update(float delta) {

    }
}
