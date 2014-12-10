<?php

use SmartMap\DBInterface\Event;
use SmartMap\DBInterface\User;
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
    private $mockEventRepo;
    private $mockUserRepo;

    private $mValidEvent;

    public function setUp()
    {
        $this->mockEventRepo = $this->getMockBuilder('SmartMap\DBInterface\EventRepository')
             ->disableOriginalConstructor()
             ->getMock();

        $this->mockUserRepo = $this->getMockBuilder('SmartMap\DBInterface\UserRepository')
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
        $returnEvent = new Event(
            14,
            123,
            '2014-11-18 23:05:22',
            '2014-11-23 10:34:34',
            46.05647,
            6.84657,
            'EPFL',
            'Fondue au soleil',
            ''
        );

        $this->mockEventRepo
             ->method('createEvent')
             ->willReturn($returnEvent);

        $this->mockEventRepo->expects($this->once())
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

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $response = $controller->createEvent($request);

        $validResponse = array('status' => 'Ok', 'message' => 'Created event.', 'id' => 14);

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

        $controller = new \SmartMap\Control\EventController($this->mockEventRepo, $this->mockUserRepo);

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

        $controller = new \SmartMap\Control\EventController($this->mockEventRepo, $this->mockUserRepo);

        $controller->createEvent($request);
    }

    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in createEvent.
     */
    public function testCreateUserDBException()
    {
        $this->mockEventRepo
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

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

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

        $this->mockEventRepo
            ->method('getEvent')
            ->willReturn($returnEvent);

        $this->mockEventRepo->expects($this->once())
             ->method('getEvent')
             ->with($this->equalTo($this->mValidEvent->getId()));

        $this->mockEventRepo->expects($this->once())
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

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

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

        $this->mockEventRepo
            ->method('getEvent')
            ->willReturn($returnEvent);

        $this->mockEventRepo->expects($this->once())
            ->method('getEvent')
            ->with($this->equalTo($this->mValidEvent->getId()));

        $this->mockEventRepo->expects($this->once())
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

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

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
        $this->mockEventRepo
            ->method('getEvent')
            ->willReturn($this->mValidEvent);

        $this->mockEventRepo->expects($this->once())
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

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $controller->updateEvent($request);
    }

    /**
     * @expectedException SmartMap\Control\ServerFeedbackException
     * @expectedExceptionMessage You cannot edit this event.
     */
    public function testNotAllowedUpdateEvent()
    {
        $this->mockEventRepo
            ->method('getEvent')
            ->willReturn($this->mValidEvent);

        $this->mockEventRepo->expects($this->once())
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

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $controller->updateEvent($request);
    }

    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in updateEvent.
     */
    public function testDBExceptionUpdateEvent()
    {
        $this->mockEventRepo
            ->method('getEvent')
            ->will($this->throwException(new \SmartMap\DBInterface\DatabaseException('Event does not exist.')));

        $request = new Request($query = array(), $request = array(
            'eventId' => $this->mValidEvent->getId()
        ));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 123);
        $request->setSession($session);

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $controller->updateEvent($request);
    }

    public function testGetPublicEvents()
    {
        $eventsIds = array(5, 22, 54);

        $this->mockEventRepo
             ->method('getEventsInRadius')
             ->willReturn($eventsIds);

        $this->mockEventRepo->expects($this->once())
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

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $response = $controller->getPublicEvents($request);

        $validResponse = array(
            'status' => 'Ok',
            'message' => 'Fetched events.',
            'events' => $eventsIds
        );

        $this->assertEquals(json_encode($validResponse), $response->getContent());
    }

    public function testJoinEvent()
    {
        $this->mockEventRepo->expects($this->once())
             ->method('addUserToEvent')
             ->with($this->equalTo(1), $this->equalTo(14));

        $request = new Request($query = array(), $request = array('event_id' => 1));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $response = $controller->joinEvent($request);

        $validResponse = array('status' => 'Ok', 'message' => 'Event joined.');

        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }

    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in joinEvent.
     */
    public function testJoinEventDBException()
    {
        $this->mockEventRepo
            ->method('addUserToEvent')
            ->will($this->throwException(new \SmartMap\DBInterface\DatabaseException()));

        $request = new Request($query = array(), $request = array('event_id' => 1));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $controller->joinEvent($request);
    }

    public function testLeaveEvent()
    {
        $this->mockEventRepo->expects($this->once())
            ->method('removeUserFromEvent')
            ->with($this->equalTo(1), $this->equalTo(14));

        $request = new Request($query = array(), $request = array('event_id' => 1));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $response = $controller->leaveEvent($request);

        $validResponse = array('status' => 'Ok', 'message' => 'Event left.');

        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }

    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in leaveEvent.
     */
    public function testLeaveEventDBException()
    {
        $this->mockEventRepo
            ->method('removeUserFromEvent')
            ->will($this->throwException(new \SmartMap\DBInterface\DatabaseException()));

        $request = new Request($query = array(), $request = array('event_id' => 1));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $controller->leaveEvent($request);
    }

    public function testInviteUsersToEvent()
    {
        $this->mockEventRepo->expects($this->once())
             ->method('addEventInvitations')
             ->with($this->equalTo(3), $this->equalTo(array(1, 12, 14)));

        $request = new Request($query = array(), $request = array('event_id' => 3, 'users_ids' => '1,12,14'));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $response = $controller->inviteUsersToEvent($request);

        $validResponse = array('status' => 'Ok', 'message' => 'Invited users.');

        $this->assertEquals(json_encode($validResponse), $response->getContent());
    }

    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in inviteUsersToEvent.
     */
    public function testInviteUsersDBException()
    {
        $this->mockEventRepo
            ->method('addEventInvitations')
            ->will($this->throwException(new \SmartMap\DBInterface\DatabaseException()));

        $request = new Request($query = array(), $request = array('event_id' => 1, 'users_ids' => '1,12,14'));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $controller->inviteUsersToEvent($request);
    }

    public function testGetEventInvitations()
    {
        $creator = new User(1, 2, 'Toto', 'VISIBLE', 1.0, 2.0);

        $this->mockEventRepo
             ->method('getEventInvitations')
             ->willReturn(array(5, 7, 9));

        $this->mockEventRepo->expects($this->once())
             ->method('getEventInvitations')
             ->with($this->equalTo(14));

        $this->mockEventRepo
             ->method('getEvent')
             ->will($this->onConsecutiveCalls($this->mValidEvent, $this->mValidEvent, $this->mValidEvent));

        $this->mockEventRepo->expects($this->exactly(3))
             ->method('getEvent')
             ->withConsecutive(
                array($this->equalTo(5)),
                array($this->equalTo(7)),
                array($this->equalTo(9)));

        $this->mockEventRepo
             ->method('getEventParticipants')
             ->will($this->onConsecutiveCalls(array(7, 9), array(1), array()));

        $this->mockEventRepo->expects($this->exactly(3))
             ->method('getEventParticipants')
             ->withConsecutive(
                array($this->equalTo(5)),
                array($this->equalTo(7)),
                array($this->equalTo(9)));

        $this->mockUserRepo
             ->method('getUser')
             ->willReturn($creator);

        $this->mockUserRepo->expects($this->exactly(3))
             ->method('getUser')
             ->withConsecutive(
                 array($this->equalTo($this->mValidEvent->getCreatorId())),
                 array($this->equalTo($this->mValidEvent->getCreatorId())),
                 array($this->equalTo($this->mValidEvent->getCreatorId())));

        $request = new Request($query = array(), $request = array());
        $session = new Session(new MockArraySessionStorage());
        $session->set('userId', 14);

        $request->setSession($session);

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);
        $response = $controller->getEventInvitations($request);

        $validCreator = array('id' => 1,'name' => 'Toto');

        $event = array(
            'id' => $this->mValidEvent->getId(),
            'creator' => $validCreator,
            'startingDate' => $this->mValidEvent->getStartingDate(),
            'endingDate' => $this->mValidEvent->getEndingDate(),
            'longitude' => $this->mValidEvent->getLongitude(),
            'latitude' => $this->mValidEvent->getLatitude(),
            'positionName' => $this->mValidEvent->getPositionName(),
            'name' => $this->mValidEvent->getName(),
            'description' => $this->mValidEvent->getDescription(),
        );

        $eventsList = array();

        $event1 = $event;
        $event1['participants'] = array(7, 9);
        $eventsList[] = $event1;

        $event2 = $event;
        $event2['participants'] = array(1);
        $eventsList[] = $event2;

        $event3 = $event;
        $event3['participants'] = array();
        $eventsList[] = $event3;

        $validResponse = array(
            'status' => 'Ok',
            'message' => 'Fetched events.',
            'events' => $eventsList
        );
        $this->assertEquals(json_encode($validResponse), $response->getContent());
    }

    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in getEventInvitations.
     */
    public function testGetEventInvitationsDBException()
    {
        $this->mockEventRepo
             ->method('getEventInvitations')
             ->will($this->throwException(new \SmartMap\DBInterface\DatabaseException('Toto')));

        $request = new Request($query = array(), $request = array());

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $controller->getEventInvitations($request);
    }

    public function testAckEventInvitation()
    {
        $this->mockEventRepo->expects($this->once())
             ->method('removeEventInvitation')
             ->with($this->equalTo(36), $this->equalTo(14));

        $request = new Request($query = array(), $request = array('event_id' => 36));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $response = $controller->ackEventInvitation($request);

        $validResponse = array('status' => 'Ok', 'message' => 'Acknowledged event invitation.');

        $this->assertEquals(json_encode($validResponse), $response->getContent());
    }

    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in ackEventInvitation.
     */
    public function testAckEventInvitationDBException()
    {
        $this->mockEventRepo
            ->method('removeEventInvitation')
            ->will($this->throwException(new \SmartMap\DBInterface\DatabaseException()));

        $request = new Request($query = array(), $request = array('event_id' => 36));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $controller->ackEventInvitation($request);
    }

    public function testGetEventInfo()
    {
        $creator = new User(1, 2, 'Toto', 'VISIBLE', 1.0, 2.0);

        $this->mockEventRepo
             ->method('getEvent')
             ->willReturn($this->mValidEvent);

        $this->mockEventRepo->expects($this->once())
            ->method('getEvent')
            ->with($this->equalTo(36));

        $this->mockEventRepo
             ->method('getEventParticipants')
             ->willReturn(array(1,2,3));

        $this->mockEventRepo->expects($this->once())
             ->method('getEventParticipants')
             ->with($this->equalTo(36));

        $this->mockUserRepo
             ->method('getUser')
             ->willReturn($creator);

        $this->mockUserRepo->expects($this->once())
             ->method('getUser')
             ->with($this->equalTo($this->mValidEvent->getCreatorId()));

        $request = new Request($query = array(), $request = array('event_id' => 36));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $response = $controller->getEventInfo($request);

        $validCreator = array('id' => 1,'name' => 'Toto');

        $event = array(
            'id' => $this->mValidEvent->getId(),
            'creator' => $validCreator,
            'startingDate' => $this->mValidEvent->getStartingDate(),
            'endingDate' => $this->mValidEvent->getEndingDate(),
            'longitude' => $this->mValidEvent->getLongitude(),
            'latitude' => $this->mValidEvent->getLatitude(),
            'positionName' => $this->mValidEvent->getPositionName(),
            'name' => $this->mValidEvent->getName(),
            'description' => $this->mValidEvent->getDescription(),
        );
        $event['participants'] = array(1,2,3);

        $validResponse = array('status' => 'Ok', 'message' => 'Fetched event.', 'event' => $event);

        $this->assertEquals(json_encode($validResponse), $response->getContent());
    }

    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in getEventInfo.
     */
    public function testGetEventInfoDBException()
    {
        $this->mockEventRepo
            ->method('getEvent')
            ->will($this->throwException(new \SmartMap\DBInterface\DatabaseException()));

        $request = new Request($query = array(), $request = array('event_id' => 36));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new EventController($this->mockEventRepo, $this->mockUserRepo);

        $controller->getEventInfo($request);
    }
}
