package se.rhel.event;

/**
 * Created by Emil on 2014-03-28.
 */
public class ViewEvent implements GameEvent<ViewListener> {

    EventType type;

    public ViewEvent(EventType type) {
        this.type = type;
    }

    @Override
    public void notify(ViewListener listener) {
        listener.inputEvent(this.type);
    }

}
