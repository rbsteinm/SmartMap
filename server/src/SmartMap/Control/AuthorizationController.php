<?php

namespace SmartMap\Control;

use Silex\Application;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;

use SmartMap\DBInterface\UserRepository;
use SmartMap\DBInterface\User;

/**
 * This class handles all the authorizations (allow, disallow friends, ...) between the users. 
 *
 * @author Pamoi
 * 
 * @author SpicyCH (code reviewed - 02.11.2014) : code looks fine, just added javadoc
 */
class AuthorizationController
{
    private $mRepo;
    
    function __construct(UserRepository $repo)
    {
        $this->mRepo = $repo;
    }
    
    public function allowFriend(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $request->request->get('friend_id');
        if ($friendId === null)
        {
            throw new ControlException('Post parameter friend_id is not set !');
        }
        
        $this->mRepo->setFriendshipStatus($userId, $friendId, 'ALLOWED');
        
        $response = array('status' => 'Ok', 'message' => 'Allowed friend !');
        
        return new JsonResponse($response);
    }
    
    public function disallowFriend(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $request->request->get('friend_id');
        if ($friendId === null)
        {
            throw new ControlException('Post parameter friend_id is not set !');
        }
        
        $this->mRepo->setFriendshipStatus($userId, $friendId, 'DISALLOWED');
        
        $response = array('status' => 'Ok', 'message' => 'Disallowed friend !');
        
        return new JsonResponse($response);
    }
    
    public function allowFriendList(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendsIds = $request->request->get('friend_ids');
        if ($friendsIds === null)
        {
            throw new ControlException('Post parameter friend_id is not set !');
        }
        
        $friendsIds = explode(',', $friendsIds);
        
        $this->mRepo->setFriendshipsStatus($userId, $friendsIds, 'ALLOWED');
        
        $response = array('status' => 'Ok', 'message' => 'Allowed friend list !');
        
        return new JsonResponse($response);
    }
    
    public function disallowFriendList(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendsIds = $request->request->get('friend_ids');
        if ($friendsIds === null)
        {
            throw new ControlException('Post parameter friend_id is not set !');
        }
        
        $friendsIds = explode(',', $friendsIds);
        
        $this->mRepo->setFriendshipsStatus($userId, $friendsIds, 'DISALLOWED');
        
        $response = array('status' => 'Ok', 'message' => 'Disallowed friend list !');
        
        return new JsonResponse($response);
    }
    
    public function followFriend(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $request->request->get('friend_id');
        if ($friendId === null)
        {
            throw new ControlException('Post parameter friend_id is not set !');
        }
        
        $this->mRepo->setFriendshipFollow($userId, $friendId, 'FOLLOWED');
        
        $response = array('status' => 'Ok', 'message' => 'Followed friend !');
        
        return new JsonResponse($response);
    }
    
    public function unfollowFriend(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $request->request->get('friend_id');
        if ($friendId === null)
        {
            throw new ControlException('Post parameter friend_id is not set !');
        }
        
        $this->mRepo->setFriendshipFollow($userId, $friendId, 'UNFOLLOWED');
        
        $response = array('status' => 'Ok', 'message' => 'Unfollowed friend !');
        
        return new JsonResponse($response);
    }
}
