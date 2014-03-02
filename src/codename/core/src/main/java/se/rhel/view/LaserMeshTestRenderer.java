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

    public static final String VERT_SHADER =
            "attribute vec3 a_position;\n" +
                    "attribute vec4 a_color;\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "varying vec4 vColor;\n" +
                    "void main() {\n" +
                    "	vColor = a_color;\n" +
                    "	gl_Position =  u_projTrans * vec4(a_position.xyz, 1.0);\n" +
                    "}";

    public static final String FRAG_SHADER =
            "#ifdef GL_ES\n" +
                    "precision mediump float;\n" +
                    "#endif\n" +
                    "varying vec4 vColor;\n" +
                    "void main() {\n" +
                    "	gl_FragColor = vColor;\n" +
                    "}";

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
        mesh = new Mesh(false, MAX_VERTS, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.Color, COLOR_COMPONENTS, "a_color"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0" ));
        shader = createMeshShader();
        this.cam = cam;
    }

    public void render() {

        //this will push the triangles into the batch
        drawTriangle();

        //this will render the triangles to GL
        flush();
    }

    void flush() {
        //if we've already flushed
        if (idx==0)
            return;

        //sends our vertex data to the mesh
        mesh.setVertices(verts);

        //enable blending, for alpha
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        //number of vertices we need to render
        int vertexCount = (idx/NUM_COMPONENTS);

        //start the shader before setting any uniforms
        shader.begin();

        //update the projection matrix so our triangles are rendered in 2D
        shader.setUniformMatrix("u_projTrans", cam.combined);

        Resources.INSTANCE.laser.bind();
        //render the mesh
        mesh.render(shader, GL20.GL_TRIANGLE_STRIP, 0, 4);


        shader.end();

        //re-enable depth to reset states to their default
        Gdx.gl.glDepthMask(true);

        //reset index to zero
        idx = 0;
    }

    void drawTriangle() {
        //we don't want to hit any index out of bounds exception...
        //so we need to flush the batch if we can't store any more verts
        if (idx==verts.length)
            flush();

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

        Color color = Color.RED;

        //now we push the vertex data into our array
        //we are assuming (0, 0) is lower left, and Y is up
        //bottom right vertex


        //bottom left vertex
        verts[idx++] = to.x; 			//Position(x, y)
        verts[idx++] = to.y;
        verts[idx++] = to.z;
        verts[idx++] = color.r; 	//Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;
        verts[idx++] = 1;
        verts[idx++] = 0;

        verts[idx++] = to2.x;	 //Position(x, y)
        verts[idx++] = to2.y;
        verts[idx++] = to2.z;
        verts[idx++] = color.r;		 //Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;
        verts[idx++] = 0;
        verts[idx++] = 0;

        //top left vertex
        verts[idx++] = from2.x; 			//Position(x, y)
        verts[idx++] = from2.y;
        verts[idx++] = from2.z;
        verts[idx++] = color.r; 	//Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;
        verts[idx++] = 1;
        verts[idx++] = 1;

        verts[idx++] = from.x;	 //Position(x, y)
        verts[idx++] = from.y;
        verts[idx++] = from.z;
        verts[idx++] = color.r;		 //Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;
        verts[idx++] = 0;
        verts[idx++] = 1;

    }

}
