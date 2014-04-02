package se.rhel.event;

/**
 * Group: Logic
 * Created by Emil on 2014-03-28.
 */
public class ModelEvent implements GameEvent<ModelListener> {

    EventType type;
    Object[] objs;

    public ModelEvent(EventType type, Object... objs) {
        this.type = type;
        this.objs = objs;
    }

    @Override
    public void notify(ModelListener listener) {
        listener.modelEvent(this.type, this.objs);
    }
}
