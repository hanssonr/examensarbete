package se.rhel.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.GdxRuntimeException;
import se.rhel.model.FPSCamera;
import se.rhel.res.Resources;

/**
 * Created by Emil on 2014-03-01.
 * assigned to libgdx-gradle-template in se.rhel.view
 */
public class LaserMeshTestRenderer {

    protected static ShaderProgram createMeshShader() {
        ShaderProgram.pedantic = false;
        // ShaderProgram shader = new ShaderProgram(VERT_SHADER, FRAG_SHADER);
        ShaderProgram shader = new ShaderProgram(Gdx.files.internal("shader/laser.vertex.glsl"), Gdx.files.internal("shader/laser.fragment.glsl"));
        String log = shader.getLog();
        if (!shader.isCompiled())
            throw new GdxRuntimeException(log);
        if (log!=null && log.length()!=0)
            System.out.println("Shader Log: "+log);
        return shader;
    }

    Mesh mesh;
    ShaderProgram shader;
    FPSCamera cam;

    //Position attribute - (x, y, z)
    public static final int POSITION_COMPONENTS = 3;

    //Color attribute - (r, g, b, a)
    public static final int COLOR_COMPONENTS = 4;

    public static final int UV_COMPONENTS = 2;

    //Total number of components for all attributes
    public static final int NUM_COMPONENTS = POSITION_COMPONENTS + COLOR_COMPONENTS + UV_COMPONENTS;

    //The maximum number of triangles our mesh will hold
    public static final int MAX_TRIS = 2;

    //The maximum number of vertices our mesh will hold
    public static final int MAX_VERTS = MAX_TRIS * 3;

    //The array which holds all the data, interleaved like so:
    //    x, y, r, g, b, a
    //    x, y, r, g, b, a,
    //    x, y, r, g, b, a,
    //    ... etc ...
    private float[] verts = new float[MAX_VERTS * NUM_COMPONENTS];

    //The index position
    private int idx = 0;

    public LaserMeshTestRenderer(FPSCamera cam) {
        mesh = new Mesh(false, 6, 6,
                new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.Color, COLOR_COMPONENTS, "a_color"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0" ));
        mesh.setIndices(new short[]{0, 1, 2, 2, 3, 0});
        shader = createMeshShader();
        this.cam = cam;
    }

    public void render() {

        //this will push the triangles into the batch
        drawTriangle(Color.RED);
        //this will render the triangles to GL
        flush(Resources.INSTANCE.laser);

        drawTriangle(Color.WHITE);
        flush(Resources.INSTANCE.laser_o);
    }

    void flush(Texture tex) {
        //if we've already flushed
        if (idx==0)
            return;

        //sends our vertex data to the mesh
        mesh.setVertices(verts);

        //enable blending, for alpha
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glEnable(GL20.GL_TEXTURE);

        //number of vertices we need to render
        int vertexCount = (idx/NUM_COMPONENTS);
        tex.bind();
        //start the shader before setting any uniforms
        shader.begin();

        //update the projection matrix so our triangles are rendered in 2D
        shader.setUniformMatrix("u_projTrans", cam.combined);

        //render the mesh
        mesh.render(shader, GL20.GL_TRIANGLE_STRIP, 0, 4);

        shader.end();


        //reset index to zero
        idx = 0;
    }

    void drawTriangle(Color color) {
        //we don't want to hit any index out of bounds exception...
        //so we need to flush the batch if we can't store any more verts
        if (idx==verts.length)
            flush(Resources.INSTANCE.laser);

        // Visual representation of the starting point bottom right
        Ray vis = cam.getPickRay(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        vis = vis.cpy();

        // bottom right + 20%
        Ray vis2 = cam.getPickRay(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() * 0.8f);
        vis2 = vis2.cpy();

        // Middle + 20%
        Ray vis3 = cam.getPickRay(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() / 2));
        vis3 = vis3.cpy();

        // Middle
        Ray vis4 = cam.getPickRay(Gdx.graphics.getWidth() / 2, (Gdx.graphics.getHeight() / 2) * 0.9f);
        vis4 = vis4.cpy();

        Vector3 from = new Vector3();
        Vector3 from2 = new Vector3();
        Vector3 to = new Vector3();
        Vector3 to2 = new Vector3();

        from.set(vis.origin);
        from2.set(vis2.origin);
        to.set(vis4.direction).scl(50f).add(from);
        to2.set(vis3.direction).scl(50f).add(from2);

        from.add(cam.direction.cpy().scl(0.2f));
        from2.add(cam.direction.cpy().scl(0.2f));

        //now we push the vertex data into our array
        //we are assuming (0, 0) is lower left, and Y is up
        //bottom right vertex


        // 4
        verts[idx++] = to2.x;
        verts[idx++] = to2.y;
        verts[idx++] = to2.z;
        verts[idx++] = color.r;
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;
        verts[idx++] = 1;
        verts[idx++] = 1;

        // 2
        verts[idx++] = from.x;
        verts[idx++] = from.y;
        verts[idx++] = from.z;
        verts[idx++] = color.r;
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;
        verts[idx++] = 1;
        verts[idx++] = 0;

        // 3
        verts[idx++] = from2.x;
        verts[idx++] = from2.y;
        verts[idx++] = from2.z;
        verts[idx++] = color.r;
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;
        verts[idx++] = 0;
        verts[idx++] = 0;

        //1
        verts[idx++] = to.x;
        verts[idx++] = to.y;
        verts[idx++] = to.z;
        verts[idx++] = color.r;
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;
        verts[idx++] = 0;
        verts[idx++] = 1;

        /*
        11 11 11 11 11 11
        01 01 00 00 10 10
        10 00 01 10 00 01
        00 10 10 01 01 00

        00 00 00 00 00 00
        11 11 10 10 01 01
        01 10 11 01 10 11
        10 01 01 11 11 10

        10 10 10 10 10 10
        00 00 01 01 11 11
        11 01 00 11 01 00
        01 11 11 00 00 01

        01 01 01 01 01 01
        10 10 11 11 00 00
        00 11 10 00 11 10
        11 00 00 10 10 11
         */

    }

}
