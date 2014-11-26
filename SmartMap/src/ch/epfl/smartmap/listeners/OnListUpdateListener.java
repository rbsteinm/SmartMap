package ch.epfl.smartmap.listeners;

/**
 * @author jfperren
 */
public interface OnListUpdateListener {
    /**
     * Called when a Friend is added
     * 
     * @param id
     *            id of the Friend added
     */
    void onElementAdded(long id);

    /**
     * Called when a Friend is removed
     * 
     * @param id
     *            id of the Friend removed
     */
    void onElementRemoved(long id);
}
