
package se.rhel.gradletest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;


public class HelloApp extends ApplicationAdapter {

    private PerspectiveCamera mCamera;
    private ModelBatch mModelBatch;
    private Model mBox;
    private ModelInstance mBoxInstance;
    private Environment mEnvironment;
    private CameraInputController mCamController;
	
	@Override
	public void create () {

        // Create camera sized to screens width/height with a FieldOfView of 75
        mCamera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Move the camera 3 units back along the z-axis and look at the origin
        mCamera.position.set(0f, 0f, 3f);
        mCamera.lookAt(0f, 0f, 0f);

        // Near and far plane represents the min and max ranges of the camera
        mCamera.near = 0.1f;
        mCamera.far = 300f;

        // A ModelBatch is like a SpriteBatch for models. Use it to batch up geometry for OpenGL
        mModelBatch = new ModelBatch();

        // A ModelBuilder can be used to build meshes by hand
        ModelBuilder modelBuilder = new ModelBuilder();

        // There is alse the handy ability to make certain premade shapes, like a cube
        // we pass in a ColorAttribute, making the cube diffuse
        // and let OpenGL know we are interested in the position and normal channels
        mBox = modelBuilder.createBox(2f, 2f, 2f, new Material(ColorAttribute.createDiffuse(Color.BLUE)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        // A model holds all of the information about a model, such as vertex data and texture info
        // however, you need an instace to actually render it. The instance contains all the positioning info
        // and more. Remember Model==heavy ModelInstace==Light
        mBoxInstance = new ModelInstance(mBox, 0f, 0f, 0f);

        // Finally we want some light, or we wont see our color. The environment gets passed in during
        // the rendering process. Create one, then create an Ambient light
        mEnvironment = new Environment();
        mEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
        mEnvironment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        mCamController = new CameraInputController(mCamera);
        Gdx.input.setInputProcessor(mCamController);
	}

    @Override
    public void dispose() {
        mModelBatch.dispose();
        mBox.dispose();
    }

	@Override
	public void render () {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        mCamController.update();

        // For some flavor, lets spin the camera around the Y axis
        //mCamera.rotateAround(Vector3.Zero, new Vector3(0f , 1f, 0f), 1f);
        //mCamera.update();
        mBoxInstance.transform.rotate(new Quaternion().setEulerAngles(1,0,0));


        // Like spriteBatch, but with models, hooray
        mModelBatch.begin(mCamera);
        mModelBatch.render(mBoxInstance, mEnvironment);
        mModelBatch.end();
	}
}
