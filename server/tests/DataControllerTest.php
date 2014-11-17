<?php

use SmartMap\DBInterface\User;
use SmartMap\Control\UserRepository;
use SmartMap\Control\DataController;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Session\Session;
use Symfony\Component\HttpFoundation\Session\Storage\MockArraySessionStorage;

/** 
 * Tests for the DataController class.
 * To run them, run
 * $> phpunit --bootstrap vendor/autoload.php tests/DataControllerTest.php
 * from the server directory.
 *
 * @author Pamoi
 *
 */
class DataControllerTest extends PHPUnit_Framework_TestCase
{
    private $mockRepo;
    
    public function setUp()
    {
        $this->mockRepo = $this->getMockBuilder('SmartMap\DBInterface\UserRepository')
                               ->disableOriginalConstructor()
                               ->getMock();
    }
    
    public function testValidUpdatePos()
    {
        $returnUser = new User(14, 12345, 'Toto', 'VISIBLE', 1.0, 2.0);
        
        $this->mockRepo
             ->method('getUser')
             ->willReturn($returnUser);
        
        $this->mockRepo->expects($this->once())
             ->method('getUser')
             ->with($this->equalTo(14));
        
        $updatedUser = new User(14, 12345, 'Toto', 'VISIBLE', 15.0, -35.0);
        
        $this->mockRepo->expects($this->once())
             ->method('updateUser')
             ->with($this->equalTo($updatedUser));
        
        $request = new Request($query = array(), $request = array('longitude' => 15.0, 'latitude' => -35.0));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $response = $controller->updatePos($request);
        
        $validResponse = array('status' => 'Ok', 'message' => 'Updated position !');
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage Invalid coordinates.
     */
    public function testInvalidCoordinatesUpdatePos()
    {
        $returnUser = new User(14, 12345, 'Toto', 'VISIBLE', 1.0, 2.0);
        
        $this->mockRepo
             ->method('getUser')
             ->willReturn($returnUser);
        
        $request = new Request($query = array(), $request = array('longitude' => 200.0, 'latitude' => -35.0));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $controller->updatePos($request);
    }
    
    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage Post parameter longitude is not set !
     */
    public function testMissingPostParam()
    {
        $request = new Request();
    
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new DataController($this->mockRepo);
    
        $response = $controller->updatePos($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in updatePos.
     */
    public function testDatabaseErrorUpdatePos()
    {
        $this->mockRepo
             ->method('getUser')
             ->will($this->throwException(new SmartMap\DBInterface\DatabaseException('Nasty error')));
        
        $request = new Request($query = array(), $request = array('longitude' => 100.0, 'latitude' => -35.0));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $controller->updatePos($request);
    }
    
    public function testValidListFriendPos()
    {
        $friendsIds = array(1, 2);
        
        $returnUsers = array(
            new User(1, 2, 'Toto', 'VISIBLE', 1.0, 2.0, '2014-11-12 13:33:45'),
            new User(2, 3, 'Titi', 'VISIBLE', 3.0, 4.0, '2014-11-13 01:56:22')
        );
        
        $this->mockRepo
             ->method('getFriendsIds')
             ->willReturn($friendsIds);
        
        $this->mockRepo->expects($this->once())
             ->method('getFriendsIds')
             ->with($this->equalTo(14), $this->equalTo(array('ALLOWED')), $this->equalTo(array('FOLLOWED')));
        
        $this->mockRepo
             ->method('getUsers')
             ->willReturn($returnUsers);
        
        $this->mockRepo->expects($this->once())
             ->method('getUsers')
             ->with($this->equalTo($friendsIds));
        
        $request = new Request();
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $response = $controller->listFriendsPos($request);
        
        $list = array(
            array('id' => 1, 'longitude' => 1.0, 'latitude' => 2.0, 'lastUpdate' => '2014-11-12 13:33:45'),
            array('id' => 2, 'longitude' => 3.0, 'latitude' => 4.0, 'lastUpdate' => '2014-11-13 01:56:22')
        );
        
        $validResponse = array('status' => 'Ok', 'message' => 'Fetched friends positions !', 'positions' => $list);
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    public function testValidGetUserInfo()
    {
        $returnUser = new User(15, 12345, 'Toto', 'VISIBLE', 1.0, 2.0);
        
        $this->mockRepo
             ->method('getUser')
             ->willReturn($returnUser);
        
        $this->mockRepo->expects($this->once())
             ->method('getUser')
             ->with($this->equalTo(15));
        
        $request = new Request($query = array(), $request = array('user_id' => 15));
        
        $controller = new DataController($this->mockRepo);
        
        $response = $controller->getUserInfo($request);
        
        $validResponse = array('status' => 'Ok', 'message' => 'Fetched user info !', 'id' => 15, 'name' => 'Toto');
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    public function testValidInviteFriend()
    {
        $this->mockRepo
             ->method('getFriendsIds')
             ->willReturn(array(1,2));
        
        $this->mockRepo->expects($this->once())
             ->method('getFriendsIds')
             ->with($this->equalTo(14));
        
        $this->mockRepo
             ->method('getInvitationIds')
             ->willReturn(array());
        
        $this->mockRepo->expects($this->exactly(2))
             ->method('getInvitationIds')
             ->withConsecutive(
                 $this->equalTo(14),
                 $this->equalTo(15)
             );
        
        $this->mockRepo->expects($this->once())
             ->method('addInvitation')
             ->with($this->equalTo(14), $this->equalTo(15));
        
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $response = $controller->inviteFriend($request);
        
        $validResponse = array('status' => 'Ok', 'message' => 'Invited friend !');
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage You cannot invite yourself !
     */
    public function testInvalidSelfInviteFriend()
    {
        $request = new Request($query = array(), $request = array('friend_id' => 14));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $response = $controller->inviteFriend($request);
    }
    
    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage You are already friends or invited.
     */
    public function testInvalidInviteFriend()
    {
        $this->mockRepo
             ->method('getFriendsIds')
             ->willReturn(array(1,2));
        
        $this->mockRepo->expects($this->once())
             ->method('getFriendsIds')
             ->with($this->equalTo(14));
        
        $this->mockRepo
             ->method('getInvitationIds')
             ->willReturn(array());
        
        $this->mockRepo->expects($this->exactly(2))
             ->method('getInvitationIds')
             ->withConsecutive(
                $this->equalTo(14),
                $this->equalTo(15)
             );
        
        $request = new Request($query = array(), $request = array('friend_id' => 1));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $controller->inviteFriend($request);
    }
    
    public function testValidGetInvitations()
    {
        $invitIds = array(1, 2);
        
        $returnUsers = array(
            new User(1, 2, 'Toto', 'VISIBLE', 1.0, 2.0),
            new User(2, 3, 'Titi', 'VISIBLE', 3.0, 4.0)
        );
        
        $acceptedInvitationIds = array(3);
        
        $acceptingUsers = array(new User(3, 4, 'Tutu', 'VISIBLE', 5.0, 6.0, '2014-11-12 13:33:45'));
        
        $this->mockRepo
             ->method('getInvitationIds')
             ->willReturn($invitIds);
        
        $this->mockRepo->expects($this->once())
             ->method('getInvitationIds')
             ->with($this->equalTo(14));
        
        $this->mockRepo
             ->method('getAcceptedInvitations')
             ->willReturn($acceptedInvitationIds);
        
        $this->mockRepo->expects($this->once())
             ->method('getAcceptedInvitations')
             ->with($this->equalTo(14));
        
        $this->mockRepo
             ->method('getUsers')
             ->will($this->onConsecutiveCalls($returnUsers, $acceptingUsers));
        
        $this->mockRepo->expects($this->exactly(2))
             ->method('getUsers')
             ->withConsecutive(
                 array($this->equalTo($invitIds)),
                 array($this->equalTo($acceptedInvitationIds))
             );
        
        $request = new Request();
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $response = $controller->getInvitations($request);
        
        $invitations = array(
            array('id' => 1, 'name' => 'Toto'),
            array('id' => 2, 'name' => 'Titi')
        );
        
        $newFriends = array(
            array('id' => 3,
                'name' => 'Tutu',
                'longitude' => 5.0,
                'latitude' => 6.0,
                'lastUpdate' => '2014-11-12 13:33:45'
            ));
        
        $validResponse = array(
            'status' => 'Ok',
            'message' => 'Fetched invitations !',
            'invitations' => $invitations,
            'newFriends' => $newFriends
        );
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    public function testValidAckAcceptedInvitation()
    {
        $this->mockRepo->expects($this->once())
             ->method('removeAcceptedInvitation')
             ->with($this->equalTo(14), $this->equalTo(1));
        
        $request = new Request($query = array(), $request = array('friend_id' => 1));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $response = $controller->ackAcceptedInvitation($request);
        
        $validResponse = array('status' => 'Ok', 'message' => 'Acknowledged accepted invitation !');
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    public function testValidAcceptInvitation()
    {
        $invitIds = array(1, 2);
        
        $friend = new User(1, 2, 'Toto', 'VISIBLE', 1.0, 2.0, '2014-11-12 13:33:45');
        
        $this->mockRepo
             ->method('getInvitationIds')
             ->willReturn($invitIds);
        
        $this->mockRepo->expects($this->once())
             ->method('getInvitationIds')
             ->with($this->equalTo(14));
        
        $this->mockRepo->expects($this->once())
             ->method('removeInvitation')
             ->with($this->equalTo(1), $this->equalTo(14));
        
        $this->mockRepo->expects($this->exactly(2))
             ->method('addFriendshipLink')
             ->withConsecutive(
                array($this->equalTo(14), $this->equalTo(1)),
                array($this->equalTo(1), $this->equalTo(14))
             );
        
        $this->mockRepo
             ->method('getUser')
             ->willReturn($friend);
        
        $this->mockRepo->expects($this->once())
             ->method('getUser')
             ->with($this->equalTo(1));
        
        $this->mockRepo->expects($this->once())
             ->method('addAcceptedInvitation')
             ->with($this->equalTo(14), $this->equalTo(1));
        
        $request = new Request($query = array(), $request = array('friend_id' => 1));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $response = $controller->acceptInvitation($request);
        
        $validResponse = array(
            'status' => 'Ok',
            'message' => 'Accepted invitation !',
            'id' => 1,
            'name' => 'Toto',
            'longitude' => 1.0,
            'latitude' => 2.0,
            'lastUpdate' => '2014-11-12 13:33:45'
        );
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage Not invited by user with id 3 !
     */
    public function testNonInvitedAcceptInvitation()
    {
        $invitIds = array(1, 2);
        
        $this->mockRepo
             ->method('getInvitationIds')
             ->willReturn($invitIds);
        
        $this->mockRepo->expects($this->once())
             ->method('getInvitationIds')
             ->with($this->equalTo(14));
        
        $request = new Request($query = array(), $request = array('friend_id' => 3));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $controller->acceptInvitation($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in acceptInvitation.
     */
    public function testAlreadyFriendAcceptInvitation()
    {
        $invitIds = array(1, 2);
        
        $this->mockRepo
             ->method('getInvitationIds')
             ->willReturn($invitIds);
        
        $this->mockRepo->expects($this->once())
             ->method('getInvitationIds')
             ->with($this->equalTo(14));
        
        $this->mockRepo->expects($this->once())
             ->method('removeInvitation')
             ->with($this->equalTo(1), $this->equalTo(14));
        
        $this->mockRepo
             ->method('addFriendshipLink')
             ->will($this->throwException(new SmartMap\DBInterface\DatabaseException(
                 'This friendship link already exists !')));
        
        $request = new Request($query = array(), $request = array('friend_id' => 1));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $controller->acceptInvitation($request);
    }
    
    public function testValidDeclineInvitation()
    {
        $this->mockRepo->expects($this->once())
             ->method('removeInvitation')
             ->with($this->equalTo(1), $this->equalTo(14));
        
        $request = new Request($query = array(), $request = array('friend_id' => 1));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $response = $controller->declineInvitation($request);
        
        $validResponse = array('status' => 'Ok', 'message' => 'Declined invitation !');
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    public function testValidRemoveFriend()
    {
        $this->mockRepo->expects($this->exactly(2))
             ->method('removeFriendshipLink')
             ->withConsecutive(
                 array($this->equalTo(14), $this->equalTo(1)),
                 array($this->equalTo(1), $this->equalTo(14))
                 );
        
        $request = new Request($query = array(), $request = array('friend_id' => 1));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $response = $controller->removeFriend($request);
        
        $validResponse = array('status' => 'Ok', 'message' => 'Removed friend !');
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    public function testValidFindUsers()
    {
        $returnUsers = array(
            new User(1, 2, 'Toto', 'VISIBLE', 1.0, 2.0),
            new User(2, 3, 'Titi', 'VISIBLE', 3.0, 4.0)
        );
        
        $this->mockRepo
             ->method('getFriendsIds')
             ->willReturn(array(3, 4));
        
        $this->mockRepo->expects($this->once())
             ->method('getFriendsIds')
             ->with($this->equalTo(14));
        
        $this->mockRepo
             ->method('findUsersByPartialName')
             ->willReturn($returnUsers);
        
        $this->mockRepo->expects($this->once())
             ->method('findUsersByPartialName')
             ->with($this->equalTo('t'), $this->equalTo(array(3, 4)));
        
        $request = new Request($query = array(), $request = array('search_text' => 't'));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $response = $controller->findUsers($request);
        
        $list = array(
            array('id' => 1, 'name' => 'Toto'),
            array('id' => 2, 'name' => 'Titi')
        );
        
        $validResponse = array('status' => 'Ok', 'message' => 'Fetched users !', 'list' => $list);
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
}