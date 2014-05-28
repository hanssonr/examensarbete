package se.rhel.network.model;


import com.badlogic.gdx.math.Vector3;
import se.rhel.event.*;
import se.rhel.model.BaseWorldModel;
import se.rhel.model.component.*;
import se.rhel.model.entity.ControlledPlayer;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Explosion;
import se.rhel.model.weapon.Grenade;
import se.rhel.model.weapon.IExplodable;
import se.rhel.network.event.ServerModelEvent;
import se.rhel.network.event.ServerModelEvents;
import se.rhel.util.Utils;
import se.rhel.view.input.PlayerInput;

import java.util.ArrayList;

/**
 * Group: Multiplayer
 */
public class ServerWorldModel extends BaseWorldModel {

    // Linkin ID's and Players
    //private HashMap<Integer, IPlayer> mPlayers;

    private final float LOW_FREQ = 0.1f;
    private float CURR_FREQ_TIMER = 0f;
    private boolean SEND_LOW_FREQ = false;

    public ServerWorldModel(Events events) {
        super(events);

        for(int i = 0; i < 10; i++) {
            float x = (float) (Math.random() * 81)-40;
            float z = (float) (Math.random() * 81)-40;

            ControlledPlayer cp = new ControlledPlayer(getBulletWorld(), new Vector3(x, 10, z));
            cp.addComponent(new BasicAI(cp));
            int id = Utils.getInstance().generateUniqueId();
            cp.addComponent(new NetworkComponent(id));
            setPlayer(id, cp);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // Low frequency update
        CURR_FREQ_TIMER += delta;
        if(CURR_FREQ_TIMER >= LOW_FREQ) {
            SEND_LOW_FREQ = true;
            CURR_FREQ_TIMER = 0f;
        } else {
            SEND_LOW_FREQ = false;
        }

        for(IPlayer p : getAllPlayers()) {
            p.update(delta);

            if(p.isAlive()) {
                if(((GameObject)p).hasComponent(IAIComponent.class)) {
                    if(PlayerInput.DO_LOW_FREQ_UPDATES) {
                        if(SEND_LOW_FREQ) {
                            // mEvents.notify(new ServerModelEvent(EventType.PLAYER_MOVE, p));
                            mEvents.notify(new ServerModelEvents.GameObjectEvent(EventType.PLAYER_MOVE, (GameObject)p));
                        }
                    } else {
                        // mEvents.notify(new ServerModelEvent(EventType.PLAYER_MOVE, p));
                        mEvents.notify(new ServerModelEvents.GameObjectEvent(EventType.PLAYER_MOVE, (GameObject)p));
                    }
                }

                IActionable ac = (IActionable) ((GameObject)p).getComponent(ActionComponent.class);
                if(ac.hasShoot()) {
                    RayVector ray = new RayVector(p.getShootPosition(), p.getDirection(), 75f);
                    // mEvents.notify(new ServerModelEvent(EventType.SHOOT, ray, p));
                    mEvents.notify(new ServerModelEvents.ShootEvent(ray, p));
                }
            }
        }

        //Update grenades
        for (Grenade grenade : getGrenades()) {
            grenade.update(delta);

            if(!grenade.isAlive()) {
                // A grenade is found as dead, send it to client as such
                // mEvents.notify(new ServerModelEvent(EventType.GRENADE, grenade, false));
                mEvents.notify(new ServerModelEvents.GrenadeEvent(grenade, false));

                handleExplosion(grenade);
                destroyGameObject(grenade);
                removeGrenade(grenade);
            } else {
                // The grenade is still alive and should be synched with client
                // If we want a lower freq update
                if(PlayerInput.DO_LOW_FREQ_UPDATES) {
                    if(SEND_LOW_FREQ) {
                        // mEvents.notify(new ServerModelEvent(EventType.GRENADE, grenade, true));
                        mEvents.notify(new ServerModelEvents.GrenadeEvent(grenade, true));
                    }
                } else {
                    mEvents.notify(new ServerModelEvents.GrenadeEvent(grenade, true));
                    // mEvents.notify(new ServerModelEvent(EventType.GRENADE, grenade, true));
                }
            }
        }
    }

    public void checkShootCollision(RayVector ray, GameObject shooter) {
        MyContactListener.CollisionObject co = super.getShootCollision(ray);

        if(co != null) {
            if(co.type == MyContactListener.CollisionObject.CollisionType.ENTITY) {
                if(!co.entity.equals(shooter)) {
                    damageEntity(co.entity, 25);
                    // mEvents.notify(new ServerModelEvent(EventType.DAMAGE, co.entity));
                    mEvents.notify(new ServerModelEvents.GameObjectEvent(EventType.DAMAGE, co.entity));
                }
            }
            else if(co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
                // mEvents.notify(new ServerModelEvent(EventType.SERVER_WORLD_COLLISION, co.hitPoint, co.hitNormal));
                mEvents.notify(new ServerModelEvents.ServerWorldCollision(co.hitPoint, co.hitNormal));
            }

            ray.setTo(co.hitPoint);
        }
    }

    public void handleExplosion(IExplodable exp) {
        ArrayList<GameObject> affected = getAffectedByExplosion(exp);

        for(GameObject obj : affected) {
            IDamageable entity = (IDamageable) obj.getComponent(DamageComponent.class);
            entity.damageEntity(exp.getExplosionDamage());
            // mEvents.notify(new ServerModelEvent(EventType.DAMAGE, obj));
            mEvents.notify(new ServerModelEvents.GameObjectEvent(EventType.DAMAGE, obj));
        }
    }

    public void checkEntityStatus(GameObject entity) {
        IDamageable dae = (IDamageable) entity.getComponent(DamageComponent.class);
        if(dae.isAlive() && dae.getHealth() <= 0) {
            dae.setAlive(false);

            Explosion exp = new Explosion(entity.getPosition(), 5, 50);
            handleExplosion(exp);
            // mEvents.notify(new ServerModelEvent(EventType.SERVER_DEAD_ENTITY, entity));
            mEvents.notify(new ServerModelEvents.GameObjectEvent(EventType.SERVER_DEAD_ENTITY, entity));
        }
    }

    public void transformEntity(int clientId, Vector3 position, Vector3 rotation) {
        GameObject obj = (ControlledPlayer)getPlayer(clientId);
        obj.rotateAndTranslate(rotation, position);
    }
}
