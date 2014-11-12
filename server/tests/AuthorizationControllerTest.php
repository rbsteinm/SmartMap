<?php

use SmartMap\DBInterface\User;
use SmartMap\Control\UserRepository;
use SmartMap\Control\AuthorizationController;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Session\Session;
use Symfony\Component\HttpFoundation\Session\Storage\MockArraySessionStorage;

/** 
 * Tests for the AuthorizationController class.
 * To run them, run
 * $> phpunit --bootstrap vendor/autoload.php tests/AuthorizationControllerTest.php
 * from the server directory.
 *
 * @author Pamoi
 *
 */
class AuthorizationControllerTest extends PHPUnit_Framework_TestCase
{
    private $mockRepo;
    
    public function setUp()
    {
        $this->mockRepo = $this->getMockBuilder('SmartMap\DBInterface\UserRepository')
                               ->disableOriginalConstructor()
                               ->getMock();
    }
    
    public function testValidAllowFriend()
    {   
        $this->mockRepo->expects($this->once())
                       ->method('setFriendshipStatus')
                       ->with($this->equalTo(14), $this->equalTo(15), $this->equalTo('ALLOWED'));
        
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new AuthorizationController($this->mockRepo);
        
        $response = $controller->allowFriend($request);
        
        $validResponse = array('status' => 'Ok', 'message' => 'Allowed friend !');
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage The user is not authenticated.
     */
    public function testUnauthenticatedAllowFriend()
    {
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $request->setSession($session);
        
        $controller = new AuthorizationController($this->mockRepo);
        
        $controller->allowFriend($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage Post parameter friend_id is not set !
     */
    public function testInvalidParameterAllowFriend()
    {
        $request = new Request($query = array(), $request = array('false_param' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new AuthorizationController($this->mockRepo);
        
        $controller->allowFriend($request);
    }
    
    public function testValidDisallowFriend()
    {
        $this->mockRepo->expects($this->once())
                 ->method('setFriendshipStatus')
                 ->with($this->equalTo(14), $this->equalTo(15), $this->equalTo('DISALLOWED'));
    
        $request = new Request($query = array(), $request = array('friend_id' => 15));
    
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($this->mockRepo);
    
        $response = $controller->disallowFriend($request);
    
        $validResponse = array('status' => 'Ok', 'message' => 'Disallowed friend !');
    
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage The user is not authenticated.
     */
    public function testUnauthenticatedDisallowFriend()
    {
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $request->setSession($session);
    
        $controller = new AuthorizationController($this->mockRepo);
    
        $controller->disallowFriend($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage Post parameter friend_id is not set !
     */
    public function testInvalidParameterDisallowFriend()
    {
        $request = new Request($query = array(), $request = array('false_param' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($this->mockRepo);
    
        $controller->disallowFriend($request);
    }
    
    public function testValidAllowFriendList()
    {
        $this->mockRepo->expects($this->once())
                 ->method('setFriendshipsStatus')
                 ->with($this->equalTo(14), $this->equalTo(array(1,2,3,4,5)), $this->equalTo('ALLOWED'));
        
        $request = new Request($query = array(), $request = array('friend_ids' => '1,2 ,3,4, 5'));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new AuthorizationController($this->mockRepo);
        
        $response = $controller->allowFriendList($request);
        
        $validResponse = array('status' => 'Ok', 'message' => 'Allowed friend list !');
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage The user is not authenticated.
     */
    public function testUnauthenticatedAllowFriendList()
    {
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $request->setSession($session);
    
        $controller = new AuthorizationController($this->mockRepo);
    
        $controller->allowFriendList($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage Post parameter friend_id is not set !
     */
    public function testInvalidParameterAllowFriendList()
    {
        $request = new Request($query = array(), $request = array('false_param' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($this->mockRepo);
    
        $controller->allowFriend($request);
    }
    
    public function testValidDisallowFriendList()
    {
        $this->mockRepo->expects($this->once())
        ->method('setFriendshipsStatus')
        ->with($this->equalTo(14), $this->equalTo(array(1,2,3,4,5)), $this->equalTo('DISALLOWED'));
    
        $request = new Request($query = array(), $request = array('friend_ids' => ' 1, 2 ,3,4, 5'));
    
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($this->mockRepo);
    
        $response = $controller->disallowFriendList($request);
    
        $validResponse = array('status' => 'Ok', 'message' => 'Disallowed friend list !');
    
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage The user is not authenticated.
     */
    public function testUnauthenticatedDisallowFriendList()
    {
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $request->setSession($session);
    
        $controller = new AuthorizationController($this->mockRepo);
    
        $controller->disallowFriendList($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage Post parameter friend_ids is not set !
     */
    public function testInvalidParameterDisallowFriendList()
    {
        $request = new Request($query = array(), $request = array('false_param' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($this->mockRepo);
    
        $controller->disallowFriendList($request);
    }
    
    public function testValidFollowFriend()
    {
        $this->mockRepo->expects($this->once())
        ->method('setFriendshipFollow')
        ->with($this->equalTo(14), $this->equalTo(15), $this->equalTo('FOLLOWED'));
        
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new AuthorizationController($this->mockRepo);
        
        $response = $controller->followFriend($request);
        
        $validResponse = array('status' => 'Ok', 'message' => 'Followed friend !');
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage The user is not authenticated.
     */
    public function testUnauthenticatedFollowFriend()
    {
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $request->setSession($session);
        
        $controller = new AuthorizationController($this->mockRepo);
        
        $controller->followFriend($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage Post parameter friend_id is not set !
     */
    public function testInvalidParameterFollowFriend()
    {
        $request = new Request($query = array(), $request = array('false_param' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($this->mockRepo);
    
        $controller->followFriend($request);
    }
    
    public function testValidUnfollowFriend()
    {
        $this->mockRepo->expects($this->once())
        ->method('setFriendshipFollow')
        ->with($this->equalTo(14), $this->equalTo(15), $this->equalTo('UNFOLLOWED'));
    
        $request = new Request($query = array(), $request = array('friend_id' => 15));
    
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($this->mockRepo);
    
        $response = $controller->unfollowFriend($request);
    
        $validResponse = array('status' => 'Ok', 'message' => 'Unfollowed friend !');
    
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage The user is not authenticated.
     */
    public function testUnauthenticatedUnfollowFriend()
    {
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $request->setSession($session);
        
        $controller = new AuthorizationController($this->mockRepo);
        
        $controller->unfollowFriend($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage Post parameter friend_id is not set !
     */
    public function testInvalidParameterUnfollowFriend()
    {
        $request = new Request($query = array(), $request = array('false_param' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new AuthorizationController($this->mockRepo);
        
        $controller->unfollowFriend($request);
    }
}
