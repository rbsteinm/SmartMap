<?php

namespace SmartMap\Control;

/*
 * @author Pamoi
 * 
 * This interface contains the methods needed to process client requests.
 * 
 */
interface Handler
{
    public function authenticate();
    
    public function registerUser();
    
    public function verifySMS();
    
    public function updatePos();
    
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
