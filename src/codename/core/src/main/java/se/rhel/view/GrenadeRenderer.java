package se.rhel.view;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import se.rhel.model.weapon.Grenade;
import se.rhel.res.Resources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by rkh on 2014-03-31.
 */
public class GrenadeRenderer {

    volatile HashMap<Grenade, ModelInstance> mGrenades = new HashMap<>();

    public GrenadeRenderer() {
    }

    public void render(ModelBatch batch, Environment env) {
        Iterator it = mGrenades.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry<Grenade, ModelInstance> pairs = (Map.Entry<Grenade, ModelInstance>) it.next();

            Grenade g = pairs.getKey();

            if(g.isAlive()) {
                batch.render(mGrenades.get(g), env);
            }
        }
    }

    public void update(float delta) {
        Iterator it = mGrenades.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry<Grenade, ModelInstance> pairs = (Map.Entry<Grenade, ModelInstance>) it.next();

            Grenade g = pairs.getKey();
            ModelInstance i = pairs.getValue();

            i.transform.set(g.getTransformation());

            if(!g.isAlive())
                it.remove();
        }
    }

    public void addGrenade(Grenade grenade) {
        mGrenades.put(grenade, new ModelInstance(Resources.INSTANCE.grenadeModel, grenade.getTransformation()));
    }


}
