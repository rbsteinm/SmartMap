package ch.epfl.smartmap.listeners;

/**
 * @author jfperren
 */
public interface LocalisableListener {
    void onLocationChanged();

    void onLocationStringChanged();

    void onMarkerOptionsChanged();
}
