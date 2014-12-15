package ch.epfl.smartmap.callbacks;

/**
 * Used as Future object for {@code SearchEngine}, different methods are called by Asynchronous mechanisms to
 * give feedback when the result arrive.
 * 
 * @param <T>
 *            Type of result the search function is supposed to send back
 * @author jfperren
 */
public interface SearchRequestCallback<T> {

    /**
     * Called when a problem arise on the Network
     */
    void onNetworkError(Exception e);

    /**
     * Called when the object that is being searched doesn't exist
     */
    void onNotFound();

    /**
     * Called when a result is found, sends it back as parameter
     * 
     * @param result
     *            Result of the search query
     */
    void onResult(T result);
}
