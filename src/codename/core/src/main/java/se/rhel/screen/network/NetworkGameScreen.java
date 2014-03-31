package se.rhel.screen.network;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import se.rhel.Client;
import se.rhel.CodeName;
import se.rhel.Snaek;
import se.rhel.event.*;
import se.rhel.network.packet.BulletHolePacket;
import se.rhel.network.packet.PlayerMovePacket;
import se.rhel.network.packet.PlayerPacket;
import se.rhel.network.packet.ShootPacket;
import se.rhel.packet.Packet;
import se.rhel.screen.BaseScreen;
import se.rhel.Server;
import se.rhel.view.BulletHoleRenderer;
import se.rhel.view.input.PlayerInput;
import se.rhel.model.client.ClientWorldModel;
import se.rhel.model.server.ServerWorldModel;
import se.rhel.view.WorldView;

/**
 * Group: Mixed
 */
public class NetworkGameScreen extends BaseScreen implements ViewListener, ModelListener, NetworkListener {

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
        }
    }

    @Override
    public void playerEvent(EventType type) {
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
    }
}
