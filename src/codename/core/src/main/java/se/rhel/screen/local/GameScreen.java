package se.rhel.screen.local;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import se.rhel.CodeName;
import se.rhel.model.WorldModel;
import se.rhel.model.physics.MyContactListener;
import se.rhel.view.BulletHoleRenderer;
import se.rhel.view.input.PlayerInput;
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

    private PlayerInput mPlayerInput;
    private WorldView mWorldView;

    private WorldModel mWorldModel;

    public GameScreen(CodeName game) {
        super(game);
        Gdx.app.setLogLevel(Application.LOG_NONE);

        mWorldModel = new WorldModel();
        mWorldModel.create();

        mPlayerInput = new PlayerInput();
        mWorldView = new WorldView(mWorldModel);

        Gdx.input.setInputProcessor(mPlayerInput);
    }


    @Override
    public void update(float delta) {
        mPlayerInput.processCurrentInput(delta);
        
        mWorldModel.getPlayer().rotate(mPlayerInput.getRotation());
        mWorldModel.getPlayer().move(mPlayerInput.getDirection());

        if (mPlayerInput.isShooting()) {
            MyContactListener.CollisionObject co = MyContactListener.checkShootCollision(mWorldModel.getBulletWorld().getCollisionWorld(), mWorldModel.getPlayer().shoot());

            if(co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
                // World hit
                BulletHoleRenderer.addBullethole(co.hitPoint, co.hitNormal);
            } else {
                // Entity hit
                co.entity.damageEntity(25);
            }
        }
            // mWorldModel.checkShootCollision(mWorldModel.getPlayer().shoot());

        if (mPlayerInput.isJumping())
            mWorldModel.getPlayer().jump();

        mWorldModel.update(delta);
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
