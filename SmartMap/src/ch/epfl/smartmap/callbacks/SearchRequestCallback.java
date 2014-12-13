package ch.epfl.smartmap.callbacks;

public interface SearchRequestCallback<T> {
    void onNetworkError();

    void onNotFound();

    void onResult(T result);
}
