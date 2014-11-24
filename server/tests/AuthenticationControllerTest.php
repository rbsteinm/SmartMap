<?php

use Silex\Application;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Session\Storage\MockArraySessionStorage;
use Symfony\Component\HttpFoundation\Session\Storage\MockFileSessionStorage;
use Symfony\Component\HttpFoundation\Session\Session;

use SmartMap\DBInterface\User;
use SmartMap\DBInterface\UserRepository;
use SmartMap\Control\AuthenticationController;
use SmartMap\Control\ControlException;

use Facebook\FacebookSession;
use Facebook\FacebookRedirectLoginHelper;
use Facebook\FacebookRequest;
use Facebook\FacebookResponse;
use Facebook\FacebookSDKException;
use Facebook\FacebookRequestException;
use Facebook\FacebookAuthorizationException;
use Facebook\GraphObject;

use Doctrine\DBAL\DriverManager;
use Doctrine\DBAL\Configuration;


/**
 * Tests for the AuthenticationController class.
 * To run them, run
 * $> phpunit --bootstrap vendor/autoload.php tests/AuthenticationControllerTest.php
 * from the server directory.
 *
 * @author SpicyCH
 * 
 * @author Pamoi (code reviewed - 01.11.2014)
 * @author Pamoi (modified tests to use a mock repo so this
 * is really a unit test and we don't need a database to run it)
 *        
 */
class AuthenticationControllerTest extends PHPUnit_Framework_TestCase
{
    private static $APP_ID = '305881779616905';
    private static $APP_SECRET = 'b851a1eb3edcaf637f92fbb2af2b3b47';

    private $authController;
    private $validPostRequest;
    private $validFbToken;

    private $mockRepo;
    
    public function setUp()
    {
        $this->mockRepo = $this->getMockBuilder('SmartMap\DBInterface\UserRepository')
             ->disableOriginalConstructor()
             ->getMock();
        
        $this->authController = new AuthenticationController($this->mockRepo, self::$APP_ID, self::$APP_SECRET);
        
        $this->validFbToken = 'CAAEWMqbRPIkBAJjvxMI0zNXLgzxYJURV5frWkDu8T60EfWup92GNEE7xDIVohfpa43Qm7' .
                        'FNbZCvZB7bXVTd0ZC0qLHZCju2zZBR3mc8mQH0OskEe7X5mZAWOlLZCIzsAWnfEy1ZAzz2JgYPKjaIwhIpI9OvJkQ' .
                        'NWkJnX3rIwv4v9lL7hr9yx8LKuOegEHfZCcCNp491jewilZCz69ZA2ohryEYy';
        
        $this->validPostRequest = array(
                        'name' => 'SmartMap SwEng',
                        'facebookId' => '1482245642055847',
                        'facebookToken' => $this->validFbToken
        );
    }
    
    public function testCanLoginWithGoodRequestParams()
    {
        $this->mockRepo
             ->method('getUserIdFromFb')
             ->willReturn(1);

        $this->mockRepo->expects($this->once())
             ->method('getUserIdFromFb')
             ->with($this->equalTo(1482245642055847));

        $request = new Request($getRequest = array(), $this->validPostRequest);
        
        $session = new Session(new MockFileSessionStorage());
        $request->setSession($session);

        $response = $this->authController->authenticate($request);

        $validResponse = array('status' => 'Ok', 'message' => 'Successfully authenticated !', 'id' => 1);
        
        $this->assertEquals($response->getContent(), json_encode($validResponse));
    
    }
    
    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage Session is null. Did you send session cookie ?
     */
    public function testNoSessionYieldsException()
    {
        $request = new Request($getRequest = array(), $this->validPostRequest);
        $serverResponse = $this->authController->authenticate($request);
    }
    
    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage Mismatch or expired facebook data.
     */
    public function testCannotLoginWithBadSession()
    {
        $postReq = array (
                        'name' => 'SmartMap SwEng',
                        'facebookId' => '1482245642055847',
                        'facebookToken' => 'ehf h e ue'
        );
        $request = new Request($getRequest = array(), $postReq);
        
        $session = new Session(new MockFileSessionStorage());
        $request->setSession($session);
        
        $this->authController->authenticate($request);
    }
    
    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage Id and name do not match the fb access token.
     */
    public function testCannotLoginWithGoodSessionButBadName()
    {
        $postReq = array(
                        'name' => 'Robich',
                        'facebookId' => '1482245642055847',
                        'facebookToken' => $this->validFbToken 
        );
        $request = new Request($getRequest = array(), $postReq);
        
        $session = new Session(new MockFileSessionStorage());
        $request->setSession($session);
        
        $this->authController->authenticate($request);
    }
    
    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage Id and name do not match the fb access token.
     */
    public function testCannotLoginWithGoodSessionButBadId()
    {
        $postReq = array(
                        'name' => 'SmartMap SwEng',
                        'facebookId' => '1337',
                        'facebookToken' => $this->validFbToken 
        );
        $request = new Request($getRequest = array(), $postReq);
        
        $session = new Session(new MockFileSessionStorage());
        $request->setSession($session);
        
        $this->authController->authenticate($request);
    }
    
    /**
     * @expectedException SmartMap\DBInterface\DatabaseException
     * @expectedExceptionMessage toto
     */
    public function testDatabaseErrorYieldsDBException()
    {
        $this->mockRepo
             ->method('getUserIdFromFb')
             ->will($this->throwException(new \SmartMap\DBInterface\DatabaseException('toto')));

        
        $request = new Request($getRequest = array(), $this->validPostRequest);
        
        $session = new Session(new MockFileSessionStorage());
        $request->setSession($session);
        
        $this->authController->authenticate($request);
    }
    
    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage Missing POST parameter.
     */
    public function testNoPostParamsYieldsException()
    {
        $request = new Request($getRequest = array (), $postParams = array());
        
        $session = new Session(new MockFileSessionStorage());
        $request->setSession($session);
        
        $serverResponse = $this->authController->authenticate($request);
    }
    
    public function testSessionUserIdIsSetAfterLogin()
    {
        $this->mockRepo
             ->method('getUserIdFromFb')
             ->willReturn(1);

        $this->mockRepo->expects($this->once())
             ->method('getUserIdFromFb')
             ->with($this->equalTo(1482245642055847));

        $request = new Request($getRequest = array(), $this->validPostRequest);
        
        $session = new Session(new MockFileSessionStorage());
        $request->setSession($session);
        
        $this->assertTrue($session->get('userId') == null);
        
        $serverResponse = $this->authController->authenticate($request);
        
        $this->assertTrue($session->get('userId') == 1);
    }
}
