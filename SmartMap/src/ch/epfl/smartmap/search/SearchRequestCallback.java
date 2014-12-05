package ch.epfl.smartmap.search;

/**
 * @author jfperren
 */
public interface SearchRequestCallback<T> {
    void onNetworkError();

    void onNotFound();

    void onResult(T result);
}
