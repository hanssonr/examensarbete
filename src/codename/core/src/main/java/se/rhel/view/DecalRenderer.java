package se.rhel.view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.FPSCamera;
import se.rhel.res.Resources;

/**
 * Created by Emil on 2014-02-21.
 * assigned to libgdx-gradle-template in se.rhel.view
 */
public class DecalRenderer {

    private DecalBatch mDecalBatch;
    private static FPSCamera mCamera;
    private static Array<Decal> mDecals;

    public DecalRenderer(FPSCamera camera) {
        mCamera = camera;
        mDecalBatch = new DecalBatch(new CameraGroupStrategy(camera));
        mDecals = new Array<>();

        addDecalAt(new Vector3(0f, 0f, 0f), mCamera.up);
    }

    public void draw(float delta) {

        for (Decal decal : mDecals) {
            decal.lookAt(mCamera.position, mCamera.up);
            mDecalBatch.add(decal);
        }
        mDecalBatch.flush();
    }

    public static void addDecalAt(Vector3 pos, Vector3 normal) {
        Decal toAdd = Decal.newDecal(1f, 1f, new TextureRegion(Resources.INSTANCE.bulletHole), true);
        toAdd.setPosition(pos.x, pos.y, pos.z);
        toAdd.lookAt(normal, mCamera.up);
        mDecals.add(toAdd);
    }

    public void dispose() {
        mDecalBatch.dispose();
    }
}
