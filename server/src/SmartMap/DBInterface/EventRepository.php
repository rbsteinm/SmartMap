<?php

namespace SmartMap\DBInterface;

use Doctrine\DBAL\Connection;
use SmartMap\Control\RequestUtils;

/**
 * Models the Event repo.
 *
 * @author Pamoi
 */
class EventRepository
{
    private static $TABLE_EVENT = 'events';
    private static $TABLE_EVENT_PARTICIPANTS = 'events_participants';
    private static $TABLE_EVENT_INVITATIONS = 'events_invitations';

    private static $EARTH_RADIUS = 6373;
    
    private $mDb;
    
    /**
     * Constructs a EventRepository with a Doctrine\DBAL\Connection object.
     *
     * @param Connection $db
     */
    function __construct(Connection $db)
    {
        $this->mDb = $db;
    }

    /**
     * Creates an event in the database, and returns the event with it's proper id set.
     *
     * @param Event $event
     * @return Event
     * @throws DatabaseException
     */
    public function createEvent(Event $event)
    {
        try
        {
            // We do not need to check the validity of parameters as it is done in the Event class.
            $this->mDb->insert(self::$TABLE_EVENT,
                array(
                    'creator_id' => $event->getCreatorId(),
                    'starting_date' => $event->getStartingDate(),
                    'ending_date' => $event->getEndingDate(),
                    'longitude' => $event->getLongitude(),
                    'latitude' => $event->getLatitude(),
                    'position_name' => $event->getPositionName(),
                    'name' => $event->getName(),
                    'description' => $event->getDescription()
                ));

            $event->setId($this->mDb->fetchColumn('SELECT LAST_INSERT_ID()', array(), 0));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in createEvent.', 1, $e);
        }
        
        return $event;
    }

    /**
     * Updates an existing event in the database.
     *
     * @param Event $event
     * @throws DatabaseException
     */
    public function updateEvent(Event $event)
    {
        try
        {
            // We do not need to check the validity of parameters as it is done in the Event class.
            $this->mDb->update(self::$TABLE_EVENT,
                array(
                    'creator_id' => $event->getCreatorId(),
                    'starting_date' => $event->getStartingDate(),
                    'ending_date' => $event->getEndingDate(),
                    'longitude' => $event->getLongitude(),
                    'latitude' => $event->getLatitude(),
                    'position_name' => $event->getPositionName(),
                    'name' => $event->getName(),
                    'description' => $event->getDescription()
                ), array('id' => $event->getId()));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in updateEvent.', 1, $e);
        }
    }

    /**
     * Get an event from the database, given it's id.
     *
     * @param $id
     * @return Event
     * @throws DatabaseException
     */
    public function getEvent($id)
    {
        try
        {
            $req = "SELECT * FROM " . self::$TABLE_EVENT . " WHERE id = ?";

            $eventData = $this->mDb->fetchAssoc($req, array((int) $id));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in getEvent.', 1, $e);
        }

        if (!$eventData)
        {
            throw new DatabaseException('No event found with id ' . $id . ' in method getEvent.');
        }

        try
        {
            $event = new Event(
                (int) $eventData['id'],
                (int) $eventData['creator_id'],
                $eventData['starting_date'],
                $eventData['ending_date'],
                (double) $eventData['longitude'],
                (double) $eventData['latitude'],
                $eventData['position_name'],
                $eventData['name'],
                $eventData['description']
            );
        }
        catch (\InvalidArgumentException $e)
        {
            throw new DatabaseException('Event with invalid state in database with id ' . $id . '.', 1, $e);
        }

        return $event;
    }

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
    public function getEventsInRadius($longitude, $latitude, $radius)
    {
        // We only send events that are not finished yet.
        $req = "SELECT id, longitude, latitude FROM events WHERE ending_date > NOW()";

        try
        {
            $stmt = $this->mDb->executeQuery($req);
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in getEventsInRadius.', 1, $e);
        }

        $eventsIds = array();

        $lat1 = deg2rad($latitude);
        $long1 = deg2rad($longitude);

        while ($eventData = $stmt->fetch())
        {
            $lat2 = deg2rad($eventData['latitude']);
            $long2 = deg2rad($eventData['longitude']);

            $dlat = $lat1 - $lat2;
            $dlon = $long1 - $long2;

            $a = sin($dlat / 2) * sin($dlat / 2) + cos($lat1) * cos($lat2) * sin($dlon / 2) * sin($dlon / 2);

            $c = 2 * atan2(sqrt($a), sqrt(1 - $a));

            $d = self::$EARTH_RADIUS * $c;

            if ($d <= $radius) {
                $eventsIds[] = $eventData['id'];
            }
        }

        return $eventsIds;
    }

