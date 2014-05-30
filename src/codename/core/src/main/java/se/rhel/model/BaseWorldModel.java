package se.rhel.model;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.event.Events;
import se.rhel.model.component.DamageComponent;
import se.rhel.model.component.GameObject;
import se.rhel.model.component.IDamageable;
import se.rhel.model.component.TeamComponent;
import se.rhel.model.entity.ControlledPlayer;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.BulletWorld;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Grenade;
import se.rhel.model.weapon.IExplodable;
import se.rhel.utils.CombiMap;
import se.rhel.utils.RespawnBoundary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Group: Logic
 * Created by rkh on 2014-03-28.
 */
public class BaseWorldModel {

    private final int MAX_GRENADES = 25;
    protected final RespawnBoundary mRedRespawn = new RespawnBoundary(
            new Vector3(-17f, 1.3f, -90f), new Vector3(17f, 1.3f, -90f),
            new Vector3(-17f, 1.3f, -58f), new Vector3(17f, 1.3f, -58f));

    protected final RespawnBoundary mBlueRespawn = new RespawnBoundary(
            new Vector3(-17f, 1.3f, 58f), new Vector3(17f, 1.3f, 58f),
            new Vector3(-17f, 1.3f, 90f), new Vector3(17f, 1.3f, 90f));

    private Map<IPlayer, Float> mRespawns = new HashMap<IPlayer, Float>();

    private BulletWorld mBulletWorld;
    private MyContactListener mContactListener = new MyContactListener();

    protected ArrayList<GameObject> mDestroy = new ArrayList<>();
    private Array<Grenade> mGrenades = new Array<>(true, MAX_GRENADES);
    protected Events mEvents;

    private CombiMap<IPlayer> mPlayers = new CombiMap<>();

    public BaseWorldModel(Events events) {

        mBulletWorld = new BulletWorld();
        mEvents = events;
    }

    public BulletWorld getBulletWorld() {
        return mBulletWorld;
    }
    public MyContactListener getContactListener() { return mContactListener; }

    public void update(float delta) {
        mBulletWorld.update(delta);

        for (int i = 0; i < mDestroy.size(); i++) {
            mDestroy.get(i).destroy();
            mDestroy.remove(i);
        }
    }

    public MyContactListener.CollisionObject getShootCollision(RayVector ray) {
        return mContactListener.checkShootCollision(getBulletWorld().getCollisionWorld(), ray);
    }

    public ArrayList<GameObject> getAffectedByExplosion(IExplodable explosion) {
        return mContactListener.checkExplosionCollision(getBulletWorld().getCollisionWorld(), explosion);
    }

    public void damageEntity(GameObject obj, int amount) {
        if(obj.hasComponent(DamageComponent.class)) {
            IDamageable dae = (IDamageable)obj.getComponent(DamageComponent.class);
            dae.damageEntity(amount);
        }
    }

    public void killEntity(GameObject obj) {
        if(obj.hasComponent(DamageComponent.class)) {
            IDamageable dae = (IDamageable)obj.getComponent(DamageComponent.class);
            dae.setAlive(false);
        }
    }

    public void destroyGameObject(GameObject obj) {
        mDestroy.add(obj);
    }


    // Players

    public void addPlayer(IPlayer player) {
        mPlayers.add(player);
    }

    public void setPlayer(int index, IPlayer player) {
        mPlayers.set(index, player);
    }

    public Array<IPlayer> getControlledPlayers() {
        Array<IPlayer> players = new Array<>();

        for(IPlayer player : mPlayers.toArray()) {
            if(player instanceof ControlledPlayer)
                players.add(player);
        }
        return players;
    }

    public Array<IPlayer> getAllPlayers() {
        return mPlayers.toArray();
    }

    public IPlayer getPlayer(int index) {
        return mPlayers.get(index);
    }

    // Grenades
    public void addGrenade(Grenade g) {
        mGrenades.add(g);
    }

    public Array<Grenade> getGrenades() {
        return mGrenades;
    }

    public void removeGrenade(Grenade grenade) {
        mGrenades.removeValue(grenade, true);
    }

    protected Map<IPlayer, Float> getRespawnMap() {
        return mRespawns;
    }

    protected void addRespawn(IPlayer player) {
        mRespawns.put(player, 0f);
    }

    public void respawn(IPlayer player) {
        GameObject go = (GameObject) player;
        Vector3 pos = new Vector3(0, 1.3f, 0);
        if(go.hasComponent(TeamComponent.class)) {
            TeamComponent tc = (TeamComponent) go.getComponent(TeamComponent.class);
            pos = tc.getTeam() == 0 ? mBlueRespawn.getRandomPosInBoundary() : mRedRespawn.getRandomPosInBoundary();
        }
        player.respawn(pos);
    }
}
