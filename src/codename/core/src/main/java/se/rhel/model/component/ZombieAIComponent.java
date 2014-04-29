package se.rhel.model.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.RayVector;

/**
 * Created by rkh on 2014-04-28.
 */
public class ZombieAIComponent implements IAIComponent, IComponent {

    private IPlayer mVictim;
    private Vector3 mDirection = new Vector3();
    private float mMovetimer = 4f;
    private boolean mChasingTarget = false;

    private ITransform mTransform;
    private IPhysics mPhysic;
    private IGravity mGravity;

    public ZombieAIComponent(IPlayer player, GameObject obj) {
        mVictim = player;

        mTransform = (ITransform) obj.getComponent(TransformComponent.class);
        mPhysic = (IPhysics) obj.getComponent(PhysicsComponent.class);
        mGravity = (IGravity) obj.getComponent(GravityComponent.class);
    }

    public void update(float delta) {
        if(RayVector.getDistance(mVictim.getPosition(), mTransform.getPosition()) < 15) {
            mChasingTarget = true;
            mDirection = mVictim.getPosition().cpy().sub(mTransform.getPosition()).nor();

            Vector2 currXDir = new Vector2(mTransform.getDirection().x, mTransform.getDirection().z).nor();
            Vector2 wantedXDir = new Vector2(mDirection.x, mDirection.z).nor();

            float xangle = (float) Math.toDegrees(Math.atan2(wantedXDir.cpy().crs(currXDir), wantedXDir.cpy().dot(currXDir)));
            float yangle = (float) Math.toDegrees(mDirection.y - mTransform.getDirection().y);

            mTransform.rotateBy(new Vector3(xangle, yangle, 0));
        } else {
            mChasingTarget = false;
            mMovetimer += delta;
            if(mMovetimer > 4f) {
                mMovetimer = 0;
                mTransform.rotateTo(new Vector3((float) (Math.random() * 360), 0, 0));
                mDirection.set(mTransform.getDirection());
            }
        }

//        shootTimer += delta;
//        if(shootTimer > 2f && mAI.hasTarget()) {
//            mActionComponent.shoot();
//            shootTimer = 0f;
//        }

        Vector3 vel = new Vector3(mDirection.cpy().scl(4f));
        vel.y = mGravity.getGravity();
        mPhysic.getBody().activate(true);
        mPhysic.getBody().setLinearVelocity(vel);

        mTransform.getTransformation().setTranslation(mPhysic.getBody().getCenterOfMassPosition());
    }
}