    /**
     * Adds an invitation for a user to an event.
     *
     * @param $eventId
     * @param $usersIds
     * @throws DatabaseException
     */
    public function addEventInvitations($eventId, $usersIds)
    {
        if (!is_array($usersIds))
        {
            throw new DatabaseException('Expected argument 2 to be array in addEventInvitations.');
        }

        try
        {
            $req = "SELECT * FROM " . self::$TABLE_EVENT . " WHERE id = ?";
            $event = $this->mDb->fetchAssoc($req, array((int) $eventId));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in addEventInvitations.', 1, $e);
        }

        if (!$event)
        {
            throw new DatabaseException('Trying to add an invitation for an non existing event.');
        }

        try
        {
            foreach ($usersIds as $userId) {
                $req = "SELECT * FROM " . self::$TABLE_EVENT_INVITATIONS . " WHERE id_event = ? AND id_user = ?";
                $invitation = $this->mDb->fetchAssoc($req, array((int)$eventId, (int)$userId));

                // If the invitation doesn't exist yet, we add it.
                if (!$invitation) {
                    $req = "INSERT INTO " . self::$TABLE_EVENT_INVITATIONS . " VALUES (?, ?)";
                    $this->mDb->executeQuery($req, array((int)$eventId, (int)$userId));
                }
            }
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in addEventInvitations.', 1, $e);
        }
    }

    /**
     * Removes an invitation for a user to an event.
     *
     * @param $eventId
     * @param $userId
     * @throws DatabaseException
     */
    public function removeEventInvitation($eventId, $userId)
    {
        try
        {
            $this->mDb->delete(self::$TABLE_EVENT_INVITATIONS, array(
                'id_event' => (int)$eventId,
                'id_user' => (int)$userId));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in removeEventInvitation.', 1, $e);
        }
    }

    /**
     * Adds a user as participant to an event.
     *
     * @param $eventId
     * @param $userId
     * @throws DatabaseException
     */
    public function addUserToEvent($eventId, $userId)
    {
        try
        {
            $req = "SELECT * FROM " . self::$TABLE_EVENT . " WHERE id = ?";
            $event = $this->mDb->fetchAssoc($req, array((int) $eventId));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in addUserToEvent.', 1, $e);
        }

        if (!$event)
        {
            throw new DatabaseException('Trying to add a user to a non existing event.');
        }

        try
        {
            $req = "SELECT * FROM " . self::$TABLE_EVENT_PARTICIPANTS . " WHERE id_event = ? AND id_user = ?";
            $participant = $this->mDb->fetchAssoc($req, array((int)$eventId, (int)$userId));

            // If the user is not participating yet, we add it.
            if (!$participant) {
                $req = "INSERT INTO " . self::$TABLE_EVENT_PARTICIPANTS . " VALUES (?, ?)";
                $this->mDb->executeQuery($req, array((int)$eventId, (int)$userId));
            }
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in addUserToEvent.', 1, $e);
        }
    }

    /**
     * Removes a participant from an event.
     *
     * @param $eventId
     * @param $userId
     * @throws DatabaseException
     */
    public function removeUserFromEvent($eventId, $userId)
    {
        try
        {
            $this->mDb->delete(self::$TABLE_EVENT_PARTICIPANTS, array(
                'id_event' => (int)$eventId,
                'id_user' => (int)$userId));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in removeUserFromEvent.', 1, $e);
        }
    }

    /**
     * Get the ids of users participating to an event.
     *
     * @param $eventId
     * @return array
     * @throws DatabaseException
     */
    public function getEventParticipants($eventId)
    {
        try
        {
            $req = "SELECT id_user FROM " . self::$TABLE_EVENT_PARTICIPANTS . " WHERE id_event = ?";
            $stmt = $this->mDb->executeQuery($req, array((int) $eventId));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in getEventParticipants.', 1, $e);
        }

        $users = array();

        while ($userId = $stmt->fetch())
        {
            $users[] = (int) $userId['id_user'];
        }

        return $users;
    }

    /**
     * Get the ids of the events to which a user is invited.
     *
     * @param $userId
     * @return array
     * @throws DatabaseException
     */
    public function getEventInvitations($userId)
    {
        try
        {
            $req = "SELECT id_event FROM " . self::$TABLE_EVENT_INVITATIONS . " WHERE id_user = ?";
            $stmt = $this->mDb->executeQuery($req, array((int) $userId));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in getEventInvitations.', 1, $e);
        }

        $eventsIds= array();

        while ($eventId = $stmt->fetch())
        {
            $eventsIds[] = (int) $eventId['id_event'];
        }

        return $eventsIds;
    }
}