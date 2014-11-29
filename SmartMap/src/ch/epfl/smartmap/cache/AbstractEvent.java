package ch.epfl.smartmap.cache;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.smartmap.listeners.DisplayableListener;
import ch.epfl.smartmap.listeners.EventListener;
import ch.epfl.smartmap.listeners.LocalisableListener;

/**
 * @author jfperren
 */
public abstract class AbstractEvent implements Event {

    // Instance Listeners
    protected final List<EventListener> mEventListeners;
    protected final List<LocalisableListener> mLocalisableListeners;
    protected final List<DisplayableListener> mDisplayableListeners;

    public AbstractEvent() {
        mEventListeners = new LinkedList<EventListener>();
        mLocalisableListeners = new LinkedList<LocalisableListener>();
        mDisplayableListeners = new LinkedList<DisplayableListener>();
    }

    @Override
    public void addDisplayableListener(DisplayableListener newListener) {
        mDisplayableListeners.add(newListener);
    }

    @Override
    public void addEventListener(EventListener newListener) {
        mEventListeners.add(newListener);
        mDisplayableListeners.add(newListener);
        mLocalisableListeners.add(newListener);
    }

    @Override
    public void addLocalisableListener(LocalisableListener newListener) {
        mLocalisableListeners.add(newListener);
    }

    @Override
    public void removeDisplayableListener(DisplayableListener oldListener) {
        mDisplayableListeners.remove(oldListener);
    }

    @Override
    public void removeEventListener(EventListener oldListener) {
        mEventListeners.add(oldListener);
        mLocalisableListeners.remove(oldListener);
        mDisplayableListeners.remove(oldListener);
    }

    @Override
    public void removeLocalisableListener(LocalisableListener oldListener) {
        mLocalisableListeners.remove(oldListener);
    }
}
