package se.rhel.model;

import com.badlogic.gdx.math.Vector3;
import se.rhel.event.*;
import se.rhel.model.component.*;
import se.rhel.model.entity.ControlledPlayer;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.entity.Player;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Explosion;
import se.rhel.model.weapon.Grenade;
import se.rhel.model.weapon.IExplodable;
import java.util.ArrayList;

/**
 * Group: Logic
 *
 * Created by rkh on 2014-03-21.
 */
public class WorldModel extends BaseWorldModel implements IWorldModel {

    private Player mPlayer;

    public WorldModel(Events events) {
        super(events);
        mPlayer = new Player(new Vector3(0, 20, 0), getBulletWorld());
        addPlayer(mPlayer);

        for(int i = 0; i < 2; i++) {
            float x = (float) (Math.random() * 81)-40;
            float z = (float) (Math.random() * 81)-40;

            ControlledPlayer cp = new ControlledPlayer(getBulletWorld(), new Vector3(x, 10, z));
            cp.addComponent(new BasicAI(cp));
            addPlayer(cp);
        }
    }

    private void updatePlayer(float delta) {
        mPlayer.update(delta);

        if(mPlayer.wantToShoot()) {
            RayVector ray = new RayVector(mPlayer.getShootPosition(), mPlayer.getDirection(), 75f);
            mEvents.notify(new ModelEvent(EventType.SHOOT, ray));
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        updatePlayer(delta);

        for(IPlayer player : getControlledPlayers()) {
            ControlledPlayer cp = (ControlledPlayer) player;
            cp.update(delta);

            IActionable ac = (IActionable) cp.getComponent(ActionComponent.class);
            if(ac.hasShoot()) {
                RayVector ray = new RayVector(cp.getShootPosition(), cp.calculateShootDirection(), 75f);
                mEvents.notify(new ModelEvent(EventType.SHOOT, ray));
            }
        }

        for(Grenade grenade : getGrenades()) {
            grenade.update(delta);

            if(!grenade.isAlive()) {
                mEvents.notify(new ModelEvent(EventType.EXPLOSION, grenade.getPosition()));
                handleExplosion(grenade);
                destroyGameObject(grenade);
                removeGrenade(grenade);
            }
        }
    }

    @Override
    public void checkShootCollision(RayVector ray) {
        MyContactListener.CollisionObject co = super.getShootCollision(ray);

        if(co != null) {
            if(co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
                mEvents.notify(new ModelEvent(EventType.BULLET_HOLE, co.hitPoint, co.hitNormal));
            }
            else if(co.type == MyContactListener.CollisionObject.CollisionType.ENTITY) {
                damageEntity(co.entity, 25);
                mEvents.notify(new ModelEvent(EventType.DAMAGE, co.entity));
            }

            ray.setTo(co.hitPoint);
        }
    }

    public void handleExplosion(IExplodable exp) {
        ArrayList<GameObject> affected = getAffectedByExplosion(exp);

        for(GameObject obj : affected) {
            IDamageable entity = (IDamageable) obj.getComponent(DamageComponent.class);
            entity.damageEntity(exp.getExplosionDamage());
            mEvents.notify(new ModelEvent(EventType.DAMAGE, obj));
        }
    }

    public void checkEntityStatus(GameObject entity) {
        IDamageable da = (IDamageable) entity.getComponent(DamageComponent.class);

        if(da.isAlive() && da.getHealth() <= 0) {
            da.setAlive(false);

            Explosion exp = new Explosion(entity.getPosition(), 5, 50);
            mEvents.notify(new ModelEvent(EventType.EXPLOSION, exp.getPosition()));
            handleExplosion(exp);
            entity.destroy();
        }
    }

    public Player getPlayer() {
        return mPlayer;
    }
}
