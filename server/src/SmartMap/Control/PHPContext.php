<?php

namespace SmartMap\Control;

/*
 * @author Pamoi
 * 
 * This class implements the Context interface using PHP global variables.
 */
class PHPContext implements Context
{
    private $mPost;
    private $mSession;
    
    function __construct($post, $session, $options)
    {
        $this->mPost = $post;
        $this->mSession = $session;
        $this->mOptions = $options;
    }
    
    /*
     * @see isAuthenticated()
     */
    public function isAuthenticated()
    {
        return isset($this->mSession['authenticated']) AND $this->mSession['authenticated'] == true;
    }
    
    /*
     * @see getPost($fieldname)
     */
    public function getPost($fieldname)
    {
        if (!isset($this->mPost[$fieldname]))
        {
            throw new ContextExeption("POST parameter " . $fieldname . "is not set.");
        }
        else
        {
            return $this->mPost[$fieldname];
        }
    }
    
    /*
     * @see getSession($fieldname)
     */
    public function getSession($fieldname)
    {
        if (!$this->isAuthenticated OR !isset($this->mSession[$fieldname]))
        {
            throw new ContextExeption("Session variable " . $fieldname . "is not available.");
        }
        else
        {
            return $this->mPost[$fieldname];
        }
    }
    
    /*
     * @see getOption($optionName)
     */
     public function getOption($optionName)
     {
         if (!isset($this->mOptions[$option]))
         {
             throw new ContextException("Option " . $optionName . " is not set.");
         }
         else
         {
             return $this->mOption[$optionName];
         }
     }
}
