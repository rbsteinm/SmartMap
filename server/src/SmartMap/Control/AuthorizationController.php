<?php

namespace SmartMap\Control;

use Silex\Application;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;

class AuthorizationController
{
    private $mRepo;
    
    function __construct(UserRepository $repo)
    {
        $this->mRepo = $repo;
    }
    
    public function allowFriend(Request $request, Application $app)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $request->request->get('friend_id');
        if ($friendId === null)
        {
            throw new \InvalidArgumentException('Post parameter friend_id is not set !');
        }
        
        $this->mRepo->setFriendshipStatus($userId, $friendId, 'ALLOW');
        
        $response = array('status' => 'Ok', 'message' => 'Allowed friend !');
        
        return new JsonResponse($response);
    }
    
    public function disallowFriend(Request $request, Application $app)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $request->request->get('friend_id');
        if ($friendId === null)
        {
            throw new \InvalidArgumentException('Post parameter friend_id is not set !');
        }
        
        $this->mRepo->setFriendshipStatus($userId, $friendId, 'DISALLOW');
        
        $response = array('status' => 'Ok', 'message' => 'Disallowed friend !');
        
        return new JsonResponse($response);
    }
    
    public function allowFriendList(Request $request, Application $app)
    {
        // TODO
    }
    
    public function disallowFriendList(Request $request, Application $app)
    {
        // TODO
    }
    
    public function followFriend(Request $request, Application $app)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $request->request->get('friend_id');
        if ($friendId === null)
        {
            throw new \InvalidArgumentException('Post parameter friend_id is not set !');
        }
        
        $this->mRepo->setFriendshipFollow($userId, $friendId, 'FOLLOW');
        
        $response = array('status' => 'Ok', 'message' => 'Followed friend !');
        
        return new JsonResponse($response);
    }
    
    public function unfollowFriend(Request $request, Application $app)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $request->request->get('friend_id');
        if ($friendId === null)
        {
            throw new \InvalidArgumentException('Post parameter friend_id is not set !');
        }
        
        $this->mRepo->setFriendshipFollow($userId, $friendId, 'UNFOLLOW');
        
        $response = array('status' => 'Ok', 'message' => 'Unfollowed');
        
        return new JsonResponse($response);
    }
}
