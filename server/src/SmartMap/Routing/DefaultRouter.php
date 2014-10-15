<?php

namespace SmartMap\Routing;

use SmartMap\Control\Context;
use SmartMap\Control\PHPContext;
use SmartMap\Control\Controller;

/*
 * @author Pamoi
 * 
 * This class tries to match the request URI to a route and then calls
 * the corresponding controller method.
 */
class DefaultRouter implements Router
{
    private $mContext;
    private $mController;
    
    /* Constructs the Router with needed global variables
     * 
     * @param $context 
     */
    public function __construct($context, $controller)
    {
        $this->mContext = $context;
        $this->mController = $controller;
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
                return $this->mController->authenticate();
                break;
            
            // Friend management routes
            case "/listFriendsPos":
                return $this->mController->listFriendsPos();
                break;
            
            case "/followFriend":
                return $this->mController->followFriend();
                break;
            
            case "/unfollowFriend":
                return $this->mController->unfollowFriend();
                break;
            
            case "/allowFriend":
                return $this->mController->allowFriend();
                break;
            
            case "/disallowFriend":
                return $this->mController->disallowFriend();
                break;
            
            case "/allowFriendList":
                return $this->mController->allowFriendList();
                break;
            
            case "/disallowFriendList":
                return $this->mController->disallowFriendList();
                break;
            
            // Friend invitations routes
            case "/inviteFriend":
                return $this->mController->inviteFriend();
                break;
            
            case "/getInvitations":
                return $this->mController->getInvitations();
                break;
            
            case "/acceptInvitation":
                return $this->mController->acceptInvitation();
                break;
            
            case "/getUserInfo":
                return $this->mController->getUserInfo();
                break;
            
            default:
                return 'ERROR: no route found !';
        }
    }
}
