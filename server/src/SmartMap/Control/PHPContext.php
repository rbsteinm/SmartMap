<?php

namespace SmartMap\Control;

/*
 * @author Pamoi
 * 
 * This class implements the Context interface using PHP global variables.
 */
class PHPContext implements Context
{
    private $mOptions;
    
    function __construct($options)
    {
        $this->mOptions = $options;
    }
    
    /*
     * @see isAuthenticated()
     */
    public function isAuthenticated()
    {
        return (isset($_SESSION['authenticated']) AND $_SESSION['authenticated'] == true);
    }
    
    /*
     * @see getPost($fieldname)
     */
    public function getPost($fieldname)
    {
        if (!isset($_POST[$fieldname]))
        {
            throw new ContextException("POST parameter " . $fieldname . " is not set.");
        }
        else
        {
            return $_POST[$fieldname];
        }
    }
    
    /*
     * @see getSession($fieldname)
     */
    public function getSession($fieldname)
    {
        if (!isset($_SESSION[$fieldname]))
        {
            throw new ContextException("Session variable " . $fieldname . " is not set.");
        }
        else
        {
            return $_SESSION[$fieldname];
        }
    }
    
    /*
     * @see setSession($fieldname, $value)
     */
    public function setSession($fieldname, $value)
    {
        $_SESSION[$fieldname] = $value;
    }
    
    /*
     * @see getOption($optionName)
     */
     public function getOption($optionName)
     {
         if (!isset($this->mOptions[$optionName]))
         {
             throw new ContextException("Option " . $optionName . " is not set.");
         }
         else
         {
             return $this->mOptions[$optionName];
         }
     }
}
