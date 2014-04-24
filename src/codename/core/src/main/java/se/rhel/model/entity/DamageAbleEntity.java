package se.rhel.model.entity;

import com.badlogic.gdx.math.Vector3;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.model.physics.BulletWorld;


/**
 * Group: Logic
 *
 * Created by rkh on 2014-03-24.
 */
public class DamageAbleEntity extends DynamicEntity {

    protected boolean mAlive = true;
    protected int mHealth;

    private boolean mStartRespawnTimer = false;
    private float mCurrentRespawnTime = 0;
    private float mMaxRespawnTime = 5f;

    private int A_POINT_FAR_FAR_AWAY = 10;

    protected DamageAbleEntity(BulletWorld world, int maxHealth, float movespeed) {
        super(world, movespeed);

        mHealth = maxHealth;
    }

    public void update(float delta) {
        if(mStartRespawnTimer) {

            mCurrentRespawnTime += delta;
            if(mCurrentRespawnTime >= mMaxRespawnTime) {

                mCurrentRespawnTime = 0;
                mStartRespawnTimer = false;
                setPosition(new Vector3(getPosition().x, getPosition().y - A_POINT_FAR_FAR_AWAY + 1f, getPosition().z));
                setAlive(true);

            }
        }

    }

    public boolean isAlive() {
        return mAlive;
    }

    public int getHealth() {
        return mHealth;
    }

    public void damageEntity(int amount) {
        mHealth -= amount;

        //EventHandler.events.notify(new ModelEvent(EventType.DAMAGE, this));
    }

    public void setAlive(boolean b) {
        mAlive = b;

        if(!b) {
            // Dead dynamic-entity
            Vector3 currPos = new Vector3(getPosition().x, getPosition().y + A_POINT_FAR_FAR_AWAY, getPosition().z);
            setPosition(currPos.cpy());
            mStartRespawnTimer = true;
        }
    }
}
