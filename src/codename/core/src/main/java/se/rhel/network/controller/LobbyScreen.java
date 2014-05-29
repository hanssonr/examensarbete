package se.rhel.network.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import se.rhel.CodeName;
import se.rhel.Snaek;
import se.rhel.network.packet.MyPacketRegisterInitializer;
import se.rhel.screen.AbstactController;
import se.rhel.screen.scene.UIComponents;
import se.rhel.Server;


/**
 * Group: Mixed
 *
 * Created by Emil on 2014-03-05.
 * assigned to libgdx-gradle-template in se.rhel.screen.scene
 */
public class LobbyScreen extends AbstactController {

    private boolean mIsHost;
    private Stage mStage;
    private Table mTable;

    private CodeName mGame;
    private Server mServer;

    private String mErrorMessage = "";

    public LobbyScreen(CodeName game, boolean isHost) {
        super(game);

        mIsHost = isHost;
        mStage = new Stage();
        mGame = game;

        MyPacketRegisterInitializer.register();

        //Start the server if host
        if(isHost) {
            mServer = Snaek.newServer(4455, 5544);
        }
    }

    public LobbyScreen(CodeName game, boolean isHost, String error) {
        this(game, isHost);

        mErrorMessage = error;
    }

    private void initStage() {

        mTable.clear();
        final TextField hostField = UIComponents.getDefaultTextField("IP to host");
        final TextButton startButton = UIComponents.getDefaultTextButton("Start game", 200f, 20f);
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String host = "localhost";
                if(!mIsHost && !hostField.getText().isEmpty()) host = hostField.getText();
                mGame.setScreen(new NetworkGameScreen(mGame, mServer, host));
            }
        });

        if (!mIsHost) {
            mTable.row().padTop(10);
            mTable.add(hostField);
        }

        mTable.row().padTop(10);
        mTable.add(startButton);
        mTable.row().padTop(10);

        if(mIsHost) {
            mTable.row().padTop(10);
            mTable.add(UIComponents.getDefaultLabel("Your IP: " + mServer.getHostAddress()));
        }
        if(!mErrorMessage.isEmpty()) {
            mTable.row().padTop(10);
            mTable.add(UIComponents.getErrorLabel(mErrorMessage));
        }

        mStage.addActor(mTable);
    }

    @Override
    public void show() {
        mTable = new Table();
        mTable.setFillParent(true);

        Gdx.input.setCursorCatched(false);
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