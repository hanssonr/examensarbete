package se.rhel.screen.scene;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import se.rhel.CodeName;
import se.rhel.screen.BaseScreen;
import se.rhel.screen.GameScreen;

/**
 * Created by Emil on 2014-02-18.
 * assigned to libgdx-gradle-template in se.rhel.screen.scene
 */
public class MainMenu extends BaseScreen {

    private Stage mStage;
    private Table mTable;

    private CodeName mGame;

    public MainMenu(CodeName game) {
        super(game);

        mStage = new Stage();
        mGame = game;
    }

    private void initStage() {

        mTable.clear();

        final TextButton optButton = UIComponents.getDefaultTextButton("Options", 200f, 20f);
        // button.setPosition(Gdx.graphics.getWidth() /2 - 100f, Gdx.graphics.getHeight()/2 - 10f);
        final TextButton startButton = UIComponents.getDefaultTextButton("Start", 200f, 20f);

        optButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // mGame.setScreenWithTransition(new OptionsMenu(mGame));
                Gdx.app.log("Button", "Opt");
                mGame.setScreen(new OptionsMenu(mGame));
            }
        });

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("Button", "Game");
                // mGame.setScreenWithTransition(new GameScreen(mGame));
                mGame.setScreen(new GameScreen(mGame));
            }
        });

        mTable.add(optButton);
        mTable.row().padTop(10);
        mTable.add(startButton);
        mStage.addActor(mTable);

    }

    @Override
    public void show() {
        mTable = new Table();
        mTable.setFillParent(true);

        Gdx.input.setInputProcessor(mStage);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void update(float delta) {
        mStage.act(delta);
    }

    @Override
    public void draw(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        mStage.draw();
        Table.drawDebug(mStage);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        mStage.setViewport(width, height, true);
        initStage();
    }

    @Override
    public void dispose() {
        super.dispose();
        Gdx.input.setInputProcessor(null);
        mStage.dispose();
    }
}
