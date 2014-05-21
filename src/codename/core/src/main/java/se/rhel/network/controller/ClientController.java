package se.rhel.network.controller;

import com.badlogic.gdx.math.Vector3;
import se.rhel.Client;
import se.rhel.Snaek;
import se.rhel.event.*;
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
import se.rhel.view.WorldView;

/**
 * Group: Multiplayer
 * Created by Emil on 2014-04-02.
 */
public class ClientController extends BaseGameController implements NetworkListener {

    private Client mClient;
    private ClientSynchronizedUpdate mSyncedUpdate;

    public ClientController() {
        super();
        mClient = Snaek.newClient(4455, 5544, "localhost");
        mWorldModel = new ClientWorldModel(mClient.getId(), mEvents);
        mSyncedUpdate = new ClientSynchronizedUpdate(mEvents);
        mClient.addListener(mSyncedUpdate);

        mClient.sendTcp(new RequestInitialStatePacket(mClient.getId()));
        mWorldView = new WorldView(mWorldModel);

        mEvents.listen(NetworkEvent.class, this);
    }

    public void update(float delta) {
        super.update(delta);

        // Network stuff
        mSyncedUpdate.update((INetworkWorldModel)mWorldModel, mClient);
    }

    public void draw(float delta) {
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
    }
}
