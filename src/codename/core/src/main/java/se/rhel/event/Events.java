package se.rhel.event;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Group: Logic
 * Created by Emil on 2014-03-28.
 */
public final class Events {

    // Mapping of class events to active listeners
    private final HashMap<Class, ArrayList> mMap = new HashMap<Class, ArrayList>(10);

    // Add a listener to an event class
    public <L> void listen(Class<? extends GameEvent<L>> evtClass, L listener) {
        final ArrayList<L> listeners = listenersOf(evtClass);

        synchronized (listeners) {
            if(!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    // Stop sending an event class to a given listener
    public <L> void mute(Class<? extends GameEvent<L>> evtClass, L listener) {
        final ArrayList<L> listeners = listenersOf(evtClass);

        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    // Gets listeners for a given event class
    private <L> ArrayList<L> listenersOf(Class<? extends GameEvent<L>> evtClass) {
        synchronized (mMap) {
            final ArrayList<L> existing = mMap.get(evtClass);
            if(existing != null) {
                return existing;
            }

            final ArrayList<L> emptyList = new ArrayList<L>(5);
            mMap.put(evtClass, emptyList);
            return emptyList;
        }
    }

    // Notify a new event to registered listeners of this event class
    public <L> void notify(final GameEvent<L> event) {
        Class<GameEvent<L>> evtClass = (Class<GameEvent<L>>) event.getClass();

        for(L listener : listenersOf(evtClass)) {
            event.notify(listener);
        }
    }
}
