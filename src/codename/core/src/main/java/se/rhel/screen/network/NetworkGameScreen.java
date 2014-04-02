package se.rhel.screen.network;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import se.rhel.CodeName;
import se.rhel.screen.BaseScreen;
import se.rhel.Server;

/**
 * Group: Mixed
 */
public class NetworkGameScreen extends BaseScreen {

    private ClientController mClientController;
    private ServerController mServerController;

    private boolean mUpdateServer = false;

    public NetworkGameScreen(CodeName game, Server server) {
        super(game);
        Gdx.app.setLogLevel(Application.LOG_NONE);

        if(server != null) {
            mUpdateServer = true;
            mServerController = new ServerController(server);
        }

        mClientController = new ClientController();
    }

    @Override
    public void update(float delta) {
        mClientController.update(delta);
        if(mUpdateServer) mServerController.update(delta);

    }

    @Override
    public void draw(float delta) {
        mClientController.draw(delta);
    }

    @Override
    public void dispose() {
        super.dispose();
        mClientController.dispose();
    }
}
