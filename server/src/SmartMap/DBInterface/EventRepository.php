<?php

namespace SmartMap\DBInterface;

use Doctrine\DBAL\Connection;

/**
 * Models the Event repo.
 *
 * @author Pamoi
 */
class EventRepository
{
    private static $TABLE_EVENT = 'events';

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
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in createEvent.', 1, $e);
        }
        
        $event->setId($this->mDb->fetchColumn('SELECT LAST_INSERT_ID()', array(), 0));
        
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
                $eventData['id'],
                $eventData['creator_id'],
                $eventData['starting_date'],
                $eventData['ending_date'],
                $eventData['longitude'],
                $eventData['latitude'],
                $eventData['position_name'],
                $eventData['name'],
                $eventData['description']
            );
        }
        catch (\InvalidArgumentException $e)
        {
            throw new DatabaseException('Event with invalid state in database with id ' . $id . '.');
        }

        return $event;
    }

    /**
     * Gets the events in a radius of $radius kilometers around position given by
     * $longitude and $latitude.
     *
     * @param $longitude
     * @param $latitude
     * @param $radius
     * @return array
     * @throws DatabaseException
     */
    public function getEventsInRadius($longitude, $latitude, $radius)
    {
        $req = "SELECT  * FROM events";

        try
        {
            $stmt = $this->mDb->executeQuery($req);
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in getEventsInRadius.', 1, $e);
        }

        $events = array();

        $lat1 = deg2rad($latitude);
        $long1 = deg2rad($longitude);

        while ($eventData = $stmt->fetch())
        {
            $lat2 = deg2rad($eventData['latitude']);
            $long2 = deg2rad($eventData['longitude']);

            $dlat = $lat1 - $lat2;
            $dlon = $long1 - $long2;

            $a = sin($dlat/2) * sin($dlat/2) + cos($lat1) * cos($lat2) * sin($dlon/2) * sin($dlon/2);

            $c = 2 * atan2(sqrt($a), sqrt(1 - $a));

            $d = self::$EARTH_RADIUS * $c;

            if ($d <= $radius)
            {
                try {
                    $event = new Event(
                        $eventData['id'],
                        $eventData['creator_id'],
                        $eventData['starting_date'],
                        $eventData['ending_date'],
                        $eventData['longitude'],
                        $eventData['latitude'],
                        $eventData['position_name'],
                        $eventData['name'],
                        $eventData['description']
                    );
                }
                catch (\InvalidArgumentException $e)
                {
                    throw new DatabaseException('Event with invalid state in database with id '
                        . $eventData['id'] . '.');
                }

                $events[] = $event;
            }
        }

        return $events;
    }
}