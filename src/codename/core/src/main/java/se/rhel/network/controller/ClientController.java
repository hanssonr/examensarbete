package se.rhel.network.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import se.rhel.Client;
import se.rhel.Snaek;
import se.rhel.event.*;
import se.rhel.model.physics.RayVector;
import se.rhel.network.event.NetworkEvent;
import se.rhel.network.event.NetworkListener;
import se.rhel.network.model.ClientWorldModel;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.model.INetworkWorldModel;
import se.rhel.network.packet.*;
import se.rhel.packet.Packet;
import se.rhel.screen.BaseGameController;
import se.rhel.view.BulletHoleRenderer;
import se.rhel.view.ParticleRenderer;
import se.rhel.view.WorldView;
import se.rhel.view.input.PlayerInput;

/**
 * Group: Multiplayer
 * Created by Emil on 2014-04-02.
 */
public class ClientController extends BaseGameController implements NetworkListener {

    private Client mClient;

    private ClientSynchronizedUpdate mSyncedUpdate;

    public ClientController() {
        super(new PlayerInput());
        mClient = Snaek.newClient(4455, 5544, "localhost");
        mWorldModel = new ClientWorldModel(mClient);
        mSyncedUpdate = new ClientSynchronizedUpdate(mWorldModel);
        mClient.addListener(mSyncedUpdate);

        mClient.sendTcp(new RequestInitialStatePacket(mClient.getId()));
        mWorldView = new WorldView(mWorldModel);

        EventHandler.events.listen(NetworkEvent.class, this);
    }

    public void update(float delta) {
        super.update(delta);

        // Network stuff
        mSyncedUpdate.update();
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
                // Check against the rules in the model
                // mClientWorldModel.getPlayer().grenadeThrow();
                // Send to server
                mClient.sendTcp(new GrenadeCreatePacket(mClient.getId(),
                        mWorldModel.getPlayer().getPosition(),
                        mWorldModel.getPlayer().getDirection()));
                break;
        }
    }

    @Override
    public void modelEvent(EventType type, Object... objs) {
        super.modelEvent(type, objs);
        switch (type) {
            case SHOOT:
                RayVector ray = mWorldView.getCamera().getShootRay();
                Vector3[] visual = mWorldView.getCamera().getVisualRepresentationShoot();

                mWorldModel.checkShootCollision(ray);
                mWorldView.shoot(visual);
                // The network, notify the server that we have shot
                mClient.sendTcp(new ShootPacket(mClient.getId(), ray.getFrom(), ray.getTo(), visual[0], visual[1], visual[2], visual[3]));
                break;

            case BULLET_HOLE:
                BulletHoleRenderer.addBullethole((Vector3) objs[0], (Vector3) objs[1]);
                break;

            case GRENADE_CREATED:
                mWorldView.addGrenade((Grenade) objs[0]);
                break;

            case PLAYER_MOVE:
                mClient.sendUdp(new PlayerMovePacket(mClient.getId(),
                        mWorldModel.getPlayer().getPosition(),
                        mWorldModel.getPlayer().getRotation()));
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
            mWorldView.shoot(new Vector3[]{sp.vFrom, sp.vTo, sp.vFrom2, sp.vTo2});
        }
        else if(packet instanceof BulletHolePacket) {
            BulletHolePacket bhp = (BulletHolePacket) packet;
            // Draw bullethole
            BulletHoleRenderer.addBullethole(bhp.hitWorld, bhp.hitNormal);
        }
        else if(packet instanceof PlayerPacket) {
            PlayerPacket pp = (PlayerPacket)packet;
            mWorldView.addPlayer(((INetworkWorldModel) mWorldModel).getExternalPlayer(pp.clientId));
        }
    }
}
