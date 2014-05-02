package se.rhel.model.component;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by rkh on 2014-04-28.
 */
public class BasicAI implements IAIComponent, IComponent, IUpdateable {

    private Vector3 mDirection = new Vector3();
    private float mMovetimer = 4f;
    private float mShootTimer = 0;
    private float mJumpTimer = 10f;

    private ITransform mTransform;
    private IPhysics mPhysic;
    private IGravity mGravity;
    private IActionable mAction;
    private IDamageable mDamage;

    public BasicAI(GameObject obj) {
        mShootTimer = (float)((Math.random() * 10) + 5);

        mTransform = (ITransform) obj.getComponent(TransformComponent.class);
        mPhysic = (IPhysics) obj.getComponent(PhysicsComponent.class);
        mGravity = (IGravity) obj.getComponent(GravityComponent.class);
        mAction = (IActionable) obj.getComponent(ActionComponent.class);
        mDamage = (IDamageable) obj.getComponent(DamageComponent.class);
    }

    public void update(float delta) {
//        mChaseTimer += delta;

        //Get victim to chase
//        if(mChaseTimer >= 5f) {
//            mChaseTimer = 0;
//
//            if(mVictims.size > 0)
//                mVictim = mVictims.get((int)Math.floor(Math.random() * mVictims.size));
//        }

        //If victim is alive, chase
//        if(hasVictim() && mVictim.isAlive()) {
//            mDirection = mVictim.getPosition().cpy().sub(mTransform.getPosition()).nor();
//
//            Vector2 currXDir = new Vector2(mTransform.getDirection().x, mTransform.getDirection().z).nor();
//            Vector2 wantedXDir = new Vector2(mDirection.x, mDirection.z).nor();
//
//            float xangle = (float) Math.toDegrees(Math.atan2(wantedXDir.cpy().crs(currXDir), wantedXDir.cpy().dot(currXDir)));
//            float yangle = (float) Math.toDegrees(mDirection.y - mTransform.getDirection().y);
//
//            mTransform.rotateBy(new Vector3(xangle, yangle, 0));
//        } else {
        mMovetimer += delta;
        if(mMovetimer > 4f) {
            mMovetimer = 0;
            mTransform.rotateTo(new Vector3((float) (Math.random() * 360), 0, 0));
            mDirection.set(mTransform.getDirection());
        }
//        }

        mShootTimer -= delta;
        if(mShootTimer <= 0f) {
            if(mDamage.isAlive()) {
                mAction.shoot();
                mShootTimer = (float) (Math.random() * 2 + 2);
            }
        }

        mJumpTimer += delta;
        if(mJumpTimer <= 0f) {
            mGravity.setGravity(7f);
            mJumpTimer = (float) (Math.random() * 5 + 5);
        }

        Vector3 vel = new Vector3(mDirection.cpy().scl(4f));
        vel.y = mGravity.getGravity();

        mPhysic.getBody().activate(true);
        mPhysic.getBody().setLinearVelocity(vel);

        mTransform.getTransformation().setTranslation(mPhysic.getBody().getCenterOfMassPosition());
    }
}
