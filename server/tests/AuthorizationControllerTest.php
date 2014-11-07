<?php

use SmartMap\DBInterface\User;
use SmartMap\Control\AuthorizationController;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Session\Session;
use Symfony\Component\HttpFoundation\Session\Storage\MockArraySessionStorage;

class AuthorizationControllerTest extends PHPUnit_Framework_TestCase
{
    public function testValidAllowFriend()
    {
        $mockRepo = $this->getMock('UserRepository', array('setFriendshipStatus'));
        $mockRepo->expects($this->once())
                 ->method('setFriendshipStatus')
                 ->with($this->equalTo(14), $this->equalTo(15), $this->equalTo('ALLOWED'));
        
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new AuthorizationController($mockRepo);
        
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
        $mockRepo = $this->getMock('UserRepository');
        
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $request->setSession($session);
        
        $controller = new AuthorizationController($mockRepo);
        
        $controller->allowFriend($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage Post parameter friend_id is not set !
     */
    public function testInvalidParameterAllowFriend()
    {
        $mockRepo = $this->getMock('UserRepository');
        
        $request = new Request($query = array(), $request = array('false_param' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new AuthorizationController($mockRepo);
        
        $controller->allowFriend($request);
    }
    
    public function testValidDisallowFriend()
    {
        $mockRepo = $this->getMock('UserRepository', array('setFriendshipStatus'));
        $mockRepo->expects($this->once())
                 ->method('setFriendshipStatus')
                 ->with($this->equalTo(14), $this->equalTo(15), $this->equalTo('DISALLOWED'));
    
        $request = new Request($query = array(), $request = array('friend_id' => 15));
    
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($mockRepo);
    
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
        $mockRepo = $this->getMock('UserRepository');
    
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $request->setSession($session);
    
        $controller = new AuthorizationController($mockRepo);
    
        $controller->disallowFriend($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage Post parameter friend_id is not set !
     */
    public function testInvalidParameterDisallowFriend()
    {
        $mockRepo = $this->getMock('UserRepository');
    
        $request = new Request($query = array(), $request = array('false_param' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($mockRepo);
    
        $controller->disallowFriend($request);
    }
    
    public function testValidAllowFriendList()
    {
        $mockRepo = $this->getMock('UserRepository', array('setFriendshipsStatus'));
        $mockRepo->expects($this->once())
                 ->method('setFriendshipsStatus')
                 ->with($this->equalTo(14), $this->equalTo(array(1,2,3,4,5)), $this->equalTo('ALLOWED'));
        
        $request = new Request($query = array(), $request = array('friend_ids' => '1,2 ,3,4, 5'));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new AuthorizationController($mockRepo);
        
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
        $mockRepo = $this->getMock('UserRepository');
    
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $request->setSession($session);
    
        $controller = new AuthorizationController($mockRepo);
    
        $controller->allowFriendList($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage Post parameter friend_id is not set !
     */
    public function testInvalidParameterAllowFriendList()
    {
        $mockRepo = $this->getMock('UserRepository');
    
        $request = new Request($query = array(), $request = array('false_param' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($mockRepo);
    
        $controller->allowFriend($request);
    }
    
    public function testValidDisallowFriendList()
    {
        $mockRepo = $this->getMock('UserRepository', array('setFriendshipsStatus'));
        $mockRepo->expects($this->once())
        ->method('setFriendshipsStatus')
        ->with($this->equalTo(14), $this->equalTo(array(1,2,3,4,5)), $this->equalTo('DISALLOWED'));
    
        $request = new Request($query = array(), $request = array('friend_ids' => ' 1, 2 ,3,4, 5'));
    
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($mockRepo);
    
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
        $mockRepo = $this->getMock('UserRepository');
    
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $request->setSession($session);
    
        $controller = new AuthorizationController($mockRepo);
    
        $controller->disallowFriendList($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage Post parameter friend_ids is not set !
     */
    public function testInvalidParameterDisallowFriendList()
    {
        $mockRepo = $this->getMock('UserRepository');
    
        $request = new Request($query = array(), $request = array('false_param' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($mockRepo);
    
        $controller->disallowFriendList($request);
    }
    
    public function testValidFollowFriend()
    {
        $mockRepo = $this->getMock('UserRepository', array('setFriendshipFollow'));
        $mockRepo->expects($this->once())
        ->method('setFriendshipFollow')
        ->with($this->equalTo(14), $this->equalTo(15), $this->equalTo('FOLLOWED'));
        
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new AuthorizationController($mockRepo);
        
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
        $mockRepo = $this->getMock('UserRepository');
        
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $request->setSession($session);
        
        $controller = new AuthorizationController($mockRepo);
        
        $controller->followFriend($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage Post parameter friend_id is not set !
     */
    public function testInvalidParameterFollowFriend()
    {
        $mockRepo = $this->getMock('UserRepository');
    
        $request = new Request($query = array(), $request = array('false_param' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($mockRepo);
    
        $controller->followFriend($request);
    }
    
    public function testValidUnfollowFriend()
    {
        $mockRepo = $this->getMock('UserRepository', array('setFriendshipFollow'));
        $mockRepo->expects($this->once())
        ->method('setFriendshipFollow')
        ->with($this->equalTo(14), $this->equalTo(15), $this->equalTo('UNFOLLOWED'));
    
        $request = new Request($query = array(), $request = array('friend_id' => 15));
    
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($mockRepo);
    
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
        $mockRepo = $this->getMock('UserRepository');
    
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $request->setSession($session);
    
        $controller = new AuthorizationController($mockRepo);
    
        $controller->unfollowFriend($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage Post parameter friend_id is not set !
     */
    public function testInvalidParameterUnfollowFriend()
    {
        $mockRepo = $this->getMock('UserRepository');
    
        $request = new Request($query = array(), $request = array('false_param' => 15));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
    
        $controller = new AuthorizationController($mockRepo);
    
        $controller->unfollowFriend($request);
    }
}
