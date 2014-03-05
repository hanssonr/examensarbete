package se.rhel.screen;

import com.badlogic.gdx.Gdx;
import se.rhel.Client;
import se.rhel.CodeName;
import se.rhel.Server;
import se.rhel.controller.PlayerController;
import se.rhel.model.client.ClientWorldModel;
import se.rhel.model.server.ServerWorldModel;
import se.rhel.view.WorldView;


public class GameScreen extends BaseScreen {


    private PlayerController mPlayerController;
    private WorldView mWorldView;

    private Server mServer;
    private ServerWorldModel mServerWorldModel;

    private Client mClient;
    private ClientWorldModel mClientWorldModel;

    public GameScreen(CodeName game, Server server, Client client) {
        super(game);

        mServer = server;
        mClient = client;

        mClientWorldModel = new ClientWorldModel(client);

        if(mServer != null) {
            mServerWorldModel = new ServerWorldModel(server);
        }

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
