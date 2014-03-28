package se.rhel.event;

/**
 * Created by Emil on 2014-03-28.
 */
public interface GameEvent<L> {
    public void notify(final L listener);
}
