package ch.epfl.smartmap.cache;

import java.util.LinkedList;
import java.util.List;

import ch.epfl.smartmap.listeners.DisplayableListener;
import ch.epfl.smartmap.listeners.LocalisableListener;
import ch.epfl.smartmap.listeners.UserListener;

/**
 * @author jfperren
 */
public abstract class AbstractUser implements User {

    // Instance Listeners
    protected final List<UserListener> mUserListeners;
    protected final List<LocalisableListener> mLocalisableListeners;
    protected final List<DisplayableListener> mDisplayableListeners;

    public AbstractUser() {
        mUserListeners = new LinkedList<UserListener>();
        mLocalisableListeners = new LinkedList<LocalisableListener>();
        mDisplayableListeners = new LinkedList<DisplayableListener>();
    }

    @Override
    public void addDisplayableListener(DisplayableListener newListener) {
        mDisplayableListeners.add(newListener);
    }

    @Override
    public void addLocalisableListener(LocalisableListener newListener) {
        mLocalisableListeners.add(newListener);
    }

    @Override
    public void addUserListener(UserListener newListener) {
        mUserListeners.add(newListener);
        mDisplayableListeners.add(newListener);
        mLocalisableListeners.add(newListener);
    }

    @Override
    public void removeDisplayableListener(DisplayableListener oldListener) {
        mDisplayableListeners.remove(oldListener);
    }

    @Override
    public void removeLocalisableListener(LocalisableListener oldListener) {
        mLocalisableListeners.remove(oldListener);
    }

    @Override
    public void removeUserListener(UserListener oldListener) {
        mUserListeners.add(oldListener);
        mLocalisableListeners.remove(oldListener);
        mDisplayableListeners.remove(oldListener);
    }
}
