package se.rhel.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import se.rhel.CodeName;
import se.rhel.screen.scene.UIComponents;
import se.rhel.screen.scene.network.NetworkGameScreen;
import se.rhel.server.Server;


/**
 * Group: Mixed
 *
 * Created by Emil on 2014-03-05.
 * assigned to libgdx-gradle-template in se.rhel.screen.scene
 */
public class LobbyScreen extends BaseScreen {

    private boolean mIsHost;
    private Stage mStage;
    private Table mTable;

    private CodeName mGame;
    private Server mServer;

    public LobbyScreen(CodeName game, boolean isHost) {
        super(game);

        mIsHost = isHost;
        mStage = new Stage();
        mGame = game;

        // Start the server if host
        if(isHost) {
            mServer = new Server();
            mServer.start();
            mServer.bind(4455, 5544);
        }

    }

    private void initStage() {

        mTable.clear();
        final TextButton startButton = UIComponents.getDefaultTextButton("Start game", 200f, 20f);

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mGame.setScreen(new NetworkGameScreen(mGame, mServer));
            }
        });

        mTable.row().padTop(10);
        mTable.add(startButton);
        mTable.row().padTop(10);


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
