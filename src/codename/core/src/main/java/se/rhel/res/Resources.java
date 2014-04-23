package se.rhel.res;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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

    public Model levelModelPhysics;
    public Model levelModelVisual;

    public Model playerModelAnimated;
    public Model grenadeModel;

    public Model laserWeaponModel;
    public Model space;

    public Texture bulletHole;
    public Texture teamtag_oc, teamtag_m;
    public Texture laser, laser_o;

    public ParticleEffect mBloodEffect;
    public ParticleEffect mExplosionEffect;

    public Music theme, menutheme;
    public Sound laserShot;

    public void load() {
        mManager.load("data/fonts/hud.fnt", BitmapFont.class);
        mManager.load("obj/level/level_xlarge.g3db", Model.class);
        mManager.load("obj/level/level_xlarge_vis.g3db", Model.class);
        mManager.load("obj/player/wep/LaserWeapon.g3db", Model.class);
        mManager.load("obj/player/animated/player_animated.g3db", Model.class);
        mManager.load("obj/player/wep/Grenade.g3db", Model.class);
        mManager.load("obj/skybox/spacesphere.obj", Model.class);

        mManager.load("tex/laser/laser_middle_b.png", Texture.class);
        mManager.load("tex/bullethole.png", Texture.class);

        mManager.load("tex/teamtag_oc.png", Texture.class);
        mManager.load("tex/teamtag_m.png", Texture.class);

        mManager.load("sfx/sound/lasershot.wav", Sound.class);
        mManager.load("sfx/music/menutheme.mp3", Music.class);
        mManager.load("sfx/music/theme.mp3", Music.class);

        mManager.load("tex/particle/blood.p", ParticleEffect.class);
        mManager.load("tex/particle/ptest.p", ParticleEffect.class);

        Bullet.init();
    }

    public void setInstances() {
        Array<Model> toSetup = new Array<>();

        hudFont = mManager.get("data/fonts/hud.fnt", BitmapFont.class);

        //Decals
        teamtag_oc = mManager.get("tex/teamtag_oc.png", Texture.class);
        teamtag_m = mManager.get("tex/teamtag_m.png", Texture.class);
        bulletHole = mManager.get("tex/bullethole.png", Texture.class);
        laser = mManager.get("tex/laser/laser_middle_b.png", Texture.class);

        //Sound & Audio
        menutheme = mManager.get("sfx/music/menutheme.mp3", Music.class);
        theme = mManager.get("sfx/music/theme.mp3", Music.class);
        laserShot = mManager.get("sfx/sound/lasershot.wav", Sound.class);

        //Player
        playerModelAnimated = mManager.get("obj/player/animated/player_animated.g3db", Model.class);

        //Level
        levelModelPhysics = mManager.get("obj/level/level_xlarge.g3db", Model.class);
        levelModelVisual = mManager.get("obj/level/level_xlarge_vis.g3db", Model.class);
        space = mManager.get("obj/skybox/spacesphere.obj", Model.class);

        //Weapon
        grenadeModel = mManager.get("obj/player/wep/Grenade.g3db", Model.class);
        laserWeaponModel = mManager.get("obj/player/wep/LaserWeapon.g3db", Model.class);

        //Particleeffects
        mBloodEffect = mManager.get("tex/particle/blood.p", ParticleEffect.class);
        mExplosionEffect = mManager.get("tex/particle/ptest.p", ParticleEffect.class);

        toSetup.add(playerModelAnimated);

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
