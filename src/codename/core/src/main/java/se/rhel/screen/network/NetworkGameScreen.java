package se.rhel.screen.network;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import se.rhel.client.Client;
import se.rhel.CodeName;
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

    private Client mClient;
    private ClientWorldModel mClientWorldModel;

    public NetworkGameScreen(CodeName game, Server server) {
        super(game);
        Gdx.app.setLogLevel(Application.LOG_NONE);
        mServer = server;

        if(mServer != null) {
            mServerWorldModel = new ServerWorldModel(server);
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