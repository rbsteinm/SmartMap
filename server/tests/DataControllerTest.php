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
}