package se.rhel.view;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.WorldModel;


import java.util.Iterator;

/**
 * Created by Emil on 2014-03-21.
 */
public class LaserView {

    private WorldModel mWorldModel;
    private Array<LaserMeshRenderer> mLasers;

    public LaserView(WorldModel wm) {
        mLasers = new Array<>();
        mWorldModel = wm;
    }

    public void add(Vector3[] verts) {
        LaserMeshRenderer lmr = new LaserMeshRenderer(mWorldModel.getCamera(), verts);
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
