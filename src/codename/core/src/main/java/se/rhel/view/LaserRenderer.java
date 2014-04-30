package se.rhel.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.physics.RayVector;
import se.rhel.res.Resources;

public class LaserRenderer {

    private Array<LaserDecal> mLaserDecals;

    private DecalBatch mDecalBatch;

    public LaserRenderer(FPSCamera camera) {
        mLaserDecals = new Array<>();
        mDecalBatch = new DecalBatch(new CameraGroupStrategy(camera));
    }

    public void add(RayVector ray) {
        mLaserDecals.add(new LaserDecal(ray));
    }

    public void render(float delta) {

        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);

        for(LaserDecal ld : mLaserDecals) {
            ld.update(delta);
            if(ld.isAlive) {
                mDecalBatch.add(ld.getVerticalLaser());
                mDecalBatch.add(ld.getHorizontalLaser());
            } else {
                mLaserDecals.removeValue(ld, true);
            }

            mDecalBatch.flush();
        }

        Gdx.gl20.glDisable(GL20.GL_DEPTH_TEST);
    }

    private class LaserDecal {

        Decal mLaserVertical;
        Decal mLaserHorizontal;
        Vector3 mPosition;
        RayVector mRay;
        float lifeTime;
        boolean isAlive = true;
        float speed = 200;
        float mWidth = 7f;

        public LaserDecal(RayVector ray) {
            mLaserVertical = Decal.newDecal(mWidth, 0.1f, new TextureRegion(Resources.INSTANCE.laser), true);
            mLaserHorizontal = Decal.newDecal(mWidth, 0.1f, new TextureRegion(Resources.INSTANCE.laser), true);

            mLaserVertical.setColor(1, 0, 0, 1);
            mLaserHorizontal.setColor(1, 0, 0, 1);

            Vector3 dir = ray.getDirection().crs(FPSCamera.UP);

            mLaserVertical.setRotation(dir, ray.getDirection().crs(dir));
            mLaserHorizontal.setRotation(dir, ray.getDirection().crs(dir));
            mLaserHorizontal.rotateX(90f);

            mLaserVertical.setPosition(ray.getFrom().x, ray.getFrom().y, ray.getFrom().z);
            mLaserHorizontal.setPosition(ray.getFrom().x, ray.getFrom().y, ray.getFrom().z);

            mPosition = mLaserVertical.getPosition().cpy();
            lifeTime = (float)RayVector.getDistance(ray.getTo(), ray.getFrom()) / speed;
            mRay = ray;
        }

        public Decal getVerticalLaser() {
            return mLaserVertical;
        }

        public Decal getHorizontalLaser() {
            return mLaserHorizontal;
        }

        public void update(float delta) {
            mPosition.add(mRay.getDirection().scl(delta * speed));
            mLaserVertical.setPosition(mPosition.x, mPosition.y, mPosition.z);
            mLaserHorizontal.setPosition(mPosition.x, mPosition.y, mPosition.z);

            lifeTime -= delta;
            if(lifeTime <= 0) {
                isAlive = false;
            }

        }
    }
}
