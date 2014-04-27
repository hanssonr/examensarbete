package se.rhel.event;

/**
 * Created by Emil on 2014-04-21.
 */
public class ServerModelEvent implements GameEvent<ServerModelListener> {

    EventType type;
    Object[] objs;

    public ServerModelEvent(EventType type, Object... objs) {
        this.type = type;
        this.objs = objs;
    }

    @Override
    public void notify(ServerModelListener listener) {
        listener.serverModelEvent(this.type, this.objs);
    }
}