package se.rhel.screen.network;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import se.rhel.Client;
import se.rhel.CodeName;
import se.rhel.Snaek;
import se.rhel.event.*;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.physics.MyContactListener;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.packet.*;
import se.rhel.packet.Packet;
import se.rhel.screen.BaseScreen;
import se.rhel.Server;
import se.rhel.screen.Controller;
import se.rhel.view.BulletHoleRenderer;
import se.rhel.view.ParticleRenderer;
import se.rhel.view.input.PlayerInput;
import se.rhel.model.client.ClientWorldModel;
import se.rhel.model.server.ServerWorldModel;
import se.rhel.view.WorldView;

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
