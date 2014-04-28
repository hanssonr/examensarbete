package se.rhel.network.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import se.rhel.Connection;
import se.rhel.event.*;
import se.rhel.model.BaseWorldModel;
import se.rhel.model.component.DamageComponent;
import se.rhel.model.component.GameObject;
import se.rhel.model.component.IDamageable;
import se.rhel.model.component.MoveComponent;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Explosion;
import se.rhel.model.weapon.Grenade;
import se.rhel.model.weapon.IExplodable;
import se.rhel.view.input.PlayerInput;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Group: Multiplayer
 */
public class ServerWorldModel extends BaseWorldModel {

    // Linkin ID's and Players
    private HashMap<Integer, ExternalPlayer> mPlayers;

    private final float LOW_FREQ = 0.1f;
    private float CURR_FREQ_TIMER = 0f;
    private boolean SEND_LOW_FREQ = false;

    public ServerWorldModel(Events events) {
        super(events);
        mPlayers = new HashMap<>();
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

        for(ExternalPlayer p : mPlayers.values()) {
            p.update(delta);
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

    public void checkShootCollision(RayVector ray, Connection con) {
        MyContactListener.CollisionObject co = super.getShootCollision(ray);

        if(co != null) {
            if(co.type == MyContactListener.CollisionObject.CollisionType.ENTITY) {
                damageEntity(co.entity, 25);
                mEvents.notify(new ServerModelEvent(EventType.DAMAGE, co.entity));
            }
            else if(co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
                mEvents.notify(new ServerModelEvent(EventType.SERVER_WORLD_COLLISION, co.hitPoint, co.hitNormal, con));
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

            Explosion exp = new Explosion(entity.getPosition(), 15, 250);
            handleExplosion(getAffectedByExplosion(exp), exp);
            mEvents.notify(new ServerModelEvent(EventType.SERVER_DEAD_ENTITY, entity));
        }
    }

    public HashMap<Integer, ExternalPlayer> getPlayers() {
        return mPlayers;
    }

    public void addPlayer(int id, ExternalPlayer player) {
        mPlayers.put(id, player);
    }

    public ExternalPlayer getExternalPlayer(int id) {
        return mPlayers.get(id);
    }

    public void transformEntity(int clientId, Vector3 position, Vector3 rotation) {
        GameObject obj = getExternalPlayer(clientId);
        obj.rotateAndTranslate(rotation, position);

    }
}
