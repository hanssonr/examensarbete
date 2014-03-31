package se.rhel.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import se.rhel.res.Resources;

/**
 * Created by Emil on 2014-03-24.
 */
public class DecalRenderer {

    private DecalBatch mDecalBatch;
    private Decal mDecal_team_m, mDecal_team_oc;
    private FPSCamera mCamera;

    public DecalRenderer(FPSCamera cam) {
        mCamera = cam;
        mDecalBatch = new DecalBatch(new CameraGroupStrategy(mCamera));

        mDecal_team_m = Decal.newDecal(2f, 0.5f, new TextureRegion(Resources.INSTANCE.teamtag_m));
        mDecal_team_oc = Decal.newDecal(2f, 0.5f, new TextureRegion(Resources.INSTANCE.teamtag_oc));
    }

    public void draw(float delta, Vector3 pos) {
        Gdx.gl.glEnable(GL20.GL_BLEND);

        mDecal_team_m.setPosition(pos.x, pos.y + 2f, pos.z);
        mDecal_team_m.lookAt(mCamera.position, FPSCamera.UP);
        mDecalBatch.add(mDecal_team_m);
        mDecalBatch.flush();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
