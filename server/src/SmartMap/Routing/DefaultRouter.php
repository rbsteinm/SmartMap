<?php

namespace SmartMap\Routing;

use SmartMap\Control\Context;
use SmartMap\Control\PHPContext;
use SmartMap\Control\Handler;


/*
 * @author Pamoi
 * 
 * This class tries to match the request URI to a route and then calls
 * the corresponding controller method.
 */
class DefaultRouter implements Router
{
    private $mHandler;
    
    /* Constructs the Router with needed global variables
     * 
     * @param $context 
     */
    public function __construct($handler)
    {
        $this->mHandler = $handler;
    }
    
    /* @see getResponse($uri)
     */
    public function getResponse($uri)
    {
        // We look for a matching route
        switch ($uri) 
        {
            // Authentication route
            case "/auth":
                return $this->mHandler->authenticate();
                break;
            
            case "/registerUser":
                return $this->mHandler->registerUser();
                break;
                
            // Position updating
            case "/updatePos":
                return $this->mHandler->updatePos();
                break;
            
            // Friend management routes
            case "/listFriendsPos":
                return $this->mHandler->listFriendsPos();
                break;
            
            case "/followFriend":
                return $this->mHandler->followFriend();
                break;
            
            case "/unfollowFriend":
                return $this->mHandler->unfollowFriend();
                break;
            
            case "/allowFriend":
                return $this->mHandler->allowFriend();
                break;
            
            case "/disallowFriend":
                return $this->mHandler->disallowFriend();
                break;
            
            case "/allowFriendList":
                return $this->mHandler->allowFriendList();
                break;
            
            case "/disallowFriendList":
                return $this->mHandler->disallowFriendList();
                break;
            
            // Friend invitations routes
            case "/inviteFriend":
                return $this->mHandler->inviteFriend();
                break;
            
            case "/getInvitations":
                return $this->mHandler->getInvitations();
                break;
            
            case "/acceptInvitation":
                return $this->mHandler->acceptInvitation();
                break;
            
            case "/getUserInfo":
                return $this->mHandler->getUserInfo();
                break;
            
            default:
                return '{"status": "error", "message": "No route found for URI ' . $uri . '."}';
        }
    }
}
