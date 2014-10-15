<?php

namespace SmartMap\Control;

/*
 * @author Pamoi
 * 
 * This interface contains the methods needed to process server requests.
 * 
 */
interface Controller
{
    public function authenticate();
    
    public function listFriendsPos();
    
    public function followFriend();
    
    public function unfollowFriend();
    
    public function allowFriend();
    
    public function disallowFriend();
    
    public function allowFriendList();
    
    public function disallowFriendList();
    
    public function inviteFriend();
    
    public function getInvitations();
    
    public function acceptInvitation();
    
    public function getUserInfo();
}
