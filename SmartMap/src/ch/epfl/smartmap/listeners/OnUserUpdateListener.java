package ch.epfl.smartmap.listeners;

/**
 * This interface is defined for all the classes that need to listen to any change in any field of a specified
 * User, for example the DatabaseHelper.
 * 
 * @author jfperren
 */
public interface OnUserUpdateListener {

    /**
     * Called when e-mail changes.
     */
    void onEmailChanged();

    /**
     * Called when the image of the Displayable changes
     */
    void onImageChanged();

    /**
     * Called when timestamp is refreshed.
     */
    void onLastSeenChanged();

    /**
     * Called when the location changes
     */
    void onLocationChanged();

    /**
     * Called when location changes
     */
    void onLocationStringChanged();

    /**
     * Called when name changes
     */
    void onNameChanged();

    /**
     * Called when phone number changes.
     */
    void onPhoneNumberChanged();
}
