<?php

use SmartMap\DBInterface\Event;
use SmartMap\Control\EventController;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Session\Session;
use Symfony\Component\HttpFoundation\Session\Storage\MockArraySessionStorage;

/**
 * Tests for the EventController class.
 * To run them, run
 * $> phpunit --bootstrap vendor/autoload.php tests/EventControllerTest.php
 * from the server directory.
 *
 * @author Pamoi
 *
 */
class EventControllerTest extends PHPUnit_Framework_TestCase
{
    private $mockRepo;

    private $mValidEvent; // This field is public so it can be returned by the mockRepo

    public function setUp()
    {
        $this->mockRepo = $this->getMockBuilder('SmartMap\DBInterface\EventRepository')
            ->disableOriginalConstructor()
            ->getMock();

        $this->mValidEvent = new Event(
            1,
            123,
            '2014-11-18 23:05:22',
            '2014-11-23 10:34:34',
            46.05647,
            6.84657,
            'EPFL',
            'Fondue au soleil',
            ''
        );
    }

    public function testCreateEvent()
    {
        $this->mockRepo->expects($this->once())
             ->method('createEvent')
             ->with($this->equalTo($this->mValidEvent));

        $request = new Request($query = array(), $request = array(
            'starting' => '2014-11-18 23:05:22',
            'ending' => '2014-11-23 10:34:34',
            'longitude' => '46.05647',
            'latitude' => '6.84657',
            'positionName' => 'EPFL',
            'name' => 'Fondue au soleil',
            'description' => ''
        ));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 123);
        $request->setSession($session);

        $controller = new EventController($this->mockRepo);

        $response = $controller->createEvent($request);

