<?php

namespace SmartMap\Control;

/*
 * @author Pamoi
 * 
 * This class implements the Controller interface and uses the database
 * to process the requests.
 */
class DatabaseHandler implements Handler
{
    private $mContext;
    
    function __construct($context)
    {
        $this->mContext = $context;
    }
    
    public function authenticate()
    {
        if ($this->mContext->isAuthenticated())
        {
            return 'Already authenticated';
        }
        else
        {
            // TODO
            $this->mContext->setSession('authenticated', true);
            
            return 'Authenticated succesfully !';
        }
    }
    
    public function listFriendsPos()
    {
        // TODO
        
        return 'OK !';
    }
    
    public function followFriend()
    {
        // TODO
        
        return 'OK !';
    }
    
    public function unfollowFriend()
    {
        // TODO
        
        return 'OK !';
    }
    
    public function allowFriend()
    {
        // TODO
        
        return 'OK !';
    }
    
    public function disallowFriend()
    {
        // TODO
        
        return 'OK !';
    }
    
    public function allowFriendList()
    {
        // TODO
        
        return 'OK !';
    }
    
    public function disallowFriendList()
    {
        // TODO
        
        return 'OK !';
    }
    
    public function inviteFriend()
    {
        // TODO
        
        return 'OK !';
    }
    
    public function getInvitations()
    {
        // TODO
        
        return 'OK !';
    }
    
    public function acceptInvitation()
    {
        // TODO
        
        return 'OK !';
    }
    
    public function getUserInfo()
    {
        // TODO
        
        return 'OK !';
    }
}
