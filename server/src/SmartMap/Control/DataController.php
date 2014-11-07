<?php

namespace SmartMap\Control;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;

use SmartMap\DBInterface\User;
use SmartMap\DBInterface\UserRepository;

/**
 * 
 *
 * @author Pamoi
 *
 * @author SpicyCH (code reviewed - 03.11.2014) : good logic, but need unit testing!
 */
class DataController
{
    private $mRepo;
    
    function __construct(UserRepository $repo)
    {
        $this->mRepo = $repo;
    }
    
    /** Updates the user's position.
     */
    public function updatePos(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $longitude = $request->request->get('longitude');
        if ($longitude === null)
        {
            throw new ControlException('Post parameter longitude is not set !');
        }
        
        $latitude = $request->request->get('latitude');
        if ($latitude === null)
        {
            throw new ControlException('Post parameter latitude is not set !');
        }
        
        $user = $this->mRepo->getUser($userId);
        
        $user->setLongitude($longitude);
        $user->setLatitude($latitude);
        
        $this->mRepo->updateUser($user);
        
        $response = array('status' => 'Ok', 'message' => 'Updated position !');
        
        return new JsonResponse($response);
    }
    
    /* Gets the position of followed friends allowing it.
     */
    public function listFriendsPos(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendIds = $this->mRepo->getFriendsIds($userId, array('ALLOWED'), array('FOLLOWED'));
        
        $friends = $this->mRepo->getUsers($friendIds, array('VISIBLE'));
        
        $list = array();
        
        foreach ($friends as $friend)
        {
            $list[] = array(
                                'id' => $friend->getId(),
                                'longitude' => $friend->getLongitude(),
                                'latitude' => $friend->getLatitude()
                            );
        }
        
        $response = array(
                            'status' => 'Ok',
                            'message' => 'Fetched friends positions',
                            'positions' => $list
                         );
        
        return new JsonResponse($response);
    }
    
    /** Gets the information for the user whose id is passed in user_id
     * POST parameter.
     */
    public function getUserInfo(Request $request)
    {
        $id = $request->request->get('user_id');
        if ($id === null)
        {
            throw new ControlException('Post parameter user_id is not set !');
        }
        
        $user = $this->mRepo->getUser($id);
        
        // We only send public data
        $response = array(
            'status' => 'Ok',
            'message' => 'Fetched user info !',
            'id' => $user->getId(),
            'name' => $user->getName(),
        );
        
        return new JsonResponse($response);
    }
    
    /** Sets an invitation for user with friend_id POST parameter.
     */
    public function inviteFriend(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $request->request->get('friend_id');
        if ($friendId === null)
        {
            throw new ControlException('Post parameter friend_id is not set !');
        }
        
        if ($userId == $friendId)
        {
            throw new ControlException('You cannot invite yourself !');
        }
        
        // Check if the user is already friend or already invited or already inviting
        $friendsIds = $this->mRepo->getFriendsIds($userId,
                                                  array('BLOCKED', 'ALLOWED', 'DISALLOWED'));
        
        $userInvitingIds = $this->mRepo->getInvitationIds($userId);
        $friendInvitingIds = $this->mRepo->getInvitationIds($friendId);
        
        if (!in_array($friendId, $friendsIds) AND !in_array($friendId, $userInvitingIds) AND
            !in_array($userId, $friendInvitingIds))
        {
            $this->mRepo->addInvitation($userId, $friendId);
            $response = array('status' => 'Ok', 'message' => 'Invited friend !');
        }
        else
        {
            $response = array('status' => 'error', 'message' => 'You are already friends or invited.');
        }
        
        return new JsonResponse($response);
    }
    
    /** Gets a list of user ids and names which are wanting to be friend
     * with the current user.
     */
    public function getInvitations(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $inviterIds = $this->mRepo->getInvitationIds($userId);
        
        $inviters = $this->mRepo->getUsers($inviterIds);
        
        $invitersList = array();
        
        foreach ($inviters as $inviter)
        {
            $invitersList[] = array('id' => $inviter->getId(), 'name' => $inviter->getName());
        }
        
        $response = array(
                            'status' => 'Ok',
                            'message' => 'Fetched invitations',
                            'list' => $invitersList
                         );
        
        return new JsonResponse($response);
    }
    
    /** Accpets the invitation from the user with in POST parameter 
     * friend_id.
     */
    public function acceptInvitation(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $request->request->get('friend_id');
        if ($friendId === null)
        {
            throw new ControlException('Post parameter friend_id is not set !');
        }
        
        // We check that the friend invited the user
        $invitersIds = $this->mRepo->getInvitationIds($userId);
        
        if (!in_array($friendId, $invitersIds))
        {
            throw new ControlException('Not invited by user with id ' . $friendId .' !');
        }
        
        $this->mRepo->removeInvitation($friendId, $userId);
        
        try
        {
            $this->mRepo->addFriendshipLink($userId, $friendId);
            $this->mRepo->addFriendshipLink($friendId, $userId);
        }
        catch (\Exception $e)
        {
            throw new ControlException('You are already friends !');
        }
        
        $user = $this->mRepo->getUser($friendId);
        
        $response = array(
                            'status' => 'Ok',
                            'message' => 'Accepted invitation !',
                            'id' => $user->getId(),
                            'name' => $user->getName(),
                            'longitude' => $user->getLongitude(),
                            'latitude' => $user->getLatitude()
                         );
        
        return new JsonResponse($response);
    }
    
    /** Gets a list of user names and ids where name begins with post parameter
     * search_text (ignores case). 
     */
    public function findUsers(Request $request)
    {
        $partialName = $request->request->get('search_text');
        if ($partialName === null)
        {
            throw new ControlException('Post parameter search_text is not set !');
        }
        
        $users = $this->mRepo->findUsersByPartialName($partialName);
        
        $data = array();
        foreach ($users as $user)
        {
            $data[] = array('id' => $user->getId(), 'name' => $user->getName());
        }
        
        $response = array('status' => 'Ok', 'message' => 'Fetched users !', 'list' => $data);
        
        return new JsonResponse($response);
    }
}
