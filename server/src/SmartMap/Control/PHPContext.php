<?php

namespace SmartMap\Control;

/*
 * @author Pamoi
 * 
 * This class implements the Context interface using PHP global variables.
 */
class PHPContext implemets Context
{
    private $mPost;
    private $mSession;
    
    function __construct($post, $session)
    {
        $this->mPost = $post;
        $this->mSession = $session;
    }
    
    /*
     * @see isAuthenticated()
     */
    public function isAuthenticated()
    {
        return isset($this->mSession['authenticated']) AND $this->mSession['authenticated'] == true)
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
     * @see getUserId()
     */
    public function getUserId()
    {
        // TODO
        return 14;
    }
}
