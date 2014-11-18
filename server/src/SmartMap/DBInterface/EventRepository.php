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

    public function getEventsInRadius($longitude, $latitude, $radius)
    {
        // TODO ?
    }
}