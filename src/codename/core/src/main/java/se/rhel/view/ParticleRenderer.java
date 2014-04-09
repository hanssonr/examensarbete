package se.rhel.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.IWorldModel;
import se.rhel.res.Resources;
import se.rhel.view.sfx.SoundManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Group: Logic
 * Created by Emil on 2014-03-25.
 */
public class ParticleRenderer extends A2DView {

    private FPSCamera mCamera;
    private Map<Particle, ParticleEffectPool> mEffectMap = new HashMap<>();
    private Array<MyPooledEffect> mEffects = new Array();

    public ParticleRenderer(IWorldModel model, SpriteBatch batch, FPSCamera cam) {
        super(model, batch);
        mCamera = cam;

        mEffectMap.put(Particle.BLOOD, new ParticleEffectPool(Resources.INSTANCE.mBloodEffect, 1, 20));
        mEffectMap.put(Particle.EXPLOSION, new ParticleEffectPool(Resources.INSTANCE.mExplosionEffect, 1, 20));
    }

    public void addEffect(Vector3 atPos, Particle type) {
        if(type == Particle.EXPLOSION)
            SoundManager.INSTANCE.playSound(SoundManager.SoundType.GRENADE);

        ParticleEffectPool.PooledEffect effect = mEffectMap.get(type).obtain();
        mEffects.add(new MyPooledEffect(atPos, effect, type));
    }

    @Override
    public void draw(float delta) {

        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);

        for (int i = mEffects.size - 1; i >= 0; i--) {
            smSpriteBatch.setProjectionMatrix(mCamera.projection);

            MyPooledEffect mpe = mEffects.get(i);
            ParticleEffectPool.PooledEffect effect = mEffects.get(i).effect;

            smSpriteBatch.setTransformMatrix(mCamera.view);
            smSpriteBatch.getTransformMatrix().translate(mEffects.get(i).pos);
            Quaternion q = new Quaternion();
            mCamera.view.getRotation(q);
            q.conjugate();
            smSpriteBatch.getTransformMatrix().rotate(q);

            //TODO: Fixa detta snyggare!!!
            if(mpe.type == Particle.BLOOD) {
                smSpriteBatch.getTransformMatrix().scale(0.01f, 0.01f, 0.01f);
            } else if (mpe.type == Particle.EXPLOSION) {
                smSpriteBatch.getTransformMatrix().scale(0.2f, 0.2f, 0.2f);
            }

            smSpriteBatch.begin();

            effect.draw(smSpriteBatch, delta);
            if (effect.isComplete()) {
                effect.reset();
                effect.free();
                mEffects.removeIndex(i);
            }

            smSpriteBatch.end();
            smSpriteBatch.getTransformMatrix().idt();
        }

        Gdx.gl20.glDisable(GL20.GL_DEPTH_TEST);

    }

    @Override
    public void dispose() {

    }

    // Particle types
    public static enum Particle {
        BLOOD, EXPLOSION
    }

    // Helpclass
    public class MyPooledEffect  {
        public Vector3 pos = new Vector3();
        public ParticleEffectPool.PooledEffect effect;
        public Particle type;

        public MyPooledEffect(Vector3 pos, ParticleEffectPool.PooledEffect effect, Particle type) {
            this.pos.set(pos);
            this.effect = effect;
            this.type = type;
        }
    }
}
