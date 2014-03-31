package se.rhel.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.IWorldModel;

/**
 * Created by Emil on 2014-03-25.
 */
public class ParticleRenderer extends A2DView {

    private FPSCamera mCamera;

    private ParticleEffectPool mBloodEffectPool;
    private Array<MyPooledEffect> mBloodEffects = new Array();
    private ParticleEffect mBloodEffect = new ParticleEffect();

    public class MyPooledEffect  {
        public Vector3 pos;
        public ParticleEffectPool.PooledEffect effect;

        public MyPooledEffect(Vector3 pos, ParticleEffectPool.PooledEffect effect) {
            this.pos = pos;
            this.effect = effect;
        }
    }

    public ParticleRenderer(IWorldModel model, SpriteBatch batch, FPSCamera cam) {
        super(model, batch);
        mCamera = cam;

        mBloodEffect.load(Gdx.files.internal("tex/particle/test.p"), Gdx.files.internal("tex/particle"));
        mBloodEffectPool = new ParticleEffectPool(mBloodEffect, 1, 20);

        addEffect(new Vector3(0f, 2f, 0f));
        addEffect(new Vector3(10f, 2f, 0f));
        addEffect(new Vector3(20f, 2f, 0f));
    }

    public void addEffect(Vector3 atPos) {
        ParticleEffectPool.PooledEffect effect = mBloodEffectPool.obtain();
        mBloodEffects.add(new MyPooledEffect(atPos, effect));
    }

    @Override
    public void draw(float delta) {

        for (int i = mBloodEffects.size - 1; i >= 0; i--) {
            smSpriteBatch.setProjectionMatrix(mCamera.projection);
            ParticleEffectPool.PooledEffect effect = mBloodEffects.get(i).effect;

            smSpriteBatch.setTransformMatrix(mCamera.view);
            smSpriteBatch.getTransformMatrix().translate(mBloodEffects.get(i).pos.x, mBloodEffects.get(i).pos.y, mBloodEffects.get(i).pos.z);
            Quaternion q = new Quaternion();
            mCamera.view.getRotation(q);
            q.conjugate();
            smSpriteBatch.getTransformMatrix().rotate(q);
            smSpriteBatch.getTransformMatrix().scale(0.01f, 0.01f, 0.01f);
            smSpriteBatch.begin();

            effect.draw(smSpriteBatch, delta);
            if (effect.isComplete()) {
                effect.reset();
                effect.free();
                mBloodEffects.removeIndex(i);
            }

            smSpriteBatch.end();
            smSpriteBatch.getTransformMatrix().idt();
        }

    }

    @Override
    public void dispose() {

    }
}
