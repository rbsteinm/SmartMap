<?php

namespace SmartMap\Control;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;

use SmartMap\DBInterface\User;
use SmartMap\DBInterface\UserRepository;

class DataController
{
    private $mRepo;
    
    function __construct(UserRepository $repo)
    {
        $this->mRepo = $repo;
    }
    
    /* Updates the user's position.
     */
    public function updatePos(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $longitude = $request->request->get('longitude');
        if ($longitude === null)
        {
            throw new \InvalidArgumentException('Post parameter longitude is not set !');
        }
        
        $latitude = $request->request->get('latitude');
        if ($latitude === null)
        {
            throw new \InvalidArgumentException('Post parameter latitude is not set !');
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
    
    /* Gets the information for the user whose id is passed in user_id
     * POST parameter.
     */
    public function getUserInfo(Request $request)
    {
        $id = $request->request->get('user_id');
        if ($id === null)
        {
            throw new \InvalidArgumentException('Post parameter user_id is not set !');
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
    
    /* Sets an invitation for user with friend_id POST parameter.
     */
    public function inviteFriend(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $request->request->get('friend_id');
        if ($friendId === null)
        {
            throw new \InvalidArgumentException('Post parameter friend_id is not set !');
        }
        
        if ($userId == $friendId)
        {
            throw new \Exception('You cannot invite yourself !');
        }
        
        // Check if the user  blocked or already invited or already inviting
        $blockedIds = $this->mRepo->getFriendsIds($userId,
                                                  array('BLOCKED'));
        
        $userInvitingIds = $this->mRepo->getInvitationIds($userId);
        $friendInvitingIds = $this->mRepo->getInvitationIds($friendId);
        
        if (!in_array($friendId, $blockedIds) AND !in_array($friendId, $userInvitingIds) AND
            !in_array($userId, $friendInvitingIds))
        {
            $this->mRepo->addInvitation($userId, $friendId);
        }
        
        $response = array('status' => 'Ok', 'message' => 'Invited friend !');
        
        return new JsonResponse($response);
    }
    
    /* Gets a list of user ids and names which are wanting to be friend
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
    
    /* Accpets the invitation from the user with in POST parameter 
     * friend_id.
     */
    public function acceptInvitation(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $request->request->get('friend_id');
        if ($friendId === null)
        {
            throw new \InvalidArgumentException('Post parameter friend_id is not set !');
        }
        
        // We check that the friend invited the user
        $invitersIds = $this->mRepo->getInvitationIds($userId);
        
        if (!in_array($friendId, $invitersIds))
        {
            throw new \Exception('Not invited by user with id ' . $friendId .' !');
        }
        
        $this->mRepo->removeInvitation($friendId, $userId);
        
        $this->mRepo->addFriendshipLink($userId, $friendId);
        $this->mRepo->addFriendshipLink($friendId, $userId);
        
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
}
