<?php

use SmartMap\DBInterface\Event;
use SmartMap\DBInterface\EventRepository;

use Doctrine\DBAL\DriverManager;
use Doctrine\DBAL\Configuration;

/**
 * Tests for the EventRepository class.
 * To run them, run
 * $> phpunit --bootstrap vendor/autoload.php --configuration tests/phpunit.xml tests/EventRepositoryTest.php
 * from the server directory. You need to have a database configured in phpunit.xml running during the tests.
 *
 * @author Pamoi
 *
 */
class EventRepositoryTest extends PHPUnit_Extensions_Database_TestCase
{
    // only instantiate doctrine connection once
    static private $doctrine = null;

    // only instantiate PHPUnit_Extensions_Database_DB_IDatabaseConnection once per test
    private $conn = null;

    final public function getConnection()
    {
        if ($this->conn === null) {
            if (self::$doctrine == null) {
                $config = new Configuration();
                $connectionParams = array(
                    'dbname' => $GLOBALS['DB_DBNAME'],
                    'user' => $GLOBALS['DB_USER'],
                    'password' => $GLOBALS['DB_PASSWD'],
                    'host' => 'localhost',
                    'driver' => 'pdo_mysql',
                );
                self::$doctrine = DriverManager::getConnection($connectionParams, $config);
            }
            $this->conn = $this->createDefaultDBConnection(self::$doctrine->getWrappedConnection(),
                $GLOBALS['DB_DBNAME']);
        }

        return $this->conn;
    }

    protected function getDataSet()
    {
        return new PHPUnit_Extensions_Database_DataSet_YamlDataSet(
            dirname(__FILE__)."/fixtures/EventRepositoryTest.yml");
    }

    public function testCreateEvent()
    {
        $this->assertEquals(4, $this->getConnection()->getRowCount('events'), "Pre-Condition");

        $repo = new EventRepository(self::$doctrine);

        $event = new Event(
            1,
            12345,
            '2014-11-18 08:15:00',
            '2014-11-18 08:40:00',
            6.56186974,
            46.51895762,
            'EPFL',
            'Presentation of SmartMap app.',
            'SmartMap is an app that shows you your friends and events on a map.'
        );

        $event = $repo->CreateEvent($event);

        $this->assertEquals(5, $event->getId());

        $this->assertEquals(5, $this->getConnection()->getRowCount('events'), "Post-Condition");
    }

    public function testUpdateEvent()
    {
        $repo = new EventRepository(self::$doctrine);

        $event = new Event(
            1,
            274,
            '2014-11-18 08:15:00',
            '2014-11-18 08:40:00',
            1.0,
            2.0,
            'EPFL',
            'Presentation of SmartMap app.',
            'SmartMap is an app that shows you your friends and events on a map.'
        );

        $repo->updateEvent($event);

        $correctRow = array(
            'id' => 1,
            'creator_id' => 274,
            'starting_date' => '2014-11-18 08:15:00',
            'ending_date' => '2014-11-18 08:40:00',
            'longitude' => 1.0,
            'latitude' => 2.0,
            'position_name' => 'EPFL',
            'name' => 'Presentation of SmartMap app.',
            'description' => 'SmartMap is an app that shows you your friends and events on a map.'
        );

        $queryTable = $this->getConnection()->createQueryTable('events', 'SELECT * FROM events WHERE id = 1');

        $row = $queryTable->getRow(0);

        $this->assertEquals($correctRow, $row);
    }

    public function testGetEvent()
    {
        $repo = new EventRepository(self::$doctrine);

        $correct = new Event(
            2,
            1,
            '2014-12-24 20:30:00',
            '2014-12-25 00:00:00',
            45.46734,
            76.637524,
            'Somewhere',
            'Christmas night',
            'Gifts for everyone !'
        );

        $event = $repo->getEvent(2);

        $this->assertEquals($correct, $event);
    }

    /**
     * @expectedException SmartMap\DBInterface\DatabaseException
     * @expectedExceptionMessage No event found with id 10 in method getEvent.
     */
    public function testGetNonExistingEvent()
    {
        $repo = new EventRepository(self::$doctrine);

        $repo->getEvent(10);
    }

    /**
     * @expectedException SmartMap\DBInterface\DatabaseException
     * @expectedExceptionMessage Event with invalid state in database with id 3.
     */
    public function testGetInvalidStateEvent()
    {
        $repo = new EventRepository(self::$doctrine);

        $repo->getEvent(3);
    }

