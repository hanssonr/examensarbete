package se.rhel.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class WorldModel {

    private FPSCamera mCamera;
    private Player mPlayer;

    private ModelInstance mBoxInstance;
    private ModelBatch mModelBatch;
    private Environment mEnvironment;
    private CameraInputController mCamController;

    private Vector3 oldpos;



    public WorldModel() {

        mPlayer = new Player(new Vector3(0,0,5));
        mCamera = new FPSCamera(mPlayer, 67, 0.1f, 300f);
        mCamera.position.set(0f, 2f, 5f);
        mCamera.lookAt(0f, 0f, 0f);


        mModelBatch = new ModelBatch();
        mBoxInstance = new ModelInstance(Bodybuilder.INSTANCE.createBox(1f,1f,1f), 0f, 0f, 0f);

        oldpos = new Vector3();

        mEnvironment = new Environment();
        mEnvironment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
        mEnvironment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        mCamController = new CameraInputController(mCamera);
        Gdx.input.setInputProcessor(mCamController);
    }

    public void update(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        //mPlayer.update(delta);

        mCamController.update();
        mCamera.update();

        mBoxInstance.transform.rotate(new Quaternion().setEulerAngles(1, 0, 0));

        mModelBatch.begin(mCamera);
        mModelBatch.render(mBoxInstance, mEnvironment);
        mModelBatch.end();
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public FPSCamera getCamera() {
        return mCamera;
    }
}
