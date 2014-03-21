package se.rhel.view;

import com.badlogic.gdx.physics.bullet.collision.btPositionAndRadius;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.client.ClientWorldModel;

import java.util.Iterator;

/**
 * Created by Emil on 2014-03-21.
 */
public class LaserView {

    private ClientWorldModel mWorldModel;
    private Array<LaserMeshTestRenderer> mLasers;

    public LaserView(ClientWorldModel cwm) {
        mLasers = new Array<>();
        mWorldModel = cwm;
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
