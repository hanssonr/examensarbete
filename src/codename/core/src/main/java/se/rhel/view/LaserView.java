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
    private Array<Vector3[]> mToAdd;

    public LaserView(WorldModel wm) {
        mLasers = new Array<>();
        mToAdd = new Array<>();
        mWorldModel = wm;
    }

    public void add(Vector3[] verts) {
        mToAdd.add(verts);
    }

    public void render(float delta) {

        if(mToAdd.size != 0) {
            for(Vector3[] verts : mToAdd) {
                LaserMeshRenderer lmr = new LaserMeshRenderer(mWorldModel.getCamera(), verts);
                mLasers.add(lmr);
            }
        }
        mToAdd.clear();

        Iterator itr = mLasers.iterator();
        while(itr.hasNext()) {
            LaserMeshRenderer lmtr = (LaserMeshRenderer) itr.next();
            if(!lmtr.render(delta)) {
                itr.remove();
            }
        }
    }
}
