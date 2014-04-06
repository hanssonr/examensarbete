package se.rhel.screen.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import se.rhel.Client;
import se.rhel.Snaek;
import se.rhel.event.*;
import se.rhel.model.client.ClientWorldModel;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.model.ClientSynchronizedUpdate;
import se.rhel.network.packet.*;
import se.rhel.observer.ClientListener;
import se.rhel.packet.Packet;
import se.rhel.view.BulletHoleRenderer;
import se.rhel.view.ParticleRenderer;
import se.rhel.view.WorldView;
import se.rhel.view.input.PlayerInput;

/**
 * Group: Mixed
 * Created by Emil on 2014-04-02.
 */
public class ClientController implements ViewListener, ModelListener, NetworkListener {

    private PlayerInput mPlayerInput;
    private WorldView mWorldView;

    private Client mClient;
    private ClientWorldModel mClientWorldModel;
    private Vector3 mLastKnownPosition = Vector3.Zero;
    private float mLastKnownRotation = 0f;

    private ClientSynchronizedUpdate mSyncedUpdate;

    public ClientController() {
        mClient = Snaek.newClient(4455, 5544, "localhost");
        mClientWorldModel = new ClientWorldModel(mClient);
        mSyncedUpdate = new ClientSynchronizedUpdate(mClientWorldModel);
        mClient.addListener(mSyncedUpdate);

        mClient.sendTcp(new RequestInitialStatePacket(mClient.getId()));

        mWorldView = new WorldView(mClientWorldModel);
        mPlayerInput = new PlayerInput();

        // Listen to network events
        EventHandler.events.listen(NetworkEvent.class, this);
        // Listen to view events
        EventHandler.events.listen(ViewEvent.class, this);
        // Listen to model events
        EventHandler.events.listen(ModelEvent.class, this);

        Gdx.input.setInputProcessor(mPlayerInput);
    }

    public void update(float delta) {
        mSyncedUpdate.update();
        mPlayerInput.processCurrentInput(delta);

        mClientWorldModel.getPlayer().move(mPlayerInput.getDirection());
        mClientWorldModel.getPlayer().rotate(mPlayerInput.getRotation());

        mClientWorldModel.update(delta);

        // Network stuff
        if(mClient.getId() != -1) {
            // Send move packet

            if(mClientWorldModel.getPlayer().getPosition().dst(mLastKnownPosition) > 0.01f ||
                    mClientWorldModel.getPlayer().getRotation().x != mLastKnownRotation)  {
                mLastKnownPosition = mClientWorldModel.getPlayer().getPosition().cpy();
                mLastKnownRotation = mClientWorldModel.getPlayer().getRotation().x;

                mClient.sendUdp(new PlayerMovePacket(mClient.getId(),
                                mClientWorldModel.getPlayer().getPosition(),
                                mClientWorldModel.getPlayer().getRotation()));
            }
        }

        mWorldView.update(delta);
    }

    public void draw(float delta) {
        mWorldView.render(delta);
    }

    public void dispose() {
        mWorldView.dispose();
    }

    @Override
    public void modelEvent(EventType type, Object... objs) {
        switch (type) {

            case SHOOT:
                Vector3[] collide = mWorldView.getCamera().getShootRay();
                Vector3[] visual = mWorldView.getCamera().getVisualRepresentationShoot();

                // The collision
                mClientWorldModel.checkShootCollision(collide);

                // The rendering & sound
                mWorldView.shoot(visual);

                // The network, notify the server that we have shot
                mClient.sendTcp(new ShootPacket(mClient.getId(), collide[0], collide[1], visual[0], visual[1], visual[2], visual[3]));
                break;

            case BULLET_HOLE:
                BulletHoleRenderer.addBullethole((Vector3) objs[0], (Vector3) objs[1]);
                break;

            case GRENADE_CREATED:
                mWorldView.getGrenadeRenderer().addGrenade((Grenade)objs[0]);
                break;

            case EXPLOSION:
                mWorldView.getParticleRenderer().addEffect(((Grenade)objs[0]).getPosition(), ParticleRenderer.Particle.EXPLOSION);
                break;

            case DAMAGE:
                mWorldView.getParticleRenderer().addEffect(((DamageAbleEntity)objs[0]).getPosition(), ParticleRenderer.Particle.BLOOD);
                break;
            default:
                break;
        }
    }

    @Override
    public void inputEvent(EventType type) {
        switch (type) {
            case JUMP:
                mClientWorldModel.getPlayer().jump();
                break;
            case SHOOT:
                // Just notify the model
                mClientWorldModel.getPlayer().shoot();
                break;
            case GRENADE:
                // Check against the rules in the model
                // mClientWorldModel.getPlayer().grenadeThrow();
                // Send to server
                mClient.sendTcp(new GrenadeCreatePacket(mClient.getId(),
                        mClientWorldModel.getPlayer().getPosition(),
                        mClientWorldModel.getPlayer().getDirection()));
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
            mWorldView.getExternalPlayerRenderer().addPlayerAnimation(mClientWorldModel.getExternalPlayer(pp.clientId));
        }
    }
}
