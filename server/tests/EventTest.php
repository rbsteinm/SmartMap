<?php

use SmartMap\DBInterface\Event;

/** Tests for the Event class.
 * To run them, run
 * $> phpunit --bootstrap vendor/autoload.php tests/EventTest.php
 * from the server directory.
 *
 * @author Pamoi
 *
 */
class EventTest extends PHPUnit_Framework_TestCase
{
    public function testGetters()
    {        
        $e = new Event(
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
        
        $this->assertEquals(1, $e->getId());
        $this->assertEquals(12345, $e->getCreatorId());
        $this->assertEquals('2014-11-18 08:15:00', $e->getStartingDate());
        $this->assertEquals('2014-11-18 08:40:00', $e->getEndingDate());
        $this->assertEquals(6.56186974, $e->getLongitude());
        $this->assertEquals(46.51895762, $e->getLatitude());
        $this->assertEquals('EPFL', $e->getPositionName());
        $this->assertEquals('Presentation of SmartMap app.', $e->getName());
        $this->assertEquals('SmartMap is an app that shows you your friends and events on a map.',
            $e->getDescription());   
    }
    
    public function testSetters()
    {
        $e = new Event(
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

        $e->setId(2);
        $e->setCreatorId(3);
        $e->setStartingDate('2014-11-18 08:15:59');
        $e->setEndingDate('2014-12-13 15:30:10');
        $e->setLongitude(176.4);
        $e->setLatitude(-87.2);
        $e->setPositionName('UNIL');
        $e->setName('Sheep meeting');
        $e->setDescription('Eating grass.');

        $this->assertEquals(2, $e->getId());
        $this->assertEquals(3, $e->getCreatorId());
        $this->assertEquals('2014-11-18 08:15:59', $e->getStartingDate());
        $this->assertEquals('2014-12-13 15:30:10', $e->getEndingDate());
        $this->assertEquals(176.4, $e->getLongitude());
        $this->assertEquals(-87.2, $e->getLatitude());
        $this->assertEquals('UNIL', $e->getPositionName());
        $this->assertEquals('Sheep meeting', $e->getName());
        $this->assertEquals('Eating grass.', $e->getDescription());
    }

    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Id must be greater than 0.
     */
    public function testInvalidId()
    {
        $e = new Event(
            0,
            12345,
            '2014-11-18 08:15:00',
            '2014-11-18 08:40:00',
            6.56186974,
            46.51895762,
            'EPFL',
            'Presentation of SmartMap app.',
            'SmartMap is an app that shows you your friends and events on a map.'
        );
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Id must be greater than 0.
     */
    public function testInvalidCreatorId()
    {
        $e = new Event(
            1,
            'toto lol',
            '2014-11-18 08:15:00',
            '2014-11-18 08:40:00',
            6.56186974,
            46.51895762,
            'EPFL',
            'Presentation of SmartMap app.',
            'SmartMap is an app that shows you your friends and events on a map.'
        );
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Invalid date format.
     */
    public function testInvalidStartingDate()
    {
        $e = new Event(
            1,
            12345,
            'two thousand two',
            '2014-11-18 08:40:00',
            6.56186974,
            46.51895762,
            'EPFL',
            'Presentation of SmartMap app.',
            'SmartMap is an app that shows you your friends and events on a map.'
        );
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Invalid date format.
     */
    public function testInvalidEndingDate()
    {
        $e = new Event(
            1,
            12345,
            '2014-11-18 08:40:00',
            'toto',
            6.56186974,
            46.51895762,
            'EPFL',
            'Presentation of SmartMap app.',
            'SmartMap is an app that shows you your friends and events on a map.'
        );
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Event ending date must be after starting date !
     */
    public function testInvalidEndingDateBeforeStartingDate()
    {
        $e = new Event(
            1,
            12345,
            '2014-11-18 09:00:00',
            '2014-11-18 08:00:00',
            6.56186974,
            46.51895762,
            'EPFL',
            'Presentation of SmartMap app.',
            'SmartMap is an app that shows you your friends and events on a map.'
        );
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Longitude must be between -180 and 180.
     */
    public function testInvalidPositiveLongitude()
    {
        $e = new Event(
            1,
            12345,
            '2014-11-18 09:00:00',
            '2014-11-18 08:00:00',
            182.3,
            1.0,
            'EPFL',
            'Presentation of SmartMap app.',
            'SmartMap is an app that shows you your friends and events on a map.'
        );
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Longitude must be between -180 and 180.
     */
    public function testInvalidNegativeLongitude()
    {
        $e = new Event(
            1,
            12345,
            '2014-11-18 09:00:00',
            '2014-11-18 08:00:00',
            -180.1,
            1.0,
            'EPFL',
            'Presentation of SmartMap app.',
            'SmartMap is an app that shows you your friends and events on a map.'
        );
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Latitude must be between -90 and 90.
     */
    public function testInvalidPositiveLatitude()
    {
        $e = new Event(
            1,
            12345,
            '2014-11-18 09:00:00',
            '2014-11-18 08:00:00',
            18.3,
            90.1,
            'EPFL',
            'Presentation of SmartMap app.',
            'SmartMap is an app that shows you your friends and events on a map.'
        );
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Latitude must be between -90 and 90.
     */
    public function testInvalidNegativeLatitude()
    {
        $e = new Event(
            1,
            12345,
            '2014-11-18 09:00:00',
            '2014-11-18 08:00:00',
            18.3,
            -90.1,
            'EPFL',
            'Presentation of SmartMap app.',
            'SmartMap is an app that shows you your friends and events on a map.'
        );
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Position name must be between 2 and 60 characters.
     */
    public function testTooShortPositionName()
    {
        $e = new Event(
            1,
            12345,
            '2014-11-18 09:00:00',
            '2014-11-18 08:00:00',
            1.0,
            2.0,
            'E',
            'Presentation of SmartMap app.',
            'SmartMap is an app that shows you your friends and events on a map.'
        );
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Position name must be between 2 and 60 characters.
     */
    public function testTooLongPositionName()
    {
        $e = new Event(
            1,
            12345,
            '2014-11-18 09:00:00',
            '2014-11-18 08:00:00',
            1.0,
            2.0,
            'EPFLAAAAAAAAAAAAAAAAAAAAAAAAAAAEPFLAAAAAAAAAAAAAAAAAAAAAAAAAAALOL',
            'Presentation of SmartMap app.',
            'SmartMap is an app that shows you your friends and events on a map.'
        );
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Event name must be between 2 and 60 characters.
     */
    public function testTooShortName()
    {
        $e = new Event(
            1,
            12345,
            '2014-11-18 09:00:00',
            '2014-11-18 08:00:00',
            1.0,
            2.0,
            'EPFL',
            '.',
            'SmartMap is an app that shows you your friends and events on a map.'
        );
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Event name must be between 2 and 60 characters.
     */
    public function testTooLongName()
    {
        $e = new Event(
            1,
            12345,
            '2014-11-18 09:00:00',
            '2014-11-18 08:00:00',
            1.0,
            2.0,
            'EPFL',
            'EPFLAAAAAAAAAAAAAAAAAAAAAAAAAAAEPFLAAAAAAAAAAAAAAAAAAAAAAAAAAA',
            'SmartMap is an app that shows you your friends and events on a map.'
        );
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Description must not be longer than 255 characters.
     */
    public function testTooLongDescription()
    {
        $e = new Event(
            1,
            12345,
            '2014-11-18 09:00:00',
            '2014-11-18 08:00:00',
            1.0,
            2.0,
            'EPFL',
            'Presentation of SmartMap app.',
            'SmartMap is an app that shows you your friends and events on a map.' .
            'SmartMap is an app that shows you your friends and events on a map.' .
            'SmartMap is an app that shows you your friends and events on a map.' .
            'SmartMap is an app that shows you your friends and events on a map.' .
            'SmartMap is an app that shows you your friends and events on a map.'
        );
    }

    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Event ending date must be after starting date !
     */
    public function testSetInvalidStartingDate()
    {
        $e = new Event(
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

        $e->setStartingDate('2014-11-19 00:00:00');
    }

    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Event ending date must be after starting date !
     */
    public function testSetInvalidEndingDate()
    {
        $e = new Event(
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

        $e->setEndingDate('2014-11-12 00:00:00');
    }

    // regression test for bug in checkDate
    public function testSetValidDate()
    {
        $e = new Event(
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

        $e->setEndingDate('2014-11-20 20:00:00');

        $this->assertEquals('2014-11-20 20:00:00', $e->getEndingDate());
    }

}