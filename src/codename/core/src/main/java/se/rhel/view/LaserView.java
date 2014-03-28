package se.rhel.view;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.BaseWorldModel;
import se.rhel.model.FPSCamera;
import se.rhel.model.IWorldModel;


import java.util.Iterator;

/**
 * Created by Emil on 2014-03-21.
 */
public class LaserView {

    private IWorldModel mWorldModel;
    private Array<LaserMeshRenderer> mLasers;
    private FPSCamera mCamera;

    public LaserView(IWorldModel wm, FPSCamera camera) {
        mLasers = new Array<>();
        mWorldModel = wm;
        mCamera = camera;
    }

    public void add(Vector3[] verts) {
        LaserMeshRenderer lmr = new LaserMeshRenderer(mCamera, verts);
        mLasers.add(lmr);
    }

    public void render(float delta) {
        Iterator itr = mLasers.iterator();
        while(itr.hasNext()) {
            LaserMeshRenderer lmtr = (LaserMeshRenderer) itr.next();
            if(!lmtr.render(delta)) {
                itr.remove();
            }
        }
    }
}
