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
    
    public function testValidListFreindPos()
    {
        $friendsIds = array(1, 2);
        
        $returnUsers = array(
            new User(1, 2, 'Toto', 'VISIBLE', 1.0, 2.0),
            new User(2, 3, 'Titi', 'VISIBLE', 3.0, 4.0)
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
            array('id' => 1, 'longitude' => 1.0, 'latitude' => 2.0),
            array('id' => 2, 'longitude' => 3.0, 'latitude' => 4.0)
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
    
    public function testInvalidInviteFriend()
    {
        // TODO
    }
    
    public function gestValidGetInvitations()
    {
        $invitIds = array(1, 2);
        
        $returnUsers = array(
            new User(1, 2, 'Toto', 'VISIBLE', 1.0, 2.0),
            new User(2, 3, 'Titi', 'VISIBLE', 3.0, 4.0)
        );
        
        $this->mockRepo
             ->method('getInvitationIds')
             ->willReturn($invitIds);
        
        $this->mockRepo->expects($this->once())
             ->method('getInvitationIds')
             ->with($this->equalTo(14));
        
        $this->mockRepo
             ->method('getUsers')
             ->willReturn($returnUsers);
        
        $this->mockRepo->expects($this->once())
             ->method('getUsers')
             ->with($this->equalTo($invitIds));
        
        $request = new Request();
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new DataController($this->mockRepo);
        
        $response = $controller->inviteFriend($request);
        
        $list = array(
            array('id' => 1, 'name' => 'Toto'),
            array('id' => 2, 'name' => 'Titi')
        );
        
        $validResponse = array('status' => 'Ok', 'message' => 'Fetched invitations !', 'list' => $list);
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    public function testValidAcceptInvitation()
    {
        $invitIds = array(1, 2);
        
        $friend = new User(1, 2, 'Toto', 'VISIBLE', 1.0, 2.0);
        
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
            'latitude' => 2.0
        );
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    public function testValidFindUsers()
    {
        $returnUsers = array(
            new User(1, 2, 'Toto', 'VISIBLE', 1.0, 2.0),
            new User(2, 3, 'Titi', 'VISIBLE', 3.0, 4.0)
        );
        
        $this->mockRepo
             ->method('findUsersByPartialName')
             ->willReturn($returnUsers);
        
        $this->mockRepo->expects($this->once())
             ->method('findUsersByPartialName')
             ->with($this->equalTo('t'));
        
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
    }
}