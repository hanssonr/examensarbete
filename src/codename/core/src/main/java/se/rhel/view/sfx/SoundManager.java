package se.rhel.view.sfx;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import se.rhel.res.Resources;

import java.util.HashMap;

/**
 * Created by Emil on 2014-03-21.
 */
public enum SoundManager {
    INSTANCE;

    public enum SoundType {
        LASER;
    }

    private Music theme = Resources.INSTANCE.theme;
    private Music menutheme = Resources.INSTANCE.menutheme;

    private static HashMap<SoundType, Sound> sounds = new HashMap<>();
    static {
        sounds.put(SoundType.LASER, Resources.INSTANCE.laserShot);
    }

    public void playMusic(boolean looping, float volume) {
        /*
        menutheme.stop();
        theme.stop();
        theme.setLooping(looping);
        theme.setVolume(volume);
        theme.play();
        */
    }

    public void playMenuMusic() {
        /*
        menutheme.stop();
        menutheme.setLooping(true);
        menutheme.setVolume(0.2f);
        menutheme.play();
        */
    }

    public void playSound(SoundType type) {
        sounds.get(type).play(1f);
    }
}
