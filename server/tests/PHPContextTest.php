<?php

use SmartMap\Control\PHPContext;
use SmartMap\Control\ContextException;

class PHPContextTest extends PHPUnit_Framework_TestCase
{
    /**
     * @expectedException        SmartMap\Control\ContextException
     * @expectedExceptionMessage POST parameter field2 is not set.
     */
    public function testGetPostException()
    {
        $ctx = new PHPContext(array());
        
        $ctx->getPost('field2');
    }
    
    public function testGetPost()
    {
        // Setting global variable POST for test
        $_POST['field1'] = 'Toto';
        
        $ctx = new PHPContext(array());
        
        $this->assertEquals('Toto', $ctx->getPost('field1'));        
    }
    
    public function testIsAuthenticated()
    {
        $ctx = new PHPContext(array());
        
        $this->assertEquals(false, $ctx->isAuthenticated());
        
        $_SESSION['authenticated'] = true;
        
        $this->assertEquals(true, $ctx->isAuthenticated());
    }
    
    /**
     * @expectedException        SmartMap\Control\ContextException
     * @expectedExceptionMessage Session variable sess1 is not set.
     */
    public function testGetSessionException()
    {
        $ctx = new PHPContext(array());
        
        $ctx->getSession('sess1');
    }
    
    public function testGetSession()
    {
        $_SESSION['Sess2'] = 'Titi';
        
        $ctx = new PHPContext(array());
        
        $this->assertEquals('Titi', $ctx->getSession('Sess2'));   
    }
    
    /**
     * @expectedException        SmartMap\Control\ContextException
     * @expectedExceptionMessage Option opt1 is not set.
     */
    public function testGetOptionException()
    {
        $ctx = new PHPContext(array());
        
        $ctx->getOption('opt1');
    }
    
    public function testGetOption()
    {
        $ctx = new PHPContext(array('opt2' => 'Tata'));
        
        $this->assertEquals('Tata', $ctx->getOption('opt2')); 
    }
}
