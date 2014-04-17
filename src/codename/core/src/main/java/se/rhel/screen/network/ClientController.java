package se.rhel.screen.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import se.rhel.Client;
import se.rhel.Snaek;
import se.rhel.event.*;
import se.rhel.model.client.ClientWorldModel;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.packet.*;
import se.rhel.packet.Packet;
import se.rhel.view.BulletHoleRenderer;
import se.rhel.view.ParticleRenderer;
import se.rhel.view.WorldView;
import se.rhel.view.input.PlayerInput;

/**
 * Group: Mixed
 * Created by Emil on 2014-04-02.
 */
public class ClientController implements ViewListener, ModelListener, NetworkListener { // L

    private PlayerInput mPlayerInput; // L
    private WorldView mWorldView; // L

    private Client mClient;                                                                                             // N
    private ClientWorldModel mClientWorldModel;                                                                         // N
    private Vector3 mLastKnownPosition = Vector3.Zero; // L
    private float mLastKnownRotation = 0f; // L

    private ClientSynchronizedUpdate mSyncedUpdate;                                                                     // N

    public ClientController() { // L
        mClient = Snaek.newClient(4455, 5544, "localhost");                                                             // N
        mClientWorldModel = new ClientWorldModel(mClient);                                                              // N
        mSyncedUpdate = new ClientSynchronizedUpdate(mClientWorldModel);                                                // N
        mClient.addListener(mSyncedUpdate);                                                                             // N

        mClient.sendTcp(new RequestInitialStatePacket(mClient.getId()));                                                // N

        mWorldView = new WorldView(mClientWorldModel);                                                                  // N
        mPlayerInput = new PlayerInput(); // L

        // Listen to network events
        EventHandler.events.listen(NetworkEvent.class, this);                                                           // N?
        // Listen to view events
        EventHandler.events.listen(ViewEvent.class, this); // L
        // Listen to model events
        EventHandler.events.listen(ModelEvent.class, this); // L

        Gdx.input.setInputProcessor(mPlayerInput); // L
    } // L

    public void update(float delta) { // L
        mSyncedUpdate.update(); // L
        mPlayerInput.processCurrentInput(delta); // L

        mClientWorldModel.getPlayer().move(mPlayerInput.getDirection()); // L
        mClientWorldModel.getPlayer().rotate(mPlayerInput.getRotation()); // L

        mClientWorldModel.update(delta); // L

        // Network stuff
        if(mClient.getId() != -1) {                                                                                     // N
            // Send move packet

            if(mClientWorldModel.getPlayer().getPosition().dst(mLastKnownPosition) > 0.01f || // L
                    mClientWorldModel.getPlayer().getRotation().x != mLastKnownRotation)  { //L
                mLastKnownPosition = mClientWorldModel.getPlayer().getPosition().cpy(); // L
                mLastKnownRotation = mClientWorldModel.getPlayer().getRotation().x; // L

                mClient.sendUdp(new PlayerMovePacket(mClient.getId(),                                                   // N
                                mClientWorldModel.getPlayer().getPosition(),                                            // N
                                mClientWorldModel.getPlayer().getRotation()));                                          // N
            } // L
        }                                                                                                               // N

        mWorldView.update(delta); // L
    } // L

    public void draw(float delta) { // L
        mWorldView.render(delta); // L
    } // L

    public void dispose() { // L
        mWorldView.dispose(); // L
    } // L

    @Override // L
    public void modelEvent(EventType type, Object... objs) { // L
        switch (type) { // L
            case SHOOT: // L
                Vector3[] collide = mWorldView.getCamera().getShootRay(); // L
                Vector3[] visual = mWorldView.getCamera().getVisualRepresentationShoot(); // L

                // The collision
                mClientWorldModel.checkShootCollision(collide); // L

                // The rendering & sound
                mWorldView.shoot(visual); // L

                // The network, notify the server that we have shot
                mClient.sendTcp(new ShootPacket(mClient.getId(), collide[0], collide[1], visual[0], visual[1], visual[2], visual[3])); // N
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
    } // L

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
                mClient.sendTcp(new GrenadeCreatePacket(mClient.getId(),                                                        // N
                        mClientWorldModel.getPlayer().getPosition(),                                                            // N
                        mClientWorldModel.getPlayer().getDirection()));                                                         // N
                break;
        }
    }

    @Override                                                                                                                   // N
    public void networkEvent(Packet packet) {                                                                                   // N

        if(packet instanceof ShootPacket) {                                                                                     // N
            ShootPacket sp = (ShootPacket) packet;                                                                              // N

            // The rendering and sound
            mWorldView.shoot(new Vector3[]{sp.vFrom, sp.vTo, sp.vFrom2, sp.vTo2}); // L
        }                                                                                                                       // N
        else if(packet instanceof BulletHolePacket) {                                                                           // N
            BulletHolePacket bhp = (BulletHolePacket) packet;                                                                   // N
            // Draw bullethole
            BulletHoleRenderer.addBullethole(bhp.hitWorld, bhp.hitNormal); // L
        }                                                                                                                       // N
        else if(packet instanceof PlayerPacket) {                                                                               // N
            PlayerPacket pp = (PlayerPacket)packet;                                                                             // N
            mWorldView.getExternalPlayerRenderer().addPlayerAnimation(mClientWorldModel.getExternalPlayer(pp.clientId)); // L
        }                                                                                                                       // N
    }                                                                                                                           // N
} // L
