package se.rhel.network.model;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.Client;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.model.*;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Grenade;
import se.rhel.util.Log;

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

    public ClientWorldModel(Client client) {
        super();

        mPlayer = new Player(new Vector3(0, 10, 0), getBulletWorld());
        mClient = client;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        mPlayer.update(delta);

        if(hasPlayerMoved()) {
            mLastKnownPosition = mPlayer.getPosition();
            mLastKnownRotation = mPlayer.getRotation().x;

            EventHandler.events.notify(new ModelEvent(EventType.PLAYER_MOVE));
        }

        for(IPlayer p : mPlayers.values()) {
            p.update(delta);
        }

        for (int i = 0; i < mGrenades.size; i++) {
            Grenade g = mGrenades.get(i);

            g.update(delta);

            if(!g.isAlive()) {
                EventHandler.events.notify(new ModelEvent(EventType.EXPLOSION, g.getPosition()));
                g.destroy();
                mGrenades.removeIndex(i);
            }
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
                EventHandler.events.notify(new ModelEvent(EventType.BULLET_HOLE, co.hitPoint, co.hitNormal));
            }
        }
    }

    @Override
    public void checkEntityStatus(DamageAbleEntity entity) {

    }

    public ExternalPlayer getExternalPlayer(int id) {
        try {
            for (IPlayer entity : mPlayers.values()) {
                ExternalPlayer ep = (ExternalPlayer)entity;
                if(ep.getClientId() == id) {
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
        DamageAbleEntity dae = mClient.getId() == id ? mPlayer : getExternalPlayer(id);
        super.damageEntity(dae, amount);
        EventHandler.events.notify(new ModelEvent(EventType.DAMAGE, dae));
    }

    public void killEntity(int id) {
        DamageAbleEntity dae = mClient.getId() == id ? mPlayer : getExternalPlayer(id);
        dae.setAlive(false);
        EventHandler.events.notify(new ModelEvent(EventType.EXPLOSION, dae.getPosition()));
    }
}