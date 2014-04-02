package se.rhel.screen.network;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import se.rhel.Client;
import se.rhel.CodeName;
import se.rhel.Snaek;
import se.rhel.event.*;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.packet.*;
import se.rhel.packet.Packet;
import se.rhel.screen.BaseScreen;
import se.rhel.Server;
import se.rhel.screen.Controller;
import se.rhel.view.BulletHoleRenderer;
import se.rhel.view.ParticleRenderer;
import se.rhel.view.input.PlayerInput;
import se.rhel.model.client.ClientWorldModel;
import se.rhel.model.server.ServerWorldModel;
import se.rhel.view.WorldView;

/**
 * Group: Mixed
 */
public class NetworkGameScreen extends Controller implements NetworkListener, ServerModelListener {

    private PlayerInput mPlayerInput;
    private WorldView mWorldView;

    private Server mServer;
    private ServerWorldModel mServerWorldModel;
    private boolean mUpdateServer = false;

    private Client mClient;
    private ClientWorldModel mClientWorldModel;
    private Vector3 mLastKnownPosition = Vector3.Zero;
    private float mLastKnownRotation = 0f;

    public NetworkGameScreen(CodeName game, Server server) {
        super(game);
        Gdx.app.setLogLevel(Application.LOG_NONE);

        mServer = server;

        if(mServer != null) {
            mServerWorldModel = new ServerWorldModel(server);
            mUpdateServer = true;
        }

        mClient = Snaek.newClient(4455, 5544, "localhost");
        mClientWorldModel = new ClientWorldModel(mClient);

        mPlayerInput = new PlayerInput();

        // Listen to view events
        EventHandler.events.listen(ViewEvent.class, this);
        // Listen to model events
        EventHandler.events.listen(ModelEvent.class, this);
        // Listen to network events
        EventHandler.events.listen(NetworkEvent.class, this);
        EventHandler.events.listen(ServerModelEvent.class, this);

        mWorldView = new WorldView(mClientWorldModel);

        Gdx.input.setInputProcessor(mPlayerInput);
    }


    @Override
    public void update(float delta) {
        mPlayerInput.processCurrentInput(delta);

        mClientWorldModel.getPlayer().move(mPlayerInput.getDirection());
        mClientWorldModel.getPlayer().rotate(mPlayerInput.getRotation());

        mWorldView.getCamera().rotate(mPlayerInput.getRotation());

        mClientWorldModel.update(delta);
        if(mUpdateServer) mServerWorldModel.update(delta);

        // Network stuff
        if(mClient.getId() != -1) {
            // Send move packet

            if(mClientWorldModel.getPlayer().getPosition().dst(mLastKnownPosition) > 0.01f ||
                    mClientWorldModel.getPlayer().getRotation() != mLastKnownRotation)  {
                mLastKnownPosition = mClientWorldModel.getPlayer().getPosition().cpy();
                mLastKnownRotation = mClientWorldModel.getPlayer().getRotation();

                mClient.sendUdp(
                        new PlayerMovePacket(mClient.getId(),
                        mClientWorldModel.getPlayer().getPosition(), mClientWorldModel.getPlayer().getRotation()));
            }
        }

        mWorldView.update(delta);
    }

    @Override
    public void draw(float delta) {
        mWorldView.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();

        mWorldView.dispose();
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
                mClient.sendTcp(new GrenadeCreatePacket(mClient.getId(), new Vector3(), new Vector3()));
                break;
        }
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
                MyContactListener.checkExplosionCollision(mClientWorldModel.getBulletWorld().getCollisionWorld(), ((Grenade) objs[0]).getPosition(), 10f);
                mWorldView.getParticleRenderer().addEffect(((Grenade)objs[0]).getPosition(), ParticleRenderer.Particle.EXPLOSION);
                break;

            case DAMAGE:
                System.out.println("DAMAGE EVENT");
                mWorldView.getParticleRenderer().addEffect(((DamageAbleEntity)objs[0]).getPosition(), ParticleRenderer.Particle.BLOOD);
                break;

            default:
                break;
        }
    }

    @Override
    public void serverModelEvent(EventType type, Object... objs) {
        switch (type) {
            case GRENADE:
                // Now it should be alright to throw a grenade
                Vector3 pos = (Vector3) objs[0];
                Vector3 dir = (Vector3) objs[1];

                // A player has thrown from the model
                Grenade g = mServerWorldModel.addGrenade(pos, dir);
                mServer.sendToAllTCP(new GrenadeCreatePacket(g.getId(), pos, dir));
                // mClient.sendTcp(new GrenadeCreatePacket(g.getId(), pos, dir));

                // mClientWorldModel.addGrenade(pos, dir);
                // mWorldView.getGrenadeRenderer().addGrenade(mClientWorldModel.getGrenades().get(mClientWorldModel.getGrenades().size()-1));
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
            mWorldView.getExternalPlayerRenderer().addPlayerAnimation(mClientWorldModel.getExternalPlayer(pp.clientId));
        }
        else if(packet instanceof GrenadeCreatePacket) {
            GrenadeCreatePacket gcp = (GrenadeCreatePacket) packet;
            System.out.println("GotGrenade? id: " + gcp.clientId);
            // Should create a grenade on the client
            Grenade g = mClientWorldModel.addGrenade(gcp.position, gcp.direction);
            g.setId(gcp.clientId);
        }
    }


}
