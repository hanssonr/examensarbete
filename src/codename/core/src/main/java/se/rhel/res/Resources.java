package se.rhel.res;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Array;

/**
 * Group: Logic
 *
 * Created by Emil on 2014-02-18.
 * assigned to libgdx-gradle-template in se.rhel.res
 */
public enum Resources {
    INSTANCE;

    private static AssetManager mManager;
    static { mManager = new AssetManager(); }


    public BitmapFont hudFont;
    public Array<ModelInstance> modelInstanceArray;

    public Model levelModelPhysics;
    public Model levelModelVisual;

    public Model mcPlayerModel;
    public Model firstPersonWeaponModel;
    public Model playerModelAnimated;
    public Model fpsWeaponModel;
    public Model laserModel;

    public ModelInstance playerModelInstanceAnimated;

    public Texture bulletHole;
    public Texture hole;
    public Texture teamtag_oc, teamtag_m;
    public Texture laser, laser_o, laser_a, laserStart, lasterStart_o;

    public Music theme, menutheme;
    public Sound laserShot;

    public void load() {
        mManager.load("data/fonts/hud.fnt", BitmapFont.class);
        //mManager.load("obj/ship/ship.obj", Model.class);
        mManager.load("obj/level/level_xlarge.g3db", Model.class);
        mManager.load("obj/level/level_xlarge_vis.g3db", Model.class);
        //mManager.load("obj/player/player.g3db", Model.class);
        mManager.load("obj/player/wep/FirstPersonWeapon.g3db", Model.class);
        //mManager.load("obj/laser.g3db", Model.class);
        mManager.load("obj/player/animated/MyMinecraftPlayer_animated_idle.g3db", Model.class);
        //mManager.load("obj/player/mcplayer.g3db", Model.class);
        //mManager.load("obj/beretta/beretta.obj", Model.class);
        mManager.load("obj/skybox/spacesphere.obj", Model.class);

        mManager.load("tex/laser/laser_middle_b.png", Texture.class);
        mManager.load("tex/laser/laser_middle_o.png", Texture.class);
        mManager.load("tex/laser/laser_animation_noise.png", Texture.class);
        mManager.load("tex/laser/laser_start_b.png", Texture.class);
        mManager.load("tex/laser/laser_start_o.png", Texture.class);
        mManager.load("tex/bullethole.png", Texture.class);
        mManager.load("tex/hole.png", Texture.class);

        mManager.load("tex/teamtag_oc.png", Texture.class);
        mManager.load("tex/teamtag_m.png", Texture.class);

        mManager.load("obj/player/wep/FPSWeapon.g3db", Model.class);

        mManager.load("sfx/sound/lasershot.wav", Sound.class);
        mManager.load("sfx/music/menutheme.mp3", Music.class);
        mManager.load("sfx/music/theme.mp3", Music.class);
        // mManager.load("obj/level/testbox.obj", Model.class);

        Bullet.init();
    }

    public void setInstances() {
        modelInstanceArray = new Array<>();
        Array<Model> toSetup = new Array<>();

        hudFont = mManager.get("data/fonts/hud.fnt", BitmapFont.class);
        bulletHole = mManager.get("tex/bullethole.png", Texture.class);
        hole = mManager.get("tex/hole.png", Texture.class);
        laser = mManager.get("tex/laser/laser_middle_b.png", Texture.class);
        laser_o = mManager.get("tex/laser/laser_middle_o.png", Texture.class);
        laser_a = mManager.get("tex/laser/laser_animation_noise.png", Texture.class);
        laserStart = mManager.get("tex/laser/laser_start_b.png", Texture.class);
        lasterStart_o = mManager.get("tex/laser/laser_start_o.png", Texture.class);

        teamtag_oc = mManager.get("tex/teamtag_oc.png", Texture.class);
        teamtag_m = mManager.get("tex/teamtag_m.png", Texture.class);

        menutheme = mManager.get("sfx/music/menutheme.mp3", Music.class);
        theme = mManager.get("sfx/music/theme.mp3", Music.class);
        laserShot = mManager.get("sfx/sound/lasershot.wav", Sound.class);

        firstPersonWeaponModel = mManager.get("obj/player/wep/FirstPersonWeapon.g3db", Model.class);
        //laserModel = mManager.get("obj/laser.g3db", Model.class);
        ModelInstance instance = new ModelInstance(firstPersonWeaponModel, 5f, 0.1f, -15f);
        modelInstanceArray.add(instance);

        Model space = mManager.get("obj/skybox/spacesphere.obj", Model.class);
        instance = new ModelInstance(space);
        modelInstanceArray.add(instance);

        playerModelAnimated = mManager.get("obj/player/animated/MyMinecraftPlayer_animated_idle.g3db", Model.class);
        setupMaterial(playerModelAnimated);
        playerModelInstanceAnimated = new ModelInstance(playerModelAnimated, 10f, 0.1f, -15f);

        levelModelPhysics = mManager.get("obj/level/level_xlarge.g3db", Model.class);
        levelModelVisual = mManager.get("obj/level/level_xlarge_vis.g3db", Model.class);

        fpsWeaponModel = mManager.get("obj/player/wep/FPSWeapon.g3db", Model.class);

        //toSetup.add(levelModel);
        toSetup.add(fpsWeaponModel);
        toSetup.add(playerModelAnimated);
        //mcPlayerModel = mManager.get("obj/player/mcplayer.g3db", Model.class);

        for(Model m : toSetup)
            setupMaterial(m);
    }

    public AssetManager getAssetManager() {
        return mManager;
    }
    
    private void setupMaterial(Model model) {
        for(Material m : model.materials) {
            TextureAttribute atr = (TextureAttribute)m.get(TextureAttribute.Diffuse);
            if(atr != null) {
                atr.textureDescription.uWrap = Texture.TextureWrap.Repeat;
                atr.textureDescription.vWrap = Texture.TextureWrap.Repeat;
                atr.textureDescription.minFilter = Texture.TextureFilter.Nearest;
                atr.textureDescription.magFilter = Texture.TextureFilter.Nearest;
            }
        }
    }
}
