package se.rhel.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import se.rhel.model.FPSCamera;
import se.rhel.res.Resources;

/**
 * Group: Logic
 *
 * Created by Emil on 2014-02-27.
 * assigned to libgdx-gradle-template in se.rhel.view
 */
public class LaserRenderer {

    private DecalBatch mDecalBatch;
    private FPSCamera mCamera;

    private Decal mLaser = Decal.newDecal(1, 1, new TextureRegion(Resources.INSTANCE.laser), true);
    private Decal mLaserO = Decal.newDecal(1, 1, new TextureRegion(Resources.INSTANCE.laser_o), true);
    private Decal mLaserS = Decal.newDecal(1, 1, new TextureRegion(Resources.INSTANCE.laserStart), true);
    private Decal mLaserSO = Decal.newDecal(1, 1, new TextureRegion(Resources.INSTANCE.lasterStart_o), true);

    private float mLifeTime = 0f;
    private float totalTime = 10f;
    private float alpha = 1f;

    private Mesh testMesh;

    private Vector3 f, f2, t, t2;
    private Vector3 from, from2, to, to2;

    public LaserRenderer(FPSCamera cam) {
        mCamera = cam;
        mDecalBatch = new DecalBatch(new CameraGroupStrategy(mCamera));

        from = new Vector3();
        from2 = new Vector3();
        to = new Vector3();
        to2 = new Vector3();
    }

    public void draw(float delta) {
        /*
        mLifeTime += delta;
        alpha -= delta * 10f;

        if(alpha <= 0.1f) {
            alpha = 0f;
        }
        */

        if(testMesh != null) {
            Texture tex = Resources.INSTANCE.laser;

            // testMesh.render(new ShaderProgram(Gdx.files.internal("shader/celshading.vertex.glsl"), Gdx.files.internal("shader/celshading.fragment.glsl")), GL20.GL_TRIANGLE_STRIP, 0, 3);

            // testMesh.render(new ShaderProgram(DefaultShader.getDefaultVertexShader(), DefaultShader.getDefaultFragmentShader()), GL10.GL_TRIANGLES, 0, 3);

            ImmediateModeRenderer20 r = new ImmediateModeRenderer20(true, true, 1);


            r.begin(mCamera.combined, GL20.GL_TRIANGLE_STRIP);

            tex.bind();
            // 4
            r.color(1, 1, 1, 0);
            r.vertex(t2.x, t2.y, t2.z);
            r.texCoord(0, 0);
            // 3
            r.color(1, 1, 1, 0);
            r.vertex(f.x, f.y, f.z);
            r.texCoord(1, 1);
            // 2
            r.color(1, 1, 1, 0);
            r.vertex(t.x, t.y, t.z);
            r.texCoord(1, 0);
            // 1
            r.color(1, 1, 1, 0);
            r.vertex(f2.x, f2.y, f2.z);
            r.texCoord(0, 1);

            r.end();
        }

        mLaser.setColor(0, 1, 0, alpha);
        mLaserO.setColor(1, 1, 1, alpha);
        mLaserS.setColor(0, 1, 0, alpha);
        mLaserSO.setColor(1, 1, 1, alpha);

        mDecalBatch.add(mLaser);
        mDecalBatch.add(mLaserO);
        // mDecalBatch.add(mLaserS);
        // mDecalBatch.add(mLaserSO);
        mDecalBatch.flush();
    }

    public void shoot() {

        // Visual representation of the starting point bottom right
        Ray vis = mCamera.getPickRay(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        vis = vis.cpy();

        // bottom right + 20%
        Ray vis2 = mCamera.getPickRay(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * 0.8f);
        vis2 = vis2.cpy();

        // Middle + 20%
        Ray vis3 = mCamera.getPickRay(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() / 2));
        vis3 = vis3.cpy();

        // Middle
        Ray vis4 = mCamera.getPickRay(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() / 2) * 0.9f);
        vis4 = vis4.cpy();

        from.set(vis.origin);
        from2.set(vis2.origin);
        to.set(vis4.direction).scl(50f).add(from);
        to2.set(vis3.direction).scl(50f).add(from2);

        alpha = 1f;

        from = from.cpy();
        to = to.cpy();
        from2 = from2.cpy();
        to2 = to2.cpy();

        t=to;
        f=from;
        f2=from2;
        t2=to2;

        testMesh = createLaserMesh(from, to, from2, to2);

        // float dis = from.dst(to);

        // from.add(mCamera.direction.cpy().scl(1f));

        /*
        mLaserSO.setPosition(from.x, from.y, from.z);
        mLaserS.setPosition(from.x, from.y, from.z);

        //from.add(mCamera.direction.cpy().scl((dis / 2) + 0.5f));

        mLaser.setPosition(from.x, from.y, from.z);
        mLaserO.setPosition(from.x, from.y, from.z);

        mLaser.setRotation(mCamera.UP, mCamera.up);
        mLaser.rotateY(-45);
        mLaser.setScaleY(dis);

        mLaserO.setRotation(mCamera.UP, mCamera.up);
        mLaserO.rotateY(-45);
        mLaserO.setScaleY(dis);

        mLaserS.setRotation(mCamera.UP, mCamera.up);
        mLaserS.rotateY(-45);

        mLaserSO.setRotation(mCamera.UP, mCamera.up);
        mLaserSO.rotateY(-45);
        */
    }

    public Mesh createLaserMesh(Vector3 from, Vector3 to, Vector3 from2, Vector3 to2) {

        Mesh mesh = new Mesh(true, 4, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"),
                new VertexAttribute( VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0" ));

       /* mesh.setVertices(new float[] { from2.x, from2.y, from2.z, Color.toFloatBits(255, 0, 0, 255),
                to.x, to.y, to.z, Color.toFloatBits(0, 255, 0, 255),
                from.x, from.y, from.z, Color.toFloatBits(0, 0, 255, 255),
                to2.x, to2.y, to2.z, Color.toFloatBits(0, 0, 255, 255) });*/
       // mesh.setIndices(new short[] { 0, 1, 2 });
       return mesh;

        /*
        float[] verts = new float[15];
        int i = 0;

        verts[i++] = from.x; // x2
        verts[i++] = from.y; // y2
        verts[i++] = from.z;
        verts[i++] = 1f; // u2
        verts[i++] = 0f; // v2

        verts[i++] = to.x; // x1
        verts[i++] = to.y; // y1
        verts[i++] = to.z;
        verts[i++] = 0f; // u1
        verts[i++] = 0f; // v1

        verts[i++] = from2.x; // x3
        verts[i++] = from2.y; // y2
        verts[i++] = from2.z;
        verts[i++] = 1f; // u3
        verts[i++] = 1f; // v3


        verts[i++] = -1; // x4
        verts[i++] = 1f; // y4
        verts[i++] = 0;
        verts[i++] = 0f; // u4
        verts[i++] = 1f; // v4


        Mesh mesh = new Mesh( true, 3, 3,  // static mesh with 4 vertices and no indices
                new VertexAttribute( VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE ),
                new VertexAttribute( VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0" ) );

        mesh.setVertices( verts );
        return mesh;
        */

    }

}
