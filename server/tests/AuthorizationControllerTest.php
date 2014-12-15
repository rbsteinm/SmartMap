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
        $this->mockRepo = $this->getMock('SmartMap\DBInterface\UserRepositoryInterface');
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
     * @expectedException SmartMap\Control\InvalidRequestException
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
     * @expectedException SmartMap\Control\InvalidRequestException
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
    
    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in allowFriend method.
     */
    public function testDatabaseException()
    {
        $this->mockRepo
        ->method('setFriendshipStatus')
        ->will($this->throwException(new SmartMap\DBInterface\DatabaseException('Argh !')));
        
        $request = new Request($query = array(), $request = array('friend_id' => 15));
        
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);
        
        $controller = new AuthorizationController($this->mockRepo);
        
        $response = $controller->allowFriend($request);
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
     * @expectedException SmartMap\Control\InvalidRequestException
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
     * @expectedException SmartMap\Control\InvalidRequestException
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
     * @expectedException SmartMap\Control\InvalidRequestException
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
     * @expectedException SmartMap\Control\InvalidRequestException
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
     * @expectedException SmartMap\Control\InvalidRequestException
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
     * @expectedException SmartMap\Control\InvalidRequestException
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
     * @expectedException SmartMap\Control\InvalidRequestException
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
     * @expectedException SmartMap\Control\InvalidRequestException
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
     * @expectedException SmartMap\Control\InvalidRequestException
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
     * @expectedException SmartMap\Control\InvalidRequestException
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

    public function testValidSetVisibility()
    {
        $user = new User(14, 12345, 'Toto', 'VISIBLE', 1.0, 2.0, '2014-09-03 22:34:59');

        $modifiedUser = new User(14, 12345, 'Toto', 'INVISIBLE', 1.0, 2.0, '2014-09-03 22:34:59');

        $this->mockRepo
             ->method('getUser')
             ->willReturn($user);

        $this->mockRepo->expects($this->once())
             ->method('getUser')
             ->with($this->equalTo(14));

        $this->mockRepo->expects($this->once())
             ->method('updateUser')
             ->with($modifiedUser);

        $request = new Request($query = array(), $request = array('visibility' => 'INVISIBLE'));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new AuthorizationController($this->mockRepo);

        $response = $controller->setVisibility($request);

        $validResponse = array('status' => 'Ok', 'message' => 'Visibility changed.');

        $this->assertEquals(json_encode($validResponse), $response->getContent());
    }

    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage Visibility must be VISIBLE or INVISIBLE.
     */
    public function testInvalidParamSetVisibility()
    {
        $user = new User(14, 12345, 'Toto', 'VISIBLE', 1.0, 2.0, '2014-09-03 22:34:59');

        $this->mockRepo
            ->method('getUser')
            ->willReturn($user);

        $request = new Request($query = array(), $request = array('visibility' => 'Toto'));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new AuthorizationController($this->mockRepo);

        $controller->setVisibility($request);
    }

    // Setting the same visibility should not result in an error.
    public function testSetSameVisibility()
    {
        $user = new User(14, 12345, 'Toto', 'INVISIBLE', 1.0, 2.0, '2014-09-03 22:34:59');

        $modifiedUser = new User(14, 12345, 'Toto', 'INVISIBLE', 1.0, 2.0, '2014-09-03 22:34:59');

        $this->mockRepo
            ->method('getUser')
            ->willReturn($user);

        $this->mockRepo->expects($this->once())
            ->method('getUser')
            ->with($this->equalTo(14));

        $this->mockRepo->expects($this->once())
            ->method('updateUser')
            ->with($modifiedUser);

        $request = new Request($query = array(), $request = array('visibility' => 'INVISIBLE'));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new AuthorizationController($this->mockRepo);

        $response = $controller->setVisibility($request);

        $validResponse = array('status' => 'Ok', 'message' => 'Visibility changed.');

        $this->assertEquals(json_encode($validResponse), $response->getContent());
    }

    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in setVisibility method.
     */
    public function testSetVisibilityDBException()
    {
        $this->mockRepo
            ->method('getUser')
            ->will($this->throwException(new \SmartMap\DBInterface\DatabaseException()));

        $request = new Request($query = array(), $request = array('visibility' => 'INVISIBLE'));
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new AuthorizationController($this->mockRepo);

        $controller->setVisibility($request);
    }

    public function testBlockFriend()
    {
        $this->mockRepo->expects($this->once())
             ->method('setFriendshipStatus')
             ->with($this->equalTo(14), $this->equalTo(15), $this->equalTo('DISALLOWED'));

        $this->mockRepo->expects($this->once())
             ->method('setFriendshipFollow')
             ->with($this->equalTo(14), $this->equalTo(15), $this->equalTo('UNFOLLOWED'));

        $request = new Request($query = array(), $request = array('friend_id' => 15));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new AuthorizationController($this->mockRepo);

        $response = $controller->blockFriend($request);

        $validResponse = array('status' => 'Ok', 'message' => 'Blocked friend.');

        $this->assertEquals(json_encode($validResponse), $response->getContent());
    }

    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in blockFriend.
     */
    public function testBlockFriendDBException()
    {
        $this->mockRepo
             ->method('setFriendshipStatus')
             ->will($this->throwException(new \SmartMap\DBInterface\DatabaseException()));

        $request = new Request($query = array(), $request = array('friend_id' => 15));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new AuthorizationController($this->mockRepo);

        $controller->blockFriend($request);
    }

    public function testUnblockFriend()
    {
        $this->mockRepo->expects($this->once())
            ->method('setFriendshipStatus')
            ->with($this->equalTo(14), $this->equalTo(15), $this->equalTo('ALLOWED'));

        $this->mockRepo->expects($this->once())
            ->method('setFriendshipFollow')
            ->with($this->equalTo(14), $this->equalTo(15), $this->equalTo('FOLLOWED'));

        $request = new Request($query = array(), $request = array('friend_id' => 15));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new AuthorizationController($this->mockRepo);

        $response = $controller->unblockFriend($request);

        $validResponse = array('status' => 'Ok', 'message' => 'Unblocked friend.');

        $this->assertEquals(json_encode($validResponse), $response->getContent());
    }

    /**
     * @expectedException SmartMap\Control\ControlLogicException
     * @expectedExceptionMessage Error in unblockFriend.
     */
    public function testUnblockFriendDBException()
    {
        $this->mockRepo
            ->method('setFriendshipStatus')
            ->will($this->throwException(new \SmartMap\DBInterface\DatabaseException()));

        $request = new Request($query = array(), $request = array('friend_id' => 15));

        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $controller = new AuthorizationController($this->mockRepo);

        $controller->unblockFriend($request);
    }
}
