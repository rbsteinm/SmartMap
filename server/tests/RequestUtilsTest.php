<?php

use SmartMap\Control\RequestUtils;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Session\Session;
use Symfony\Component\HttpFoundation\Session\Storage\MockArraySessionStorage;

/** Tests for the RequestUtils class.
 * To run them, run
 * $> phpunit --bootstrap vendor/autoload.php tests/RequestUtilsTest.php
 * from the server directory.
 *
 * @author Pamoi
 *
 */
class RequestUtilsTest extends PHPUnit_Framework_TestCase
{
    public function testGetRequestParam()
    {
        $request = new Request($query = array(), $request = array('param1' => '100', 'param2' => 'toto'));

        $this->assertEquals('100', RequestUtils::getPostParam($request, 'param1'));
        $this->assertEquals('toto', RequestUtils::getPostParam($request, 'param2'));
    }

    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage Post parameter toto is not set !
     */
    public function testGetNonExistingRequestParam()
    {
        $request = new Request();

        RequestUtils::getPostParam($request, 'toto');
    }

    public function testNonThrowingGetRequestParam()
    {
        $request = new Request();

        RequestUtils::getPostParam($request, 'toto', false);

        $this->assertTrue(true);
    }

    public function testGetIdFromRequest()
    {
        $request = new Request();
        $session =  new Session(new MockArraySessionStorage());
        $session->set('userId', 14);
        $request->setSession($session);

        $id = RequestUtils::getIdFromRequest($request);

        $this->assertEquals(14, $id);
    }

    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage Trying to access session but the session is not started.
     */
    public function testSessionNotStartedException()
    {
        $request = new Request();

        RequestUtils::getIdFromRequest($request);
    }

    /**
     * @expectedException SmartMap\Control\InvalidRequestException
     * @expectedExceptionMessage The user is not authenticated.
     */
    public function testUserNotAuthenticatedException()
    {
        $request = new Request();
        $session = new Session(new MockArraySessionStorage());
        $request->setSession($session);

        RequestUtils::getIdFromRequest($request);
    }

    public function testGetIntArrayFromString1()
    {
        $string = '1';

        $this->assertEquals(array(1), RequestUtils::getIntArrayFromString($string));
    }

    public function testGetIntArrayFromString2()
    {
        $string = '1,2,3';

        $this->assertEquals(array(1, 2, 3), RequestUtils::getIntArrayFromString($string));
    }

    public function testGetIntArrayFromString3()
    {
        $string = '1, 2,3,  5';

        $this->assertEquals(array(1, 2, 3, 5), RequestUtils::getIntArrayFromString($string));
    }

    public function testGetIntArrayFromString4()
    {
        $string = '1, 2, 3,';

        $this->assertEquals(array(1, 2, 3), RequestUtils::getIntArrayFromString($string));
    }

    public function testGetIntArrayFromString5()
    {
        $string = '  1, 2,   , 3,  ';

        $this->assertEquals(array(1, 2, 3), RequestUtils::getIntArrayFromString($string));
    }
}