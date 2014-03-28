package se.rhel.event;

/**
 * Created by Emil on 2014-03-28.
 */
public class ModelEvent implements GameEvent<ModelListener> {

    EventType type;

    public ModelEvent(EventType type) {
        this.type = type;
    }

    @Override
    public void notify(ModelListener listener) {
        listener.playerEvent(this.type);
    }
}
