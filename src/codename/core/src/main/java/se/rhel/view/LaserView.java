package se.rhel.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.decals.GroupStrategy;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.IWorldModel;
import se.rhel.model.physics.RayVector;
import se.rhel.res.Resources;


import java.util.Iterator;

/**
 * Created by Emil on 2014-03-21.
 */
public class LaserView {

    private Array<LaserMeshRenderer> mLasers;
    private Array<Vector3[]> mToAdd;
    private Array<RayVector> mRays;
    private Array<Decal> mRayDecals;
    private Array<LaserDecal> mLaserDecals;
    private FPSCamera mCamera;

    private DecalBatch mDecalBatch;

    public LaserView(FPSCamera camera) {
        mLasers = new Array<>();
        mToAdd = new Array<>();
        mRays = new Array<>();
        mRayDecals = new Array<>();
        mLaserDecals = new Array<>();
        mCamera = camera;

        mDecalBatch = new DecalBatch(new CameraGroupStrategy(camera));
    }

    public void add(Vector3[] verts) {
        mToAdd.add(verts);
    }

    public void add(RayVector ray) {
        mLaserDecals.add(new LaserDecal(ray));
    }

    public void render(float delta) {

        Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);

        for(LaserDecal ld : mLaserDecals) {
            ld.update(delta);
            if(ld.isAlive) {
                mDecalBatch.add(ld.getDecal());
                mDecalBatch.add(ld.getOtherDecal());
                mDecalBatch.flush();
            } else {
                mLaserDecals.removeValue(ld, true);
            }
        }

        Gdx.gl20.glDisable(GL20.GL_DEPTH_TEST);

//        if(mToAdd.size != 0) {
//            for(Vector3[] verts : mToAdd) {
//                LaserMeshRenderer lmr = new LaserMeshRenderer(mCamera, verts);
//                mLasers.add(lmr);
//            }
//        }
//        mToAdd.clear();
//
//        if(mRays.size != 0) {
//            for(RayVector ray : mRays) {
//                LaserMeshRenderer lmr = new LaserMeshRenderer(mCamera, ray);
//                mLasers.add(lmr);
//            }
//        }
//        mRays.clear();
//
//        Iterator itr = mLasers.iterator();
//        while(itr.hasNext()) {
//            LaserMeshRenderer lmtr = (LaserMeshRenderer) itr.next();
//            if(!lmtr.render(delta)) {
//                itr.remove();
//            }
//        }
    }

    private class LaserDecal {

        Decal mDecal;
        Decal mOtherDecal;
        Vector3 mPosition;
        RayVector mRay;
        float lifeTime;
        boolean isAlive = true;
        float speed = 100;
        float mWidth = 5f;

        public LaserDecal(RayVector ray) {
            mDecal = Decal.newDecal(mWidth, 0.1f, new TextureRegion(Resources.INSTANCE.laser), true);
            mOtherDecal = Decal.newDecal(mWidth, 0.1f, new TextureRegion(Resources.INSTANCE.laser), true);

            mDecal.setColor(1,0,0,1);
            mOtherDecal.setColor(1,0,0,1);

            Vector3 dir = ray.getDirection().crs(FPSCamera.UP);

            mDecal.setRotation(dir.cpy(), ray.getDirection().crs(dir.cpy()));
            mOtherDecal.setRotation(dir.cpy(), ray.getDirection().crs(dir.cpy()));
            mOtherDecal.rotateX(90f);

            Vector3 startPos = ray.getFrom();
            mDecal.setPosition(startPos.x, startPos.y, startPos.z);
            mOtherDecal.setPosition(startPos.x, startPos.y, startPos.z);
            mPosition = mDecal.getPosition().cpy();
            lifeTime = (float)RayVector.getDistance(ray.getTo(), ray.getFrom()) / speed;
            mRay = ray;
        }

        public Decal getDecal() {
            return mDecal;
        }

        public Decal getOtherDecal() {
            return mOtherDecal;
        }

        public void update(float delta) {
            mPosition.add(mRay.getDirection().cpy().scl(delta * speed));
            mDecal.setPosition(mPosition.x, mPosition.y, mPosition.z);
            mOtherDecal.setPosition(mPosition.x, mPosition.y, mPosition.z);

            lifeTime -= delta;
            if(lifeTime <= 0) {
                isAlive = false;
            }

        }
    }
}
