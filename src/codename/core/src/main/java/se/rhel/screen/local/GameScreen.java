package se.rhel.screen.local;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import se.rhel.CodeName;
import se.rhel.controller.PlayerController;
import se.rhel.model.client.ClientWorldModel;
import se.rhel.screen.BaseScreen;
import se.rhel.view.WorldView;

/**
 * Group: Logic
 *
 * Created by Emil on 2014-03-10.
 * assigned to libgdx-gradle-template in se.rhel.screen.local
 */
public class GameScreen extends BaseScreen {

    private PlayerController mPlayerController;
    private WorldView mWorldView;

    private ClientWorldModel mClientWorldModel;

    public GameScreen(CodeName game) {
        super(game);
        Gdx.app.setLogLevel(Application.LOG_NONE);

        mClientWorldModel = ClientWorldModel.newLocalWorld();

        mPlayerController = new PlayerController(mClientWorldModel.getCamera(), mClientWorldModel);
        mWorldView = new WorldView(mClientWorldModel);

        Gdx.input.setInputProcessor(mPlayerController);
    }


    @Override
    public void update(float delta) {
        mPlayerController.processCurrentInput(delta);
        mClientWorldModel.update(delta);
        System.out.println("hajj");
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
