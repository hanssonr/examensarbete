package se.rhel.screen.local;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import se.rhel.CodeName;
import se.rhel.event.*;
import se.rhel.model.WorldModel;
import se.rhel.model.physics.MyContactListener;
import se.rhel.network.packet.ShootPacket;
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
public class GameScreen extends BaseScreen implements ViewListener, ModelListener {

    private final Events mEvents = new Events();

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

        // Listen to view events
        EventHandler.events.listen(ViewEvent.class, this);
        // Listen to model events
        EventHandler.events.listen(ModelEvent.class, this);

        Gdx.input.setInputProcessor(mPlayerInput);
    }


    @Override
    public void update(float delta) {
        mPlayerInput.processCurrentInput(delta);
        
//        mWorldModel.getPlayer().rotate(mPlayerInput.getRotation());
//        mWorldModel.getPlayer().move(mPlayerInput.getDirection());

        mWorldModel.getPlayer().move(mPlayerInput.getDirection());
        mWorldModel.getPlayer().rotate(mPlayerInput.getRotation());
        // mWorldModel.checkShootCollision(mWorldModel.getPlayer().shoot());

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
        mWorldView.update(delta);
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

        switch (type) {
            case JUMP:
                mWorldModel.getPlayer().jump();
                break;
            case SHOOT:
                mWorldModel.getPlayer().shoot();
                // We want a ray from middle of screen as basis of hit detection
                Ray ray = mWorldModel.getCamera().getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
                ray = ray.cpy();

                // For debugging purposes
                Vector3 from = new Vector3(); Vector3 to = new Vector3();

                from.set(ray.origin);
                to.set(ray.direction).scl(75f).add(from);

                Vector3[] rays = new Vector3[2];
                rays[0] = from;
                rays[1] = to;
                MyContactListener.CollisionObject co = MyContactListener.checkShootCollision(mWorldModel.getBulletWorld().getCollisionWorld(), rays);

                if(co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
                    // World hit
                    BulletHoleRenderer.addBullethole(co.hitPoint, co.hitNormal);
                } else {
                    // Entity hit
                    co.entity.damageEntity(25);
                }
                break;
        }

    }

    @Override
    public void playerEvent(EventType type) {

        switch(type) {
            case SHOOT:
                Vector3[] collide = mWorldModel.getCamera().getShootRay();
                Vector3[] visual = mWorldModel.getCamera().getVisualRepresentationShoot();

                // The collision
                mWorldModel.checkShootCollision(collide);

                // The rendering & sound
                mWorldView.shoot(visual);
                break;
            default:
                break;
        }

    }
}