    public function testGetEventsInRadius()
    {
        $repo = new EventRepository(self::$doctrine);

        // Only events ending in future !
        $correct = array(new Event(
            4,
            3,
            '2030-11-18 10:30:00',
            '2030-11-18 12:00:00',
            6.56186974,
            46.51895762,
            'UNIL',
            'Seminar on usefulness of philosophy',
            'Of course philosophy is useless !'
        ));

        $events = $repo->getEventsInRadius(6.76186181 ,46.81875763, 3735); // 3735 is just before second event

        $this->assertEquals($correct, $events);
    }

    public function testAddEventInvitations()
    {
        $this->assertEquals(4, $this->getConnection()->getRowCount('events_invitations'), "Pre-Condition");

        $repo = new EventRepository(self::$doctrine);

        $repo->addEventInvitations(1, array(3, 12, 34)); // Invitation for user 12 already exists.

        $this->assertEquals(6, $this->getConnection()->getRowCount('events_invitations'), "Post-Condition");
    }

    /**
     * @expectedException SmartMap\DBInterface\DatabaseException
     * @expectedExceptionMessage Expected argument 2 to be array in addEventInvitations.
     */
    public function testAddEventInvitationsWithBadParam()
    {
        $repo = new EventRepository(self::$doctrine);

        $repo->addEventInvitations(2, 'Toto');
    }

    /**
     * @expectedException SmartMap\DBInterface\DatabaseException
     * @expectedExceptionMessage Trying to add an invitation for an non existing event.
     */
    public function testAddNonExistingEventInvitation()
    {
        $repo = new EventRepository(self::$doctrine);

        $repo->addEventInvitations(54, array(1,2,3));
    }

    public function testRemoveEventInvitation()
    {
        $this->assertEquals(4, $this->getConnection()->getRowCount('events_invitations'), "Pre-Condition");

        $repo = new EventRepository(self::$doctrine);

        $repo->removeEventInvitation(1, 12);

        $this->assertEquals(3, $this->getConnection()->getRowCount('events_invitations'), "Post-Condition");
    }

    public function testRemoveNonExistingEventInvitation()
    {
        $this->assertEquals(4, $this->getConnection()->getRowCount('events_invitations'), "Pre-Condition");

        $repo = new EventRepository(self::$doctrine);

        $repo->removeEventInvitation(1, 54); // No invitation for user 54.

        $this->assertEquals(4, $this->getConnection()->getRowCount('events_invitations'), "Post-Condition");
    }

    public function testAddUserToEvent()
    {
        $this->assertEquals(2, $this->getConnection()->getRowCount('events_participants'), "Pre-Condition");

        $repo = new EventRepository(self::$doctrine);

        $repo->addUserToEvent(2, 12);

        $this->assertEquals(3, $this->getConnection()->getRowCount('events_participants'), "Post-Condition");
    }

    public function testAddAlreadyParticipatingUserToEvent()
    {
        $this->assertEquals(2, $this->getConnection()->getRowCount('events_participants'), "Pre-Condition");

        $repo = new EventRepository(self::$doctrine);

        $repo->addUserToEvent(1, 13); // User 13 is already participating to event 1.

        $this->assertEquals(2, $this->getConnection()->getRowCount('events_participants'), "Post-Condition");
    }

    /**
     * @expectedException SmartMap\DBInterface\DatabaseException
     * @expectedExceptionMessage Trying to add a user to a non existing event.
     */
    public function testAddUserToNonExistingEvent()
    {
        $repo = new EventRepository(self::$doctrine);

        $repo->addUserToEvent(54, 13);
    }

    public function testRemoveUserFromEvent()
    {
        $this->assertEquals(2, $this->getConnection()->getRowCount('events_participants'), "Pre-Condition");

        $repo = new EventRepository(self::$doctrine);

        $repo->removeUserFromEvent(1, 13); // User 13 is participating to event 1.

        $this->assertEquals(1, $this->getConnection()->getRowCount('events_participants'), "Post-Condition");
    }

    public function testRemoveNonParticipatingUserFromEvent()
    {
        $this->assertEquals(2, $this->getConnection()->getRowCount('events_participants'), "Pre-Condition");

        $repo = new EventRepository(self::$doctrine);

        $repo->removeUserFromEvent(1, 56); // User 56 is not participating to event 1.

        $this->assertEquals(2, $this->getConnection()->getRowCount('events_participants'), "Post-Condition");
    }

    public function testGetEventParticipants()
    {
        $repo = new EventRepository(self::$doctrine);

        $participants = $repo->getEventParticipants(1);

        $this->assertEquals(array(13, 5), $participants);
    }

    public function testGetEventInvitations()
    {
        $repo = new EventRepository(self::$doctrine);

        $invitations = $repo->getEventInvitations(20);

        $this->assertEquals(array(1, 2), $invitations);
    }
}