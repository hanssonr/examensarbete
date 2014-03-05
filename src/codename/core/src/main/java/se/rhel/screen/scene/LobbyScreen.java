package se.rhel.screen.scene;

import se.rhel.CodeName;
import se.rhel.screen.BaseScreen;

/**
 * Created by Emil on 2014-03-05.
 * assigned to libgdx-gradle-template in se.rhel.screen.scene
 */
public class LobbyScreen extends BaseScreen {

    private boolean mIsHost;

    public LobbyScreen(CodeName game, boolean isHost) {
        super(game);
        mIsHost = isHost;
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void draw(float delta) {

    }
}
