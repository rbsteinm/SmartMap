package ch.epfl.smartmap.callbacks;

/**
 * This interface must be implemented by objects interested by the
 * status of a server request.
 * 
 * @param <T>
 *            Type of result if needed (put {@code Void} otherwise)
 * @author jfperren
 */
public interface NetworkRequestCallback<T> {
    void onFailure(Exception e);

    void onSuccess(T result);
}
