package se.rhel.view;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.entity.IEntity;
import se.rhel.res.Resources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by rkh on 2014-03-28.
 */
public class ExternalPlayerRenderer {

    public static final String ANIMATION_IDLE = "idle";
    public static final String ANIMATION_WALK = "walk";

    //Instance
    private ModelInstance mExternalPlayer = new ModelInstance(Resources.INSTANCE.playerModelAnimated);
    private Vector3 mGroundOffset = new Vector3();
    private BoundingBox mBox = new BoundingBox();
    private Array<IEntity> mPlayers;

    //Animation
    private HashMap<IEntity, AnimationController> mAnimations = new HashMap<>();
    private Vector3 newPos = new Vector3();
    private Vector3 oldPos = new Vector3();

    public ExternalPlayerRenderer(Array<IEntity> players) {
        mExternalPlayer.calculateBoundingBox(mBox);
        mGroundOffset.set(0, -mBox.getDimensions().y / 2.0f, 0);
        mPlayers = players;

        for (int i = 0; i < mPlayers.size; i++) {
            addPlayerAnimation(mPlayers.get(i));
        }
    }

    public void addPlayerAnimation(IEntity entity) {
        AnimationController ac = new AnimationController(new ModelInstance(Resources.INSTANCE.playerModelAnimated));
        ac.setAnimation(ANIMATION_IDLE);

        mAnimations.put(entity, ac);
    }

    public void render(ModelBatch batch, Environment env) {
        for(int i = 0; i < mPlayers.size; i++) {
            IEntity entity = mPlayers.get(i);

            if(entity.isAlive()) {
                batch.render(mAnimations.get(entity).target, env);
            }
        }
    }

    public void update(float delta) {

        Iterator it = mAnimations.entrySet().iterator();
        while(it.hasNext()) {

            Map.Entry<IEntity, AnimationController> pairs = (Map.Entry)it.next();
            IEntity entity = pairs.getKey();
            AnimationController ac = pairs.getValue();

            ac.target.transform.getTranslation(oldPos);

            ac.target.transform.set(entity.getTransformation());
            ac.target.transform.translate(mGroundOffset);

            ac.target.transform.getTranslation(newPos);

            if (oldPos.dst(newPos) > 0.05f) {
                if(!ac.current.animation.id.equals(ANIMATION_WALK)) {
                    ac.setAnimation(ANIMATION_WALK, -1, 2f, null);
                }
            } else {
                if(ANIMATION_WALK.equals(ac.current.animation.id)) {
                    ac.setAnimation(ANIMATION_IDLE, -1);
                }
            }

            ac.update(delta);
        }
    }
}
