<?php

use SmartMap\DBInterface\User;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Session\Session;
use Symfony\Component\HttpFoundation\Session\Storage\MockArraySessionStorage;

class UserTest extends PHPUnit_Framework_TestCase
{
    public function testGetters()
    {
        $user = new User(23, 34, 'Toto', 'VISIBLE', 75.43, 22.88);
        
        $this->assertEquals(23, $user->getId());
        $this->assertEquals(34, $user->getFbId());
        $this->assertEquals('Toto', $user->getName());
        $this->assertEquals('VISIBLE', $user->getVisibility());
        $this->assertEquals(75.43, $user->getLongitude());
        $this->assertEquals(22.88, $user->getLatitude());
    }
    
    public function testSetters()
    {
    	$user = new User(23, 34, 'Toto', 'VISIBLE', 75.43, 22.88);
    	
    	$user->setId(33);
    	$user->setFbId(35);
    	$user->setName('Tata');
    	$user->setVisibility('INVISIBLE');
    	$user->setLongitude(1.2);
    	$user->setLatitude(2.1);
    	
    	$this->assertEquals(33, $user->getId());
    	$this->assertEquals(35, $user->getFbId());
    	$this->assertEquals('Tata', $user->getName());
    	$this->assertEquals('INVISIBLE', $user->getVisibility());
    	$this->assertEquals(1.2, $user->getLongitude());
    	$this->assertEquals(2.1, $user->getLatitude());
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Id must be greater than 0.
     */
    public function testInvalidId()
    {
    	$user = new User(0, 34, 'Toto', 'VISIBLE', 75.43, 22.88);
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Id must be greater than 0.
     */
    public function testInvalidFbId()
    {
        $user = new User(1, -1, 'Toto', 'VISIBLE', 75.43, 22.88);
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Name must be no longer than 60 characters.
     */
    public function testInvalidName()
    {
        $user = new User(1, 34, 'Toto Mirabeau de Kartein von Siebenlangental nach schmilblick über nordsee am stand', 
        		          'VISIBLE', 75.43, 22.88);
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Visibility must be VISIBLE or INVISIBLE.
     */
    public function testInvalidVisibility()
    {
        $user = new User(1, 1, 'Toto', 'titi', 75.43, 22.88);
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Longitude must be between -180 and 180.
     */
    public function testInvalidPositiveLongitude()
    {
        $user = new User(1, 1, 'Toto', 'VISIBLE', 190.2, 22.88);
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Longitude must be between -180 and 180.
     */
    public function testInvalidNegativeLongitude()
    {
        $user = new User(1, 1, 'Toto', 'VISIBLE', -180.1, 22.88);
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Latitude must be between -90 and 90.
     */
    public function testInvalidPositiveLatitude()
    {
        $user = new User(1, 1, 'Toto', 'VISIBLE', 75.43, 92.88);
    }
    
    /**
     * @expectedException \InvalidArgumentException
     * @expectedExceptionMessage Latitude must be between -90 and 90.
     */
    public function testInvalidNegativeLatitude()
    {
        $user = new User(1, 1, 'Toto', 'VISIBLE', 75.43, -90.88);
    }
    
    public function testGetIdFromRequest()
    {
    	$request = new Request();
    	$session =  new Session(new MockArraySessionStorage());
    	$session->set('userId', 14);
    	$request->setSession($session);
    	
    	$id = User::getIdFromRequest($request);
    	
    	$this->assertEquals(14, $id);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage Trying to access session but the session is not started.
     */
    public function testSessionNotStartedException()
    {
    	$request = new Request();
    	
    	User::getIdFromRequest($request);
    }
    
    /**
     * @expectedException SmartMap\Control\ControlException
     * @expectedExceptionMessage The user is not authenticated.
     */
    public function testUserNotAuthenticatedException()
    {
    	$request = new Request();
    	$session = new Session(new MockArraySessionStorage());
    	$request->setSession($session);
    	
    	User::getIdFromRequest($request);
    }
}
