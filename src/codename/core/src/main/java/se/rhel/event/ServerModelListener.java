package se.rhel.event;

/**
 * Created by Emil on 2014-04-02.
 */
public interface ServerModelListener {
    public void serverModelEvent(EventType type, Object... objs);
}
