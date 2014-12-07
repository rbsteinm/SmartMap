package ch.epfl.smartmap.callbacks;

/**
 * @author jfperren
 */
public interface SearchRequestCallback<T> {
    void onNetworkError();

    void onNotFound();

    void onResult(T result);
}
