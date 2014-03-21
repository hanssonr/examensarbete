package se.rhel.view;

import com.badlogic.gdx.utils.Array;
import se.rhel.model.WorldModel;


import java.util.Iterator;

/**
 * Created by Emil on 2014-03-21.
 */
public class LaserView {

    private WorldModel mWorldModel;
    private Array<LaserMeshTestRenderer> mLasers;

    public LaserView(WorldModel wm) {
        mLasers = new Array<>();
        mWorldModel = wm;
    }

    public void add() {
        LaserMeshTestRenderer lmtr = new LaserMeshTestRenderer(mWorldModel.getCamera());
        mLasers.add(lmtr);
    }

    public void render(float delta) {
        Iterator itr = mLasers.iterator();
        while(itr.hasNext()) {
            LaserMeshTestRenderer lmtr = (LaserMeshTestRenderer) itr.next();
            if(!lmtr.render(delta)) {
                itr.remove();
            }
        }
    }
}
