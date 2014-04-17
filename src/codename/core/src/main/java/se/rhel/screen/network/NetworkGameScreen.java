package se.rhel.screen.network;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import se.rhel.CodeName;
import se.rhel.screen.BaseScreen;
import se.rhel.Server;

/**
 * Group: Mixed
 */
public class NetworkGameScreen extends BaseScreen { // N

    private ClientController mClientController; // L
    private ServerController mServerController; // N

    private boolean mUpdateServer = false; // N

    public NetworkGameScreen(CodeName game, Server server) { // N
        super(game); // L
        Gdx.app.setLogLevel(Application.LOG_NONE); // L

        if(server != null) { // N
            mUpdateServer = true; // N
            mServerController = new ServerController(server); // N
        } // N

        mClientController = new ClientController(); // L
    } // N

    @Override // L
    public void update(float delta) { // L
        mClientController.update(delta); // L
        if(mUpdateServer) mServerController.update(delta); // N

    } // L

    @Override // L
    public void draw(float delta) { // L
        mClientController.draw(delta); // L
    } // L

    @Override // L
    public void dispose() { // L
        super.dispose(); // L
        mClientController.dispose(); // L
    } // L
} // N
