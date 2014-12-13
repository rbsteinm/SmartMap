<?php
/**
 * Created by PhpStorm.
 * User: matthieu
 * Date: 13.12.14
 * Time: 18:04
 */
namespace SmartMap\DBInterface;


/**
 * Models the Event repo.
 *
 * @author Pamoi
 */
interface EventRepositoryInterface
{
    /**
     * Creates an event in the database, and returns the event with it's proper id set.
     *
     * @param Event $event
     * @return Event
     * @throws DatabaseException
     */
    public function createEvent(Event $event);

    /**
     * Updates an existing event in the database.
     *
     * @param Event $event
     * @throws DatabaseException
     */
    public function updateEvent(Event $event);

    /**
     * Get an event from the database, given it's id.
     *
     * @param $id
     * @return Event
     * @throws DatabaseException
     */
    public function getEvent($id);

    /**
     * Gets the not finished yet events in a radius of $radius kilometers around position
     * given by $longitude and $latitude.
     *
     * @param $longitude
     * @param $latitude
     * @param $radius
     * @return array
     * @throws DatabaseException
     */
    public function getEventsInRadius($longitude, $latitude, $radius);

    /**
     * Adds an invitation for a user to an event.
     *
     * @param $eventId
     * @param $usersIds
     * @throws DatabaseException
     */
    public function addEventInvitations($eventId, $usersIds);

    /**
     * Removes an invitation for a user to an event.
     *
     * @param $eventId
     * @param $userId
     * @throws DatabaseException
     */
    public function removeEventInvitation($eventId, $userId);

    /**
     * Adds a user as participant to an event.
     *
     * @param $eventId
     * @param $userId
     * @throws DatabaseException
     */
    public function addUserToEvent($eventId, $userId);

    /**
     * Removes a participant from an event.
     *
     * @param $eventId
     * @param $userId
     * @throws DatabaseException
     */
    public function removeUserFromEvent($eventId, $userId);

    /**
     * Get the ids of users participating to an event.
     *
     * @param $eventId
     * @return array
     * @throws DatabaseException
     */
    public function getEventParticipants($eventId);

    /**
     * Get the ids of the events to which a user is invited.
     *
     * @param $userId
     * @return array
     * @throws DatabaseException
     */
    public function getEventInvitations($userId);
}