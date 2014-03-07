package se.rhel.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import se.rhel.client.Client;
import se.rhel.CodeName;
import se.rhel.server.Server;
import se.rhel.controller.PlayerController;
import se.rhel.model.client.ClientWorldModel;
import se.rhel.model.server.ServerWorldModel;
import se.rhel.view.WorldView;

import java.io.IOException;


public class GameScreen extends BaseScreen {


    private PlayerController mPlayerController;
    private WorldView mWorldView;

    private Server mServer;
    private ServerWorldModel mServerWorldModel;

    private Client mClient;
    private ClientWorldModel mClientWorldModel;

    public GameScreen(CodeName game, Server server) {
        super(game);
        Gdx.app.setLogLevel(Application.LOG_NONE);
        mServer = server;

        if(mServer != null) {
            mServerWorldModel = new ServerWorldModel(server);
        }

        // else/and assume client
        mClient = new Client();
        try {
            mClient.connect("127.0.0.1", 4455);
            mClient.start();
        } catch (IOException e) {
            System.err.println("Could not connect to server");
        }

        mClientWorldModel = new ClientWorldModel(mClient);

        mPlayerController = new PlayerController(mClientWorldModel.getCamera(), mClientWorldModel.getPlayer());
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
