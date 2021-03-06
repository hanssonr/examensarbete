package se.rhel.network.controller;

import com.badlogic.gdx.math.Vector3;
import se.rhel.Client;
import se.rhel.Snaek;
import se.rhel.event.*;
import se.rhel.model.component.NetworkComponent;
import se.rhel.model.entity.ControlledPlayer;
import se.rhel.model.physics.RayVector;
import se.rhel.network.event.NetworkEvent;
import se.rhel.network.event.NetworkListener;
import se.rhel.network.model.ClientWorldModel;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.model.INetworkWorldModel;
import se.rhel.network.packet.*;
import se.rhel.packet.Packet;
import se.rhel.screen.BaseGameController;
import se.rhel.screen.scene.MainMenu;
import se.rhel.view.WorldView;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Group: Multiplayer
 * Created by Emil on 2014-04-02.
 */
public class ClientController extends BaseGameController implements NetworkListener {

    private Client mClient;
    private ClientSynchronizedUpdate mSyncedUpdate;
    private boolean mStarted = false;

    public ClientController(String host) throws IOException {
        super();
        mEvents.listen(NetworkEvent.class, this);
        mSyncedUpdate = new ClientSynchronizedUpdate(mEvents);
        mClient = Snaek.newClient(mSyncedUpdate);
        mClient.connect(host, 4455, 5544);
    }

    private void startGame() {
        mWorldModel = new ClientWorldModel(mClient.getId(), mEvents);
        mWorldView = new WorldView(mWorldModel);
        mClient.sendTcp(new RequestInitialStatePacket(mClient.getId()));
        mStarted = true;
    }

    public void update(float delta) {
        if(mStarted)
            super.update(delta);

        // Network stuff
        mSyncedUpdate.update();
    }

    public void draw(float delta) {
        if(mStarted)
            mWorldView.render(delta);
    }

    public void dispose() {
        mWorldView.dispose();
    }

    @Override
    public void inputEvent(EventType type) {
        super.inputEvent(type);
        switch (type) {
            case GRENADE:
                // Send to server
                mClient.sendTcp(new GrenadeCreatePacket(mClient.getId(),
                        mWorldModel.getPlayer().getShootPosition(),
                        mWorldModel.getPlayer().getDirection()));
                break;
        }
    }

    @Override
    public void modelEvent(EventType type, Object... objs) {
        super.modelEvent(type, objs);
        switch (type) {
            case SHOOT: // [0] = RayVector
                RayVector ray = (RayVector) objs[0];
                mWorldModel.checkShootCollision(ray);

                // The network, notify the server that we have shot
                mClient.sendTcp(new ShootPacket(mClient.getId(), ray.getFrom(), ray.getTo()));

                RayVector.convertToVisual(ray);
                mWorldView.shoot(ray);
                break;

            case BULLET_HOLE:
                mWorldView.addBullethole((Vector3)objs[0], (Vector3)objs[1]);
                break;

            case GRENADE_CREATED:
                mWorldView.addGrenade((Grenade) objs[0]);
                break;

            case PLAYER_MOVE:
                mClient.sendUdp(new PlayerMovePacket(mClient.getId(),
                        mWorldModel.getPlayer().getPosition(),
                        mWorldModel.getPlayer().getRotation()));
                break;
            case PLAYER_JOIN:
                mWorldView.addPlayer((ControlledPlayer)objs[0]);
                break;
            case CONNECTED:
                startGame();
                break;
            default:
                break;
        }
    }

    @Override
    public void networkEvent(Packet packet) {
        if(packet instanceof ShootPacket) {
            ShootPacket sp = (ShootPacket) packet;

            // The rendering and sound
            RayVector rv = new RayVector(sp.mFrom, sp.mTo);
            RayVector.convertToVisual(rv);
            mWorldView.shoot(rv);
        }
        else if(packet instanceof BulletHolePacket) {
            BulletHolePacket bhp = (BulletHolePacket) packet;
            // Draw bullethole
            mWorldView.addBullethole(bhp.hitWorld, bhp.hitNormal);
        }

        else if (packet instanceof  PlayerPacket) {
            PlayerPacket pp = (PlayerPacket) packet;
            for(int i = 0; i < pp.mPlayers.size(); i++) {
                UpdateStruct ps = pp.mPlayers.get(i);
                if(ps.mID != mClient.getId()) {
                    ControlledPlayer cp = new ControlledPlayer(mWorldModel.getBulletWorld(), ps.mPosition);
                    cp.addComponent(new NetworkComponent(ps.mID));
                    cp.rotateAndTranslate(ps.mRotation, ps.mPosition);
                    mWorldModel.setPlayer(ps.mID, cp);
                    mEvents.notify(new ModelEvent(EventType.PLAYER_JOIN, cp));
                }
            }
        }

        else if (packet instanceof DeadEntityPacket) {
            DeadEntityPacket dep = (DeadEntityPacket) packet;
            ((INetworkWorldModel)mWorldModel).killEntity(dep.clientId);
        }

        else if (packet instanceof GrenadeCreatePacket) {
            GrenadeCreatePacket gcp = (GrenadeCreatePacket) packet;

            Grenade g = new Grenade(mWorldModel.getBulletWorld(), gcp.position, gcp.direction);
            g.addComponent(new NetworkComponent(gcp.clientId));

            mWorldModel.addGrenade(g);
            mEvents.notify(new ModelEvent(EventType.GRENADE_CREATED, g));
        }

        else if (packet instanceof GrenadeUpdatePacket) {
            GrenadeUpdatePacket gup = (GrenadeUpdatePacket) packet;
            ((ClientWorldModel) mWorldModel).updateGrenade(gup.clientId, gup.position, gup.rotation, gup.isAlive);
        }

        else if (packet instanceof PlayerMovePacket) {
            PlayerMovePacket pmp = (PlayerMovePacket) packet;
            ((INetworkWorldModel)mWorldModel).transformEntity(pmp.clientId, pmp.mPosition, pmp.mRotation);
        }

        else if (packet instanceof DamagePacket) {
            DamagePacket dp = (DamagePacket) packet;
            ((INetworkWorldModel)mWorldModel).damageEntity(dp.clientId, dp.amount);
        }

        else if (packet instanceof ConnectedPacket) {
            startGame();
        }

        else if(packet instanceof RespawnPacket) {
            RespawnPacket rp = (RespawnPacket) packet;
            mWorldModel.respawn(mWorldModel.getPlayer(rp.mRespawnId));
        }
    }
}
