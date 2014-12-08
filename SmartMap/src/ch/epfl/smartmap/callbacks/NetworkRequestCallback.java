package ch.epfl.smartmap.callbacks;

/**
 * This interface must be implemented by objects interested by the
 * status of a server request.
 * 
 * @author Pamoi
 */
public interface NetworkRequestCallback {
    void onSuccess();

    void onFailure();
}
