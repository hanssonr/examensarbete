package se.rhel.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import se.rhel.event.*;
import se.rhel.model.IWorldModel;
import se.rhel.model.entity.DamageAbleEntity;
import se.rhel.model.weapon.Grenade;
import se.rhel.model.weapon.IExplodable;
import se.rhel.view.IWorldView;
import se.rhel.view.ParticleRenderer;
import se.rhel.view.input.IInput;
import se.rhel.view.input.PlayerInput;


public class BaseGameController extends AbstactController implements ViewListener, ModelListener {

    protected IInput mPlayerInput;
    protected IWorldView mWorldView;
    protected IWorldModel mWorldModel;
    protected Events mEvents;


    public BaseGameController() {
        mEvents = new Events();

        mPlayerInput = new PlayerInput(mEvents);
        Gdx.input.setInputProcessor(mPlayerInput);

        mEvents.listen(ViewEvent.class, this);
        mEvents.listen(ModelEvent.class, this);
    }

    @Override
    public void update(float delta) {
        mPlayerInput.processCurrentInput(delta);

        mWorldModel.getPlayer().move(mPlayerInput.getDirection());
        mWorldModel.getPlayer().rotate(mPlayerInput.getRotation());

        mWorldModel.update(delta);
        mWorldView.update(delta);
    }

    @Override
    public void draw(float delta) {}

    @Override
    public void modelEvent(EventType type, Object... objs) {
        switch(type) {
            case EXPLOSION:
                mWorldView.addParticleEffect(((Vector3)objs[0]), ParticleRenderer.Particle.EXPLOSION);
                break;

            case DAMAGE:
                mWorldModel.checkEntityStatus(((DamageAbleEntity) objs[0]));
                mWorldView.addParticleEffect(((DamageAbleEntity) objs[0]).getPosition(), ParticleRenderer.Particle.BLOOD);
                break;
        }
    }

    @Override
    public void inputEvent(EventType type) {
        switch (type) {
            case JUMP:
                mWorldModel.getPlayer().jump();
                break;
            case SHOOT:
                mWorldModel.shoot();
                break;
        }
    }
}
