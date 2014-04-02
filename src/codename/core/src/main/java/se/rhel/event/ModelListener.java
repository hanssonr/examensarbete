package se.rhel.event;

/**
 * Group: Logic
 * Created by Emil on 2014-03-28.
 */
public interface ModelListener {
    public void modelEvent(EventType type, Object... objs);
}
