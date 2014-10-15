<?php

namespace SmartMap\Routing;

use SmartMap\Control\Context;
use SmartMap\Control\PHPContext;

/*
 * @author Pamoi
 * 
 * This class tries to match the request URI to a route and then calls
 * the corresponding controller method.
 */
class DefaultRouter implements Router
{
    private $mContext;
    
    /* Constructs the Router with needed global variables
     * 
     * @param $context 
     */
    public function __construct($context)
    {
        $this->mContext = $context;
    }
    
    /* @see getResponse($uri)
     */
    public function getResponse($uri)
    {
        $controller = new DefaultController();
        
        // We look for a matching route
        switch ($uri) 
        {
            // Authentication route
            case "/auth":
                return $controller->authenticate();
                break;
            
            // Friend management routes
            case "/listFriendPos"
                return $controller->listFriendsPos();
                break;
            
            case "/followFriend":
                return $controller->followFriend();
                break;
            
            case "/unfollowFriend":
                return $controller->unfollowFriend();
                break;
            
            case "/allowFriend":
                return $controller->allowFriend();
                break;
            
            case "/disallowFriend":
                return $controller->disallowFriend();
                break;
            
            case "/allowFriendList":
                return $controller->allowFriendList();
                break;
            
            case "/disallowFriendList"
                return $controller->disallowFriendList();
                break;
            
            // Friend invitations routes
            case "/inviteFriend"
                return $controller->inviteFriend();
                break;
            
            case "/getInvitations"
                return $controller->getInvitations();
                break;
            
            case "/acceptInvitation"
                return $controller->acceptInvitation();
                break;
            
            case "/getUserInfo"
                return $controller->getUserInfo();
                break;
            
            default:
                echo 'ERROR: no route found !';
        }
    }
}
