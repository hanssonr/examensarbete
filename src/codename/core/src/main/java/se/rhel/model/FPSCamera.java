package se.rhel.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import se.rhel.model.Entity.GameObject;


public class FPSCamera extends PerspectiveCamera {

    GameObject mObj;

    /**
     *
     * @param obj GameObject to follow
     * @param fov Field of View
     * @param near Nearplane
     * @param far Farplane
     */
    public FPSCamera(GameObject obj, float fov, float near, float far) {
        //super(fov, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.fieldOfView = fov;
        this.near = near;
        this.far = far;
        this.viewportWidth = Gdx.graphics.getWidth();
        this.viewportHeight = Gdx.graphics.getHeight();
        mObj = obj;

        //lookAt(new Vector3(0,0,-1));
        update();
    }

    @Override
    public void update() {
        super.update();
        //position.set(mObj.getPosition().cpy());
        //mObj.setRotation(new Vector3(0, direction.y, 0)); //rotate only around y axis
    }
}
