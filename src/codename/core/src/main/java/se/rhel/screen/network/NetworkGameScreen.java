package se.rhel.screen.network;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import se.rhel.client.Client;
import se.rhel.CodeName;
import se.rhel.network.packet.PlayerMovePacket;
import se.rhel.screen.BaseScreen;
import se.rhel.server.Server;
import se.rhel.view.input.PlayerInput;
import se.rhel.model.client.ClientWorldModel;
import se.rhel.model.server.ServerWorldModel;
import se.rhel.view.WorldView;

/**
 * Group: Mixed
 */
public class NetworkGameScreen extends BaseScreen {

    private PlayerInput mPlayerInput;
    private WorldView mWorldView;

    private Server mServer;
    private ServerWorldModel mServerWorldModel;
    private boolean mUpdateServer = false;

    private Client mClient;
    private ClientWorldModel mClientWorldModel;
    private Vector3 mLastKnownPosition = Vector3.Zero;

    public NetworkGameScreen(CodeName game, Server server) {
        super(game);
        Gdx.app.setLogLevel(Application.LOG_NONE);
        mServer = server;

        if(mServer != null) {
            mServerWorldModel = new ServerWorldModel(server);
            mUpdateServer = true;
        }

        // else/and assume client
        mClient = new Client();
        mClient.start();
        mClient.connect("localhost", 4455, 5544);
        //mClient.connect("192.168.0.101", 4455, 5544);

        mClientWorldModel = ClientWorldModel.newNetworkWorld(mClient);

        mPlayerInput = new PlayerInput();
        mWorldView = new WorldView(mClientWorldModel);

        Gdx.input.setInputProcessor(mPlayerInput);
    }


    @Override
    public void update(float delta) {
        mPlayerInput.processCurrentInput(delta);

        mClientWorldModel.getPlayer().rotate(mPlayerInput.getRotation());
        mClientWorldModel.getPlayer().move(mPlayerInput.getDirection());

        if (mPlayerInput.isShooting())
            mClientWorldModel.getPlayer().shoot();

        if (mPlayerInput.isJumping())
            mClientWorldModel.getPlayer().jump();

        mClientWorldModel.update(delta);
        if(mUpdateServer) mServerWorldModel.update(delta);

        // Network stuff
        if(mClient.getId() != -1) {
            // Send move packet
            if(mClientWorldModel.getPlayer().getPosition().dst(mLastKnownPosition) > 0.01f) {
                mClient.sendUdp(new PlayerMovePacket(mClient.getId(), mClientWorldModel.getPlayer().getPosition().x, mClientWorldModel.getPlayer().getPosition().y, mClientWorldModel.getPlayer().getPosition().z));
                mLastKnownPosition = mClientWorldModel.getPlayer().getPosition().cpy();
            }
        }
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
}
