package se.rhel.network.controller;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import se.rhel.Client;
import se.rhel.CodeName;
import se.rhel.Snaek;
import se.rhel.screen.AbstactController;
import se.rhel.Server;
import se.rhel.screen.scene.MainMenu;
import se.rhel.screen.scene.OptionsMenu;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Group: Multiplayer
 */
public class NetworkGameScreen extends AbstactController {

    private ClientController mClientController;
    private ServerController mServerController;

    private boolean mUpdateServer = false;
    private boolean mInitialized = true;

    public NetworkGameScreen(CodeName game, Server server, String host) {
        super(game);
        Gdx.app.setLogLevel(Application.LOG_NONE);

        if(server != null) {
            mUpdateServer = true;
            mServerController = new ServerController(server);
        }

        try {
            mClientController = new ClientController(host);
        } catch (Exception e) {
            mInitialized = false;
            //getGame().setScreen(new MainMenu(getGame()));
        }
    }

    @Override
    public void update(float delta) {
        if(mInitialized) {
            mClientController.update(delta);
            if(mUpdateServer) mServerController.update(delta);
        } else {
            getGame().setScreen(new LobbyScreen(getGame(), false, "No server found..."));
        }
    }

    @Override
    public void draw(float delta) {
        if(mInitialized)
            mClientController.draw(delta);

    }

    @Override
    public void hide() {
        super.hide();
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        if(mInitialized) mClientController.dispose();
        Gdx.input.setInputProcessor(null);
    }
}
