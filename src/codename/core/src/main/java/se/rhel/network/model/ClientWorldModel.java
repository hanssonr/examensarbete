package se.rhel.network.model;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import se.rhel.Client;
import se.rhel.event.EventType;
import se.rhel.event.Events;
import se.rhel.event.ModelEvent;
import se.rhel.model.*;
import se.rhel.model.component.GameObject;
import se.rhel.model.component.NetworkComponent;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.entity.Player;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Grenade;
import se.rhel.view.input.PlayerInput;

import java.util.HashMap;


/**
 * Group: Multiplayer
 */
public class ClientWorldModel extends BaseWorldModel implements INetworkWorldModel {

    private Vector3 mLastKnownPosition = Vector3.Zero;
    private float mLastKnownRotation = 0f;

    private HashMap<Integer, Matrix4> mTargetGrePositions = new HashMap<>();

    private Client mClient;
    private Player mPlayer;

    public ClientWorldModel(Client client, Events events) {
        super(events);
        mClient = client;

        mPlayer = new Player(new Vector3(0, 10, 0), getBulletWorld());
        setPlayer(client.getId(), mPlayer);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        updatePlayer(delta);

        for(IPlayer p : getControlledPlayers()) {
            p.update(delta);
        }

        for (Grenade grenade : getGrenades()) {
            // The grenade doesn't have to be updated on client
            // g.update(delta);
            int id = ((NetworkComponent)grenade.getComponent(NetworkComponent.class)).getID();
            Matrix4 toTemp = mTargetGrePositions.get(id);

            if(toTemp != null) {
                if(PlayerInput.CLIENT_INTERPOLATION) {
                    // Interpolate position..
                    Vector3 v = new Vector3();
                    v = grenade.getPosition().slerp(toTemp.getTranslation(v), 0.1f);
                    // .. but just take the rotation from the server
                    Matrix4 newM = new Matrix4(v, toTemp.getRotation(new Quaternion()), new Vector3(1f, 1f, 1f));
                    grenade.getTransformation().set(newM);
                } else {
                    grenade.getTransformation().set(toTemp);
                }
            }

            if(!grenade.isAlive()) {
                mEvents.notify(new ModelEvent(EventType.EXPLOSION, grenade.getPosition()));
                destroyGameObject(grenade);
                removeGrenade(grenade);
            }
        }
    }

    /**
     * A Grenade should be updated on client from server
     * @param grenadeId
     * @param newPos
     */
    public void updateGrenade(int grenadeId, Vector3 newPos, Quaternion newRotation, boolean isAlive) {
        for (Grenade grenade : getGrenades()) {

            // Check if the right component is there, well, it should
            if(!grenade.hasComponent(NetworkComponent.class))
                return;

            // We're looking for a special grenade
            int currId = ((NetworkComponent)grenade.getComponent(NetworkComponent.class)).getID();
            if(currId == grenadeId) {
                // Add the position to the hashmap
                Matrix4 m = new Matrix4(newPos, newRotation, new Vector3(1f, 1f, 1f));
                mTargetGrePositions.put(grenadeId, m);

                // Set the grenade as dead
                if(!isAlive) {
                    grenade.setAlive(isAlive);
                }
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
            RayVector ray = new RayVector(mPlayer.getShootPosition(), mPlayer.getDirection(), 75f);
            mEvents.notify(new ModelEvent(EventType.SHOOT, ray));
        }
    }

    private boolean hasPlayerMoved() {
        return (mLastKnownRotation != mPlayer.getRotation().x ||
                RayVector.getDistance(mLastKnownPosition.cpy(), mPlayer.getPosition()) > 0.2f);
    }

    @Override
    public void checkShootCollision(RayVector ray) {}

    @Override
    public void checkEntityStatus(GameObject entity) {}

    public Player getPlayer() {
        return mPlayer;
    }

    public void damageEntity(int id, int amount) {
        GameObject obj = (mClient.getId() == id ? mPlayer : (GameObject)getPlayer(id));
        super.damageEntity(obj, amount);
        mEvents.notify(new ModelEvent(EventType.DAMAGE, obj));
    }

    public void killEntity(int id) {
        GameObject obj = (mClient.getId() == id ? mPlayer : (GameObject)getPlayer(id));
        super.killEntity(obj);
        mEvents.notify(new ModelEvent(EventType.EXPLOSION, obj.getPosition()));
    }

    public void transformEntity(int id, Vector3 position, Vector3 rotation) {
        GameObject obj = (mClient.getId() == id ? mPlayer : (GameObject)getPlayer(id));
        obj.rotateAndTranslate(rotation, position);
    }
}