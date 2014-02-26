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
    public Model mcPlayerModel;
    public Model firstPersonWeaponModel;
    public Model playerModelAnimated;
    public Model fpsWeaponModel;

    public ModelInstance playerModelInstanceAnimated;

    public Texture bulletHole;
    public Texture hole;

    public void load() {
        mManager.load("data/fonts/hud.fnt", BitmapFont.class);
        mManager.load("obj/ship/ship.obj", Model.class);
        mManager.load("obj/level/level_xlarge.g3db", Model.class);
        mManager.load("obj/player/player.g3db", Model.class);
        mManager.load("obj/player/wep/FirstPersonWeapon.g3db", Model.class);
        mManager.load("obj/player/animated/MyMinecraftPlayer_animated.g3db", Model.class);
        mManager.load("obj/player/mcplayer.g3db", Model.class);
        mManager.load("obj/beretta/beretta.obj", Model.class);
        mManager.load("obj/skybox/spacesphere.obj", Model.class);
        mManager.load("tex/bullethole.png", Texture.class);
        mManager.load("tex/hole.png", Texture.class);

        mManager.load("obj/player/wep/FPSWeapon.obj", Model.class);

        Bullet.init();
    }

    public void setInstances() {
        modelInstanceArray = new Array<>();

        hudFont = mManager.get("data/fonts/hud.fnt", BitmapFont.class);

        bulletHole = mManager.get("tex/bullethole.png", Texture.class);
        hole = mManager.get("tex/hole.png", Texture.class);

        Model shipModel = mManager.get("obj/ship/ship.obj", Model.class);
        ModelInstance instance = new ModelInstance(shipModel, 0,1,0);
        modelInstanceArray.add(instance);

        // Model space = mManager.get("obj/skybox/spacesphere.obj", Model.class);
        // instance = new ModelInstance(space);
        // modelInstanceArray.add(instance);

        firstPersonWeaponModel = mManager.get("obj/player/wep/FirstPersonWeapon.g3db", Model.class);
        instance = new ModelInstance(firstPersonWeaponModel, 5f, 0.1f, -15f);
        modelInstanceArray.add(instance);

        fpsWeaponModel = mManager.get("obj/player/wep/FPSWeapon.obj", Model.class);

        playerModelAnimated = mManager.get("obj/player/animated/MyMinecraftPlayer_animated.g3db", Model.class);
        playerModelInstanceAnimated = new ModelInstance(playerModelAnimated, -19f, 0.1f, 24f);
        // modelInstanceArray.add(instance);

        levelModel = mManager.get("obj/level/level_xlarge.g3db", Model.class);
        mcPlayerModel = mManager.get("obj/player/mcplayer.g3db", Model.class);
    }

    public AssetManager getAssetManager() {
        return mManager;
    }
}
