package se.rhel.screen.network;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import se.rhel.Client;
import se.rhel.CodeName;
import se.rhel.Snaek;
import se.rhel.model.FPSCamera;
import se.rhel.network.packet.PlayerMovePacket;
import se.rhel.screen.BaseScreen;
import se.rhel.Server;
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

            if(mClientWorldModel.getPlayer().getPosition().dst(mLastKnownPosition) > 0.01f ||
                    mClientWorldModel.getPlayer().getRotation().getAxisAngle(FPSCamera.UP.cpy()) != mLastKnownRotation)  {
                mLastKnownPosition = mClientWorldModel.getPlayer().getPosition().cpy();
                mLastKnownRotation = mClientWorldModel.getPlayer().getRotation().getAxisAngle(FPSCamera.UP.cpy());

                mClient.sendUdp(
                        new PlayerMovePacket(mClient.getId(),
                                mClientWorldModel.getPlayer().getPosition().x, mClientWorldModel.getPlayer().getPosition().y, mClientWorldModel.getPlayer().getPosition().z,
                                mClientWorldModel.getPlayer().getRotation().y, mClientWorldModel.getPlayer().getRotation().w));
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
