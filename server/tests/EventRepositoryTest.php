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
class UserRepositoryTest extends PHPUnit_Extensions_Database_TestCase
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
        $this->assertEquals(3, $this->getConnection()->getRowCount('events'), "Pre-Condition");

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

        $this->assertEquals(4, $event->getId());

        $this->assertEquals(4, $this->getConnection()->getRowCount('events'), "Post-Condition");
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
     * @expectedExceptionMessage No event found with id 4 in method getEvent.
     */
    public function testGetNonExistingEvent()
    {
        $repo = new EventRepository(self::$doctrine);

        $repo->getEvent(4);
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
}