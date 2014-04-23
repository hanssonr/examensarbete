package se.rhel.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import se.rhel.CodeName;
import se.rhel.event.*;
import se.rhel.model.WorldModel;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Grenade;
import se.rhel.model.weapon.IExplodable;
import se.rhel.view.BulletHoleRenderer;
import se.rhel.view.ParticleRenderer;
import se.rhel.view.input.PlayerInput;
import se.rhel.view.WorldView;

/**
 * Group: Logic
 *
 * Created by Emil on 2014-03-10.
 * assigned to libgdx-gradle-template in se.rhel.screen.local
 */
public class GameController extends BaseGameController {

    public GameController(CodeName game) {
        super(new PlayerInput());
        //super(game);
        Gdx.app.setLogLevel(Application.LOG_NONE);

        mWorldModel = new WorldModel();
        mWorldView = new WorldView(mWorldModel);
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

    @Override
    public void inputEvent(EventType type) {
        super.inputEvent(type);
        switch (type) {
            case SHOOT:
                mWorldModel.getPlayer().shoot();
                break;
            case GRENADE:
                Grenade g = new Grenade(mWorldModel.getBulletWorld(), mWorldModel.getPlayer().getPosition(), mWorldModel.getPlayer().getDirection());

                mWorldModel.addGrenade(g);
                mWorldView.addGrenade(g);
                break;
        }

    }

    @Override
    public void modelEvent(EventType type, Object... objs) {
        super.modelEvent(type, objs);
        switch(type) {
            case SHOOT:
                RayVector ray = mWorldView.getCamera().getShootRay();
                mWorldModel.checkShootCollision(ray);
                RayVector visualray = mWorldView.getCamera().convertToVisualRay(ray);
                mWorldView.shoot(visualray);
            break;

            case BULLET_HOLE:
                BulletHoleRenderer.addBullethole((Vector3) objs[0], (Vector3) objs[1]);
                break;

            default:
                break;
        }
    }
}
