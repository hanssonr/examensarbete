package se.rhel.screen.network;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import se.rhel.client.Client;
import se.rhel.CodeName;
import se.rhel.network.MyPacketRegisterInitializer;
import se.rhel.network.PlayerPacket;
import se.rhel.network.RequestInitialStatePacket;
import se.rhel.packet.PacketManager;
import se.rhel.packet.PacketRegisterInitializer;
import se.rhel.screen.BaseScreen;
import se.rhel.server.Server;
import se.rhel.controller.PlayerController;
import se.rhel.model.client.ClientWorldModel;
import se.rhel.model.server.ServerWorldModel;
import se.rhel.view.WorldView;

/**
 * Group: Mixed
 */
public class NetworkGameScreen extends BaseScreen {

    private PlayerController mPlayerController;
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

        MyPacketRegisterInitializer.register();

        // else/and assume client
        mClient = new Client();
        mClient.start();
        mClient.connect("localhost", 4455, 5544);
        //mClient.connect("192.168.0.101", 4455, 5544);

        mClientWorldModel = ClientWorldModel.newNetworkWorld(mClient);

        mPlayerController = new PlayerController(mClientWorldModel.getCamera(), mClientWorldModel);
        mWorldView = new WorldView(mClientWorldModel);

        Gdx.input.setInputProcessor(mPlayerController);
    }


    @Override
    public void update(float delta) {
        mPlayerController.processCurrentInput(delta);
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
