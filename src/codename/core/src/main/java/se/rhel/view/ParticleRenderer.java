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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emil on 2014-03-25.
 */
public class ParticleRenderer extends A2DView {

    public static enum Particle {
        BLOOD, EXPLOSION
    }

    private FPSCamera mCamera;

    private Map<Particle, ParticleEffectPool> effects = new HashMap<>();

    //private ParticleEffectPool mEffectPool;
    private Array<MyPooledEffect> mEffects = new Array();

    private ParticleEffect mBloodEffect = new ParticleEffect();
    private ParticleEffect mExplosionEffect = new ParticleEffect();

    public class MyPooledEffect  {
        public Vector3 pos;
        public ParticleEffectPool.PooledEffect effect;
        public Particle type;

        public MyPooledEffect(Vector3 pos, ParticleEffectPool.PooledEffect effect, Particle type) {
            this.pos = pos;
            this.effect = effect;
            this.type = type;
        }
    }

    public ParticleRenderer(IWorldModel model, SpriteBatch batch, FPSCamera cam) {
        super(model, batch);
        mCamera = cam;

        mBloodEffect.load(Gdx.files.internal("tex/particle/blood.p"), Gdx.files.internal("tex/particle"));
        mExplosionEffect.load(Gdx.files.internal("tex/particle/ptest.p"), Gdx.files.internal("tex/particle"));

        effects.put(Particle.BLOOD, new ParticleEffectPool(mBloodEffect, 1, 20));
        effects.put(Particle.EXPLOSION, new ParticleEffectPool(mExplosionEffect, 1, 20));
    }

    public void addEffect(Vector3 atPos, Particle type) {
        ParticleEffectPool.PooledEffect effect = effects.get(type).obtain();
        mEffects.add(new MyPooledEffect(atPos.cpy(), effect, type));
    }

    @Override
    public void draw(float delta) {

        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);

        for (int i = mEffects.size - 1; i >= 0; i--) {
            smSpriteBatch.setProjectionMatrix(mCamera.projection);
            MyPooledEffect mpe = mEffects.get(i);
            ParticleEffectPool.PooledEffect effect = mEffects.get(i).effect;

            smSpriteBatch.setTransformMatrix(mCamera.view);
            smSpriteBatch.getTransformMatrix().translate(mEffects.get(i).pos.x, mEffects.get(i).pos.y, mEffects.get(i).pos.z);
            Quaternion q = new Quaternion();
            mCamera.view.getRotation(q);
            q.conjugate();
            smSpriteBatch.getTransformMatrix().rotate(q);

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
}
