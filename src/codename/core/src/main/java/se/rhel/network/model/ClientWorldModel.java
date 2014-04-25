package se.rhel.network.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.Client;
import se.rhel.event.EventType;
import se.rhel.event.Events;
import se.rhel.event.ModelEvent;
import se.rhel.model.*;
import se.rhel.model.component.DamageComponent;
import se.rhel.model.component.GameObject;
import se.rhel.model.component.MoveComponent;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.entity.Player;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Grenade;

import java.util.HashMap;


/**
 * Group: Multiplayer
 */
public class ClientWorldModel extends BaseWorldModel implements INetworkWorldModel {

    private Vector3 mLastKnownPosition = Vector3.Zero;
    private float mLastKnownRotation = 0f;

    private Client mClient;
    private Player mPlayer;

    private HashMap<Integer, IPlayer> mPlayers = new HashMap<>();

    public ClientWorldModel(Client client, Events events) {
        super(events);

        mPlayer = new Player(new Vector3(0, 10, 0), getBulletWorld());
        mClient = client;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        updatePlayer(delta);

        for(IPlayer p : mPlayers.values()) {
            p.update(delta);
        }

        for (int i = 0; i < mGrenades.size; i++) {
            Grenade g = mGrenades.get(i);

            g.update(delta);

            if(!g.isAlive()) {
                mEvents.notify(new ModelEvent(EventType.EXPLOSION, g.getPosition()));
                g.destroy();
                mGrenades.removeIndex(i);
            }
        }
    }

    private void updatePlayer(float delta) {
        mPlayer.update(delta);

        if(hasPlayerMoved()) {
            mLastKnownPosition = mPlayer.getPosition();
            mLastKnownRotation = mPlayer.getRotation().x;

            mEvents.notify(new ModelEvent(EventType.PLAYER_MOVE));
        }

        if(mPlayer.wantToShoot()) {
            mEvents.notify(new ModelEvent(EventType.SHOOT));
        }
    }

    private boolean hasPlayerMoved() {
        return (mLastKnownRotation != mPlayer.getRotation().x ||
                RayVector.getDistance(mLastKnownPosition.cpy(), mPlayer.getPosition()) > 0.2f);
    }

    @Override
    public void checkShootCollision(RayVector ray) {
        MyContactListener.CollisionObject co = super.getShootCollision(ray);

        if(co != null) {
            if(co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
                mEvents.notify(new ModelEvent(EventType.BULLET_HOLE, co.hitPoint, co.hitNormal));
            }
        }
    }

    @Override
    public void checkEntityStatus(GameObject entity) {

    }

    public ExternalPlayer getExternalPlayer(int id) {
        try {
            for (IPlayer entity : mPlayers.values()) {
                ExternalPlayer ep = (ExternalPlayer)entity;

                if(ep.getNetworkID() == id) {
                    return ep;
                }
            }
            throw new IllegalArgumentException("No player with id " + id);
        } catch(Exception e) {
            return null;
        }
    }

    public void addPlayer(int id, ExternalPlayer player) {
        mPlayers.put(id, player);
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public Array<IPlayer> getExternalPlayers() {
        Array<IPlayer> ret = new Array<>();
        for(IPlayer ent : mPlayers.values()) {
            ret.add(ent);
        }

        return ret;
    }

    public IPlayer getPlayerEntity(int id) {
        return id == mClient.getId() ? (IPlayer)mPlayer : getExternalPlayer(id);
    }

    public void damageEntity(int id, int amount) {
        GameObject obj = (GameObject)(mClient.getId() == id ? mPlayer : getExternalPlayer(id));
        super.damageEntity(obj, amount);
        mEvents.notify(new ModelEvent(EventType.DAMAGE, obj));
    }

    public void killEntity(int id) {
        GameObject obj = (GameObject)(mClient.getId() == id ? mPlayer : getExternalPlayer(id));
        super.killEntity(obj);
        mEvents.notify(new ModelEvent(EventType.EXPLOSION, obj.getPosition()));
    }

    public void transformEntity(int clientId, Vector3 position, Vector2 rotation) {
        GameObject obj = getExternalPlayer(clientId);
        obj.rotateAndTranslate(rotation, position);
    }
}