package se.rhel.screen.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import se.rhel.CodeName;
import se.rhel.screen.BaseScreen;
import se.rhel.utils.Options;

/**
 * Group: Logic
 *
 * Created by Emil on 2014-02-19.
 * assigned to libgdx-gradle-template in se.rhel.screen.scene
 */
public class OptionsMenu extends BaseScreen {

    private Stage mStage;
    private Table mTable;

    private CodeName mGame;

    public OptionsMenu(CodeName game) {
        super(game);

        mStage = new Stage();
        mGame = game;
    }

    private void initStage() {

        mTable.clear();

        final CheckBox fullScreenCheck = UIComponents.getDefaultCheckBox("Fullscreen");
        fullScreenCheck.setChecked(Options.INSTANCE.getFullScreen());

        final TextButton backButton = UIComponents.getDefaultTextButton("Back", 200f, 20f);

        fullScreenCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if(fullScreenCheck.isChecked()) {
                    // If we are not already in fullscreen
                    if(!Gdx.graphics.isFullscreen()) {
                        Gdx.graphics.setDisplayMode(1280, 720, true);
                    }
                } else {
                    if(Gdx.graphics.isFullscreen()) {
                        Gdx.graphics.setDisplayMode(1280, 720, false);
                    }
                }

                // Save either way
                Options.INSTANCE.setFullScreen(fullScreenCheck.isChecked());
                // Might do this in bulk later
                Options.INSTANCE.save();
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
               back();
            }
        });

        mTable.add(fullScreenCheck);
        mTable.row().padTop(10);
        mTable.add(backButton);
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



    private void back() {
        mGame.setScreen(new MainMenu(mGame));
    }
}
