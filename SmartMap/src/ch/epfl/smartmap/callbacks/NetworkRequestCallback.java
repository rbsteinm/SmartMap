package ch.epfl.smartmap.callbacks;

/**
 * Serves as Future object for application-server communications. Methods are called to give feedback on what
 * happened in the Asynchronous tasks.
 * 
 * @param <T>
 *            Type of result if needed (put {@code Void} otherwise)
 * @author jfperren
 */
public interface NetworkRequestCallback<T> {
    /**
     * Called when a network error happened
     * 
     * @param e
     *            Exception sent by the client
     */
    void onFailure(Exception e);

    /**
     * Called when the communication was successfully executed
     * 
     * @param result
     *            In some cases, need to send back a result. Be careful to check for {@code null} values.
     */
    void onSuccess(T result);
}
