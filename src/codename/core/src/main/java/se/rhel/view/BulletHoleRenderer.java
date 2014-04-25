package se.rhel.view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.res.Resources;

/**
 * Group: Logic
 *
 * Created by Emil on 2014-02-21.
 * assigned to libgdx-gradle-template in se.rhel.view
 */
public class BulletHoleRenderer {

    private DecalBatch mDecalBatch;
    private static FPSCamera mCamera;

    private Array<Decal> mBulletholes;
    private int MAX_BULLET_DECALS = 1000;
    private int CURRENT = 0;

    public BulletHoleRenderer(FPSCamera camera) {
        mCamera = camera;
        mDecalBatch = new DecalBatch(new CameraGroupStrategy(camera));
        mBulletholes = new Array<>(MAX_BULLET_DECALS);
        createBulletholes();
    }

    private void createBulletholes() {
        for (int i = 0; i < MAX_BULLET_DECALS; i++) {
            Decal toAdd = Decal.newDecal(0.4f, 0.4f, new TextureRegion(Resources.INSTANCE.bulletHole), true);
            toAdd.setPosition(0,-9999,0);
            mBulletholes.add(toAdd);
        }
    }

    public void draw(float delta) {
        mDecalBatch.setGroupStrategy(new CameraGroupStrategy(mCamera));
        for (Decal bullethole : mBulletholes) {
            mDecalBatch.add(bullethole);
        }
        mDecalBatch.flush();
    }

    public void addBullethole(Vector3 pos, Vector3 normal) {
        CURRENT = CURRENT++ >= MAX_BULLET_DECALS-1 ? 0 : CURRENT;
        Decal bh = mBulletholes.get(CURRENT);
        bh.setPosition(pos.x + (normal.x * 0.001f), pos.y + (normal.y * 0.001f), pos.z + (normal.z * 0.001f));
        bh.setRotation(normal, mCamera.up);
    }

    public void dispose() {
        mDecalBatch.dispose();
    }
}
