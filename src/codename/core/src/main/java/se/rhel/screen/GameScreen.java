package se.rhel.screen;

import com.badlogic.gdx.Gdx;
import se.rhel.CodeName;
import se.rhel.controller.PlayerController;
import se.rhel.model.WorldModel;
import se.rhel.view.WorldView;


public class GameScreen extends BaseScreen {

    private WorldModel mWorldModel;
    private PlayerController mPlayerController;
    private WorldView mWorldView;

    public GameScreen(CodeName game) {
        super(game);

        mWorldModel = new WorldModel();
        //mPlayerController = new PlayerController(mWorldModel.getCamera(), mWorldModel.getPlayer());
        mWorldView = new WorldView();

        //Gdx.input.setInputProcessor(mPlayerController);
    }


    @Override
    public void update(float delta) {
        //mPlayerController.processCurrentInput();
        mWorldModel.update(delta);
        mWorldView.update(delta);
    }

    @Override
    public void draw(float delta) {
        mWorldView.render();
    }
}
