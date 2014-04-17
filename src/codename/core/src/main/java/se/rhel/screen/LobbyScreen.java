package se.rhel.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import se.rhel.CodeName;
import se.rhel.Snaek;
import se.rhel.network.packet.MyPacketRegisterInitializer;
import se.rhel.screen.scene.UIComponents;
import se.rhel.screen.network.NetworkGameScreen;
import se.rhel.Server;


/**
 * Group: Mixed
 *
 * Created by Emil on 2014-03-05.
 * assigned to libgdx-gradle-template in se.rhel.screen.scene
 */
public class LobbyScreen extends BaseScreen { // N

    private boolean mIsHost; // N
    private Stage mStage; // L
    private Table mTable; // L

    private CodeName mGame; // L
    private Server mServer; // N

    public LobbyScreen(CodeName game, boolean isHost) { // N
        super(game); // L

        mIsHost = isHost; // N
        mStage = new Stage(); // L
        mGame = game; // L

        MyPacketRegisterInitializer.register(); // N

        //Start the server if host
        if(isHost) { // N
            mServer = Snaek.newServer(4455, 5544); // N
        } // N
    } // N

    private void initStage() { // L

        mTable.clear(); // L
        final TextButton startButton = UIComponents.getDefaultTextButton("Start game", 200f, 20f); // L

        startButton.addListener(new ChangeListener() { // L
            @Override // L
            public void changed(ChangeEvent event, Actor actor) { // L
                mGame.setScreen(new NetworkGameScreen(mGame, mServer)); // L
            } // L
        }); // L

        mTable.row().padTop(10); // L
        mTable.add(startButton); // L
        mTable.row().padTop(10); // L


        mStage.addActor(mTable); // L

    } // L

    @Override // L
    public void show() { // L
        mTable = new Table(); // L
        mTable.setFillParent(true); // L

        Gdx.input.setInputProcessor(mStage); // L
        Gdx.input.setCatchBackKey(true); // L
    } // L

    @Override // L
    public void update(float delta) { // L
        mStage.act(delta); // L
    } // L

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
} // N