package ch.epfl.smartmap.listeners;

/**
 * This interface is defined for classes that only chose to listen to a Friend, Event or any other Displayable
 * as just a Displayable and doesn't care about the rest of the fields
 * 
 * @author jfperren
 */
public interface DisplayableListener {

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
    void onSubtitleChanged();
}
