package se.rhel.view;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.entity.IPlayer;
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
    private ModelInstance mLaser = new ModelInstance(Resources.INSTANCE.laserWeaponModel);

    private Vector3 mArmOffset = new Vector3(0.45f, 0.4f, -0.2f);
    private Vector3 mGroundOffset = new Vector3();
    private BoundingBox mBox = new BoundingBox();
    private Array<IPlayer> mPlayers;

    //Animation
    private HashMap<IPlayer, AnimationController> mAnimations = new HashMap<>();
    private HashMap<IPlayer, ModelInstance> mWeaponArms = new HashMap<>();
    private HashMap<IPlayer, Boolean> mAnimationStop = new HashMap<>();
    private Vector3 newPos = new Vector3();
    private Vector3 oldPos = new Vector3();

    public ExternalPlayerRenderer(Array<IPlayer> players) {
        mExternalPlayer.calculateBoundingBox(mBox);
        mGroundOffset.set(0, -mBox.getDimensions().y / 2.0f, 0);
        mPlayers = players;

        for (int i = 0; i < mPlayers.size; i++) {
            addPlayerAnimation(mPlayers.get(i));
        }
    }

    public void addPlayerAnimation(IPlayer entity) {
        AnimationController ac = new AnimationController(mExternalPlayer.copy());
        ac.setAnimation(ANIMATION_IDLE);

        mAnimations.put(entity, ac);

        //should check what weapon entity have
        mWeaponArms.put(entity, mLaser.copy());
        mAnimationStop.put(entity, true);

        System.out.println("ExternalPlayerRenderer : " + mAnimations.size());
    }

    public void render(ModelBatch batch, Environment env) {
        for(IPlayer player : mAnimations.keySet()) {
            if(player.isAlive()) {
                AnimationController playerAnimation = mAnimations.get(player);
                ModelInstance playerWeapon = mWeaponArms.get(player);

                if(playerAnimation != null && playerWeapon != null) {
                    batch.render(playerAnimation.target, env);
                    batch.render(playerWeapon, env);
                }
            }
        }
    }

    public void update(float delta) {

        Iterator it = mAnimations.entrySet().iterator();
        while(it.hasNext()) {

            Map.Entry<IPlayer, AnimationController> pairs = (Map.Entry)it.next();
            IPlayer entity = pairs.getKey();
            AnimationController ac = pairs.getValue();

            ac.target.transform.getTranslation(oldPos);

            ac.target.transform.set(entity.getTransformation());
            ac.target.transform.translate(mGroundOffset);

            ac.target.transform.getTranslation(newPos);

            if (newPos.dst(oldPos) > 0) {
                mAnimationStop.put(entity, false);
                if(!ac.current.animation.id.equals(ANIMATION_WALK)) {
                    ac.setAnimation(ANIMATION_WALK, -1, 2f, null);
                }
            } else {
                if(mAnimationStop.get(entity)) {
                    if(ANIMATION_WALK.equals(ac.current.animation.id)) {
                        ac.setAnimation(ANIMATION_IDLE, -1);
                    }
                } else {
                    mAnimationStop.put(entity, true);
                }
            }

            ac.update(delta);

            mWeaponArms.get(entity).transform.set(entity.getTransformation());
            mWeaponArms.get(entity).transform.translate(mArmOffset);
            mWeaponArms.get(entity).transform.rotate(Vector3.X.cpy(), entity.getRotation().y);
        }
    }
}