        $validResponse = array('status' => 'Ok', 'message' => 'Created event.');

        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }

    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage Event ending date must be after starting date !
     */
    public function testInvalidParametersCreateEvent()
    {
        $request = new Request($query = array(), $request = array(
            'starting' => '2014-11-18 23:05:22',
            'ending' => '2014-11-11 10:34:34',
            'longitude' => '46.05647',
            'latitude' => '6.84657',
            'positionName' => 'EPFL',
            'name' => 'Fondue au soleil',
            'description' => ''
        ));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 123);
        $request->setSession($session);

        $controller = new \SmartMap\Control\EventController($this->mockRepo);

        $controller->createEvent($request);
    }

    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage Post parameter name is not set !
     */
    public function testMissingParameterCreateEvent()
    {
        $request = new Request($query = array(), $request = array(
            'starting' => '2014-11-18 23:05:22',
            'ending' => '2014-11-11 10:34:34',
            'longitude' => '46.05647',
            'latitude' => '6.84657',
            'positionName' => 'EPFL',
            'description' => ''
        ));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 123);
        $request->setSession($session);

        $controller = new \SmartMap\Control\EventController($this->mockRepo);

        $controller->createEvent($request);
    }

    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in createEvent.
     */
    public function testCreateUserDBException()
    {
        $this->mockRepo
            ->method('createEvent')
            ->will($this->throwException(new \SmartMap\DBInterface\DatabaseException('Oh no !')));

        $request = new Request($query = array(), $request = array(
            'starting' => '2014-11-18 23:05:22',
            'ending' => '2014-11-23 10:34:34',
            'longitude' => '46.05647',
            'latitude' => '6.84657',
            'positionName' => 'EPFL',
            'name' => 'Fondue au soleil',
            'description' => ''
        ));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 123);
        $request->setSession($session);

        $controller = new EventController($this->mockRepo);

        $controller->createEvent($request);
    }

    public function testUpdateEvent()
    {
        $returnEvent = new Event(
            1,
            123,
            '2014-11-18 23:05:22',
            '2014-11-23 10:34:34',
            46.05647,
            6.84657,
            'EPFL',
            'Fondue au soleil',
            ''
        );

        $this->mockRepo
            ->method('getEvent')
            ->willReturn($returnEvent);

        $this->mockRepo->expects($this->once())
             ->method('getEvent')
             ->with($this->equalTo($this->mValidEvent->getId()));

        $this->mockRepo->expects($this->once())
             ->method('updateEvent')
             ->with($this->equalTo($this->mValidEvent
                 ->setStartingDate('2014-11-18 23:05:25')
                 ->setName('Toto')
                 ->setLatitude(2.5)
                 ->setDescription('Très bon !')
                 ->setEndingDate('2014-11-23 11:34:34')
                 ->setLongitude('46.05647')
                 ->setPositionName('Ici')
             ));

        $request = new Request($query = array(), $request = array(
            'eventId' => $this->mValidEvent->getId(),
            'starting' => '2014-11-18 23:05:25',
            'ending' => '2014-11-23 11:34:34',
            'longitude' => '46.05647',
            'latitude' => '2.5',
            'positionName' => 'Ici',
            'name' => 'Toto',
            'description' => 'Très bon !'
        ));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 123);
        $request->setSession($session);

        $controller = new EventController($this->mockRepo);

        $response = $controller->updateEvent($request);

        $validResponse = array('status' => 'Ok', 'message' => 'Updated event.');

        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }

    public function testPartialUpdateEvent()
    {
        $returnEvent = new Event(
            1,
            123,
            '2014-11-18 23:05:22',
            '2014-11-23 10:34:34',
            46.05647,
            6.84657,
            'EPFL',
            'Fondue au soleil',
            ''
        );

        $this->mockRepo
            ->method('getEvent')
            ->willReturn($returnEvent);

        $this->mockRepo->expects($this->once())
            ->method('getEvent')
            ->with($this->equalTo($this->mValidEvent->getId()));

        $this->mockRepo->expects($this->once())
            ->method('updateEvent')
            ->with($this->equalTo($this->mValidEvent
                    ->setName('Toto')
                    ->setDescription('Très bon !')
            ));

        $request = new Request($query = array(), $request = array(
            'eventId' => $this->mValidEvent->getId(),
            'name' => 'Toto',
            'description' => 'Très bon !'
        ));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 123);
        $request->setSession($session);

        $controller = new EventController($this->mockRepo);

        $response = $controller->updateEvent($request);

        $validResponse = array('status' => 'Ok', 'message' => 'Updated event.');

        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }

    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage Event name must be between 2 and 60 characters.
     */
    public function testInvalidParameterUpdateEvent()
    {
        $this->mockRepo
            ->method('getEvent')
            ->willReturn($this->mValidEvent);

        $this->mockRepo->expects($this->once())
            ->method('getEvent')
            ->with($this->equalTo($this->mValidEvent->getId()));

        $request = new Request($query = array(), $request = array(
            'eventId' => $this->mValidEvent->getId(),
            'name' => 'T', // Too short !
            'description' => 'Très bon !'
        ));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 123);
        $request->setSession($session);

        $controller = new EventController($this->mockRepo);

        $controller->updateEvent($request);
    }

    /**
     * @expectedException SmartMap\Control\ServerFeedbackException
     * @expectedExceptionMessage You cannot edit this event.
     */
    public function testNotAllowedUpdateEvent()
    {
        $this->mockRepo
            ->method('getEvent')
            ->willReturn($this->mValidEvent);

        $this->mockRepo->expects($this->once())
            ->method('getEvent')
            ->with($this->equalTo($this->mValidEvent->getId()));

        $request = new Request($query = array(), $request = array(
            'eventId' => $this->mValidEvent->getId(),
            'name' => 'Toto',
            'description' => 'Très bon !'
        ));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 124); // Not the creator id of the event !
        $request->setSession($session);

        $controller = new EventController($this->mockRepo);

        $controller->updateEvent($request);
    }

    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in updateEvent.
     */
    public function testDBExceptionUpdateEvent()
    {
        $this->mockRepo
            ->method('getEvent')
            ->will($this->throwException(new \SmartMap\DBInterface\DatabaseException('Event does not exist.')));

        $request = new Request($query = array(), $request = array(
            'eventId' => $this->mValidEvent->getId()
        ));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 123);
        $request->setSession($session);

        $controller = new EventController($this->mockRepo);

        $controller->updateEvent($request);
    }

    public function testGetPublicEvents()
    {

        $this->mockRepo
             ->method('getEventsInRadius')
             ->willReturn($this->mValidEvent);

        $this->mockRepo->expects($this->once())
             ->method('getEventsInRadius')
             ->with($this->equalTo(1.02), $this->equalTo(5.23), $this->equalTo(100));

        $request = new Request($query = array(), $request = array(
            'radius' => '100',
            'longitude' => '1.02',
            'latitude' => '5.23'
        ));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 123);
        $request->setSession($session);

        $controller = new EventController($this->mockRepo);

        $response = $controller->getPublicEvents($request);

        $validResponse = array(
            'status' => 'Ok',
            'message' => 'Fetched events.',
            'events' => array(
                'id' => $this->mValidEvent->getId(),
                'creatorId' => $this->mValidEvent->getCreatorId(),
                'startingDate' => $this->mValidEvent->getStartingDate(),
                'endingDate' => $this->mValidEvent->getEndingDate(),
                'longitude' => $this->mValidEvent->getLongitude(),
                'latitude' => $this->mValidEvent->getLatitude(),
                'positionName' => $this->mValidEvent->getPositionName(),
                'name' => $this->mValidEvent->getName(),
                'description' => $this->mValidEvent->getDescription()
            )
        );
    }
}
