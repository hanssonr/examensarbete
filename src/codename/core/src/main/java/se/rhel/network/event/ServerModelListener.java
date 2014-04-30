package se.rhel.network.event;

import se.rhel.event.EventType;

/**
 * Created by Emil on 2014-04-21.
 */
public interface ServerModelListener {
    public void serverModelEvent(EventType type, Object... objs);
}
