package se.rhel.screen;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import se.rhel.CodeName;
import se.rhel.event.*;
import se.rhel.model.WorldModel;
import se.rhel.model.component.GameObject;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Grenade;
import se.rhel.view.WorldView;

/**
 * Group: Logic
 *
 * Created by Emil on 2014-03-10.
 * assigned to libgdx-gradle-template in se.rhel.screen.local
 */
public class GameController extends BaseGameController {

    public GameController(CodeName game) {
        super();
        Gdx.app.setLogLevel(Application.LOG_NONE);

        mWorldModel = new WorldModel(mEvents);
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
                Grenade g = new Grenade(mWorldModel.getBulletWorld(), mWorldModel.getPlayer().getShootPosition(), mWorldModel.getPlayer().getDirection());

                mWorldModel.addGrenade(g);
                mWorldView.addGrenade(g);
                break;
        }

    }

    @Override
    public void modelEvent(EventType type, Object... objs) {
        super.modelEvent(type, objs);
        switch(type) {
            case SHOOT: // [0] = RayVector
                RayVector ray = (RayVector) objs[0];
                mWorldModel.checkShootCollision(ray);
                RayVector.convertToVisual(ray);
                mWorldView.shoot(ray);
            break;

            case BULLET_HOLE:
                mWorldView.addBullethole((Vector3)objs[0], (Vector3)objs[1]);
                break;

            default:
                break;
        }
    }
}
