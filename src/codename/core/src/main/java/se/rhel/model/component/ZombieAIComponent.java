package se.rhel.model.component;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.RayVector;

/**
 * Created by rkh on 2014-04-28.
 */
public class ZombieAIComponent {

    private IPlayer mVictim;
    private Vector3 mDirection = new Vector3();
    private Vector3 mTempPos = new Vector3();
    private float mMovetimer = 4f;
    private ITransform mTc;
    private boolean mChasingTarget = false;

    public ZombieAIComponent(IPlayer player, ITransform tc) {
        mVictim = player;
        mTc = tc;
    }

    public void update(float delta) {
        mTc.getTransformation().getTranslation(mTempPos);

        if(RayVector.getDistance(mVictim.getPosition(), mTempPos) < 15) {
            mChasingTarget = true;
            mDirection = mVictim.getPosition().cpy().sub(mTempPos).nor();

            Vector2 currXDir = new Vector2(mTc.getDirection().x, mTc.getDirection().z).nor();
            Vector2 wantedXDir = new Vector2(mDirection.x, mDirection.z).nor();

            float xangle = (float) Math.toDegrees(Math.atan2(wantedXDir.cpy().crs(currXDir), wantedXDir.cpy().dot(currXDir)));
            float yangle = (float) Math.toDegrees(mDirection.y - mTc.getDirection().y);

            mTc.rotateBy(new Vector3(xangle, yangle, 0));
        } else {
            mChasingTarget = false;
            mMovetimer += delta;
            if(mMovetimer > 4f) {
                mMovetimer = 0;
                mTc.rotateTo(new Vector3((float) (Math.random() * 360), 0, 0));
                mDirection.set(mTc.getDirection());
            }
        }
    }

    public Vector3 getDirection() {
        return mDirection.cpy();
    }

    public boolean chasingTarget() {
        return mChasingTarget;
    }
}
