package ch.epfl.smartmap.listeners;

/**
 * @author jfperren
 */
public interface EventListener extends LocalisableListener, DisplayableListener {
    void onCreatorIdChanged();

    void onDatesChanged();

    void onDescriptionChanged();

    void onParticipantsChanged();
}
