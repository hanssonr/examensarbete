package se.rhel.screen.scene;

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

/**
 * Created by Emil on 2014-02-18.
 * assigned to libgdx-gradle-template in se.rhel.screen.scene
 */
public class MainMenu extends BaseScreen {

    private Stage mStage;
    private Skin mSkin;

    public MainMenu(CodeName game) {
        super(game);

        mStage = new Stage();
        mSkin = new Skin(Gdx.files.internal("data/skins/uiskin.json"));

        Gdx.input.setInputProcessor(mStage);
        initStage();
    }

    private void initStage() {
        Table table = new Table();
        table.setFillParent(true);
        mStage.addActor(table);

        final TextButton button = new TextButton("Clickme", mSkin);
        button.setWidth(200f);
        button.setHeight(20f);
        // button.setPosition(Gdx.graphics.getWidth() /2 - 100f, Gdx.graphics.getHeight()/2 - 10f);

        table.add(button);

        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("We", " are here");
            }
        });

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
    }

    @Override
    public void dispose() {
        super.dispose();
        Gdx.input.setInputProcessor(null);
        mStage.dispose();
        mSkin.dispose();
    }
}
