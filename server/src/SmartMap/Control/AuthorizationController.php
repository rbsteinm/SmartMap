<?php

namespace SmartMap\Control;

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
    
    function __construct($repo)
    {
        $this->mRepo = $repo;
    }
    
    /** Allow a friend to see the user's location.
     * 
     * @param Request $request
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function allowFriend(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $this->getPostParam($request, 'friend_id');
        
        $this->mRepo->setFriendshipStatus($userId, $friendId, 'ALLOWED');
        
        $response = array('status' => 'Ok', 'message' => 'Allowed friend !');
        
        return new JsonResponse($response);
    }
    
    /** Disallow a friend to see the user's location.
     * 
     * @param Request $request
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function disallowFriend(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $this->getPostParam($request, 'friend_id');
        
        $this->mRepo->setFriendshipStatus($userId, $friendId, 'DISALLOWED');
        
        $response = array('status' => 'Ok', 'message' => 'Disallowed friend !');
        
        return new JsonResponse($response);
    }
    
    /** Allow friends in a list of ids separated by commas to see
     * the user's location.
     * 
     * @param Request $request
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function allowFriendList(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendsIds = $this->getPostParam($request, 'friend_ids');
        
        $friendsIds = $this->getIntArrayFromString($friendsIds);
        
        $this->mRepo->setFriendshipsStatus($userId, $friendsIds, 'ALLOWED');
        
        $response = array('status' => 'Ok', 'message' => 'Allowed friend list !');
        
        return new JsonResponse($response);
    }
    
    /** Disallow friends in a list of ids separaed by commas to see
     * the user's location.
     * 
     * @param Request $request
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function disallowFriendList(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendsIds = $this->getPostParam($request, 'friend_ids');
        
        $friendsIds = $this->getIntArrayFromString($friendsIds);
        
        $this->mRepo->setFriendshipsStatus($userId, $friendsIds, 'DISALLOWED');
        
        $response = array('status' => 'Ok', 'message' => 'Disallowed friend list !');
        
        return new JsonResponse($response);
    }
    
    /** Follow a friend to be notified of his position.
     * 
     * @param Request $request
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function followFriend(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $this->getPostParam($request, 'friend_id');
        
        $this->mRepo->setFriendshipFollow($userId, $friendId, 'FOLLOWED');
        
        $response = array('status' => 'Ok', 'message' => 'Followed friend !');
        
        return new JsonResponse($response);
    }
    
    /** Unfollow a friend to be no longer notified of his position.
     * 
     * @param Request $request
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function unfollowFriend(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $this->getPostParam($request, 'friend_id');
        
        $this->mRepo->setFriendshipFollow($userId, $friendId, 'UNFOLLOWED');
        
        $response = array('status' => 'Ok', 'message' => 'Unfollowed friend !');
        
        return new JsonResponse($response);
    }
    
    /** Utility function transforming a list of numbers separated by commas
     * in an array of integers.
     * 
     * @param string $string
     * @return array
     */
    private function getIntArrayFromString($string)
    {
        $array = explode(',', $string);
        
        for ($i = 0; $i < count($array); $i++)
        {
            $array[$i] = (int) $array[$i];
        }
        
        return $array;
    }
    
    /** Utility function getting a post parameter and throwing a ControlException
     * if the parameter is not set in the request.
     * 
     * @param Request $request
     * @param string $param
     * @throws ControlException
     * @return string
     */
    private function getPostParam(Request $request, $param)
    {
        $value = $request->request->get($param);
        
        if ($value === null)
        {
            throw new ControlException('Post parameter ' . $param . ' is not set !');
        }
        
        return $value;
    }
}
