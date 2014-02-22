package se.rhel.res;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Emil on 2014-02-18.
 * assigned to libgdx-gradle-template in se.rhel.res
 */
public enum Resources {
    INSTANCE;

    private static AssetManager mManager;
    static { mManager = new AssetManager(); }

    public BitmapFont hudFont;
    public Array<ModelInstance> modelInstanceArray;

    public Model levelModel;
    public Model playerModel;
    public Texture bulletHole;

    public void load() {
        mManager.load("data/fonts/hud.fnt", BitmapFont.class);
        mManager.load("obj/ship/ship.obj", Model.class);
        mManager.load("obj/level/level.g3db", Model.class);
        mManager.load("obj/player/player.g3db", Model.class);
        mManager.load("obj/beretta/beretta.obj", Model.class);
        mManager.load("obj/skybox/spacesphere.obj", Model.class);
        mManager.load("tex/bullethole.png", Texture.class);

        Bullet.init();
    }

    public void setInstances() {
        modelInstanceArray = new Array<>();

        hudFont = mManager.get("data/fonts/hud.fnt", BitmapFont.class);

        bulletHole = mManager.get("tex/bullethole.png", Texture.class);

        Model shipModel = mManager.get("obj/ship/ship.obj", Model.class);
        ModelInstance instance = new ModelInstance(shipModel, 0,1,0);
        modelInstanceArray.add(instance);

        Model space = mManager.get("obj/skybox/spacesphere.obj", Model.class);
        instance = new ModelInstance(space);
        modelInstanceArray.add(instance);

        levelModel = mManager.get("obj/level/level.g3db", Model.class);
        playerModel = mManager.get("obj/player/player.g3db", Model.class);
    }

    public AssetManager getAssetManager() {
        return mManager;
    }
}
