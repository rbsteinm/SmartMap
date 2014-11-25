package ch.epfl.smartmap.listeners;

/**
 * @author jfperren
 */
public interface OnDisplayableInformationsUpdateListener {

    /**
     * Called when the image of the Displayable changes
     */
    void onImageChanged();

    /**
     * Called when the name of the Displayable changes
     */
    void onNameChanged();

    /**
     * Called when the short description of the Displayable changes
     */
    void onShortInfoChanged();
}
