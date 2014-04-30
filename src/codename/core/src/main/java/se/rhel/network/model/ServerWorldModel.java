package se.rhel.network.model;


import com.badlogic.gdx.math.Vector3;
import se.rhel.event.*;
import se.rhel.model.BaseWorldModel;
import se.rhel.model.component.*;
import se.rhel.model.entity.DummyEntity;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Explosion;
import se.rhel.model.weapon.Grenade;
import se.rhel.model.weapon.IExplodable;
import se.rhel.network.event.ServerModelEvent;
import se.rhel.util.Utils;
import se.rhel.view.input.PlayerInput;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Group: Multiplayer
 */
public class ServerWorldModel extends BaseWorldModel {

    // Linkin ID's and Players
    private HashMap<Integer, IPlayer> mPlayers;

    private final float LOW_FREQ = 0.1f;
    private float CURR_FREQ_TIMER = 0f;
    private boolean SEND_LOW_FREQ = false;

    public ServerWorldModel(Events events) {
        super(events);
        mPlayers = new HashMap<>();

        for(int i = 0; i < 0; i++) {
            float x = (float) (Math.random() * 10)-5;
            float z = (float) (Math.random() * 10)-5;

            DummyEntity de = new DummyEntity(getBulletWorld(), 0.6f, 1.5f, 100, 7f, new Vector3(x, 10, z));
            de.addComponent(new ZombieAIComponent(mPlayers.get(1), de));
            int id = Utils.getInstance().generateUniqueId();
            de.addComponent(new NetworkComponent(id));
            mPlayers.put(id, de);
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

        for(IPlayer p : mPlayers.values()) {
            p.update(delta);

            if(((GameObject)p).hasComponent(IAIComponent.class)) {
                if(PlayerInput.DO_LOW_FREQ_UPDATES) {
                    if(SEND_LOW_FREQ) {
                        mEvents.notify(new ServerModelEvent(EventType.PLAYER_MOVE, p));
                    }
                } else {
                    mEvents.notify(new ServerModelEvent(EventType.PLAYER_MOVE, p));
                }
            }

            IActionable ac = (IActionable) ((GameObject)p).getComponent(ActionComponent.class);
            if(ac.hasShoot()) {
                RayVector ray = new RayVector(p.getShootPosition(), p.getDirection(), 75f);
                mEvents.notify(new ServerModelEvent(EventType.SHOOT, ray, p));
            }
        }

        //Update grenades
        for (int i = 0; i < mGrenades.size; i++) {
            Grenade g = mGrenades.get(i);
            g.update(delta);

            if(!g.isAlive()) {
                // A grenade is found as dead, send it to client as such
                mEvents.notify(new ServerModelEvent(EventType.GRENADE, g, false));

                handleExplosion(getAffectedByExplosion(g), g);
                g.destroy();
                mGrenades.removeIndex(i);
            } else {
                // The grenade is still alive and should be synched with client
                // If we want a lower freq update
                if(PlayerInput.DO_LOW_FREQ_UPDATES) {
                    if(SEND_LOW_FREQ) {
                        mEvents.notify(new ServerModelEvent(EventType.GRENADE, g, true));
                    }
                } else {
                    mEvents.notify(new ServerModelEvent(EventType.GRENADE, g, true));
                }
            }
        }
    }

    public void checkShootCollision(RayVector ray) {
        MyContactListener.CollisionObject co = super.getShootCollision(ray);

        if(co != null) {
            if(co.type == MyContactListener.CollisionObject.CollisionType.ENTITY) {
                damageEntity(co.entity, 25);
                mEvents.notify(new ServerModelEvent(EventType.DAMAGE, co.entity));
            }
            else if(co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
                mEvents.notify(new ServerModelEvent(EventType.SERVER_WORLD_COLLISION, co.hitPoint, co.hitNormal));
            }

            ray.setTo(co.hitPoint);
        }
    }

    public void handleExplosion(ArrayList<GameObject> hit, IExplodable exp) {
        for(GameObject obj : hit) {
            IDamageable entity = (IDamageable) obj.getComponent(DamageComponent.class);
            entity.damageEntity(exp.getExplosionDamage());
            mEvents.notify(new ServerModelEvent(EventType.DAMAGE, obj));
        }
    }

    public void checkEntityStatus(GameObject entity) {
        IDamageable dae = (IDamageable) entity.getComponent(DamageComponent.class);
        if(dae.isAlive() && dae.getHealth() <= 0) {
            dae.setAlive(false);

            Explosion exp = new Explosion(entity.getPosition(), 5, 50);
            handleExplosion(getAffectedByExplosion(exp), exp);
            mEvents.notify(new ServerModelEvent(EventType.SERVER_DEAD_ENTITY, entity));
        }
    }

    public HashMap<Integer, IPlayer> getPlayers() {
        return mPlayers;
    }

    public void addPlayer(int id, ExternalPlayer player) {
        mPlayers.put(id, player);
    }

    public IPlayer getExternalPlayer(int id) {
        return mPlayers.get(id);
    }

    public void transformEntity(int clientId, Vector3 position, Vector3 rotation) {
        GameObject obj = (DummyEntity)getExternalPlayer(clientId);
        obj.rotateAndTranslate(rotation, position);
    }
}
