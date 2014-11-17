<?php

namespace SmartMap\Control;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;

use SmartMap\DBInterface\User;
use SmartMap\DBInterface\UserRepository;
use SmartMap\DBInterface\DatabaseException;

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
    
    /**
     * Updates the user's position.
     * 
     * @param Request $request
     * @throws InvalidRequestException
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function updatePos(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $longitude = $this->getPostParam($request, 'longitude');
        
        $latitude = $this->getPostParam($request, 'latitude');
        
        try
        {
            $user = $this->mRepo->getUser($userId);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in updatePos.', 2, $e);
        }
        
        try
        {
            $user->setLongitude($longitude);
            $user->setLatitude($latitude);
        }
        catch (\InvalidArgumentException $e)
        {
            throw new InvalidRequestException('Invalid coordinates.');
        }
        
        try
        {
            $this->mRepo->updateUser($user);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in updatePos.', 2, $e);
        }
        $response = array('status' => 'Ok', 'message' => 'Updated position !');
        
        return new JsonResponse($response);
    }
    
    /**
     * Gets the position of followed friends allowing it.
     * 
     * @param Request $request
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function listFriendsPos(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        try
        {
            $friendIds = $this->mRepo->getFriendsIds($userId, array('ALLOWED'), array('FOLLOWED'));
            
            $friends = $this->mRepo->getUsers($friendIds, array('VISIBLE'));
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in listFriendsPos.', 2, $e);
        }
        
        $list = array();
        
        foreach ($friends as $friend)
        {
            $list[] = array(
                'id' => $friend->getId(),
                'longitude' => $friend->getLongitude(),
                'latitude' => $friend->getLatitude(),
                'lastUpdate' => $friend->getLastUpdate()
            );
        }
        
        $response = array(
            'status' => 'Ok',
            'message' => 'Fetched friends positions !',
            'positions' => $list
        );
        
        return new JsonResponse($response);
    }
    
    /**
     * Gets the information for the user whose id is passed in user_id POST parameter.
     * 
     * @param Request $request
     * @throws InvalidRequestException
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function getUserInfo(Request $request)
    {
        $id = $this->getPostParam($request, 'user_id');
        
        try
        {
            $user = $this->mRepo->getUser($id);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in getUserInfo.', 2, $e);
        }
        // We only send public data
        $response = array(
            'status' => 'Ok',
            'message' => 'Fetched user info !',
            'id' => $user->getId(),
            'name' => $user->getName(),
        );
        
        return new JsonResponse($response);
    }
    
    /**
     * Sets an invitation for user with friend_id POST parameter.
     * 
     * @param Request $request
     * @throws InvalidRequestException
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function inviteFriend(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $this->getPostParam($request, 'friend_id');
        
        if ($userId == $friendId)
        {
            throw new InvalidRequestException('You cannot invite yourself !');
        }
        
        try
        {
            // Check if the user is already friend or already invited or already inviting
            $friendsIds = $this->mRepo->getFriendsIds($userId,
                                                      array('BLOCKED', 'ALLOWED', 'DISALLOWED'));
            
            $userInvitingIds = $this->mRepo->getInvitationIds($userId);
            $friendInvitingIds = $this->mRepo->getInvitationIds($friendId);
            
            if (!in_array($friendId, $friendsIds) AND !in_array($friendId, $userInvitingIds) AND
                !in_array($userId, $friendInvitingIds))
            {
                $this->mRepo->addInvitation($userId, $friendId);
            }
            else
            {
                throw new InvalidRequestException('You are already friends or invited.');
            }
            
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in inviteFriend.', 2, $e);
        }
        
        $response = array('status' => 'Ok', 'message' => 'Invited friend !');
        
        return new JsonResponse($response);
    }
    
    /**
     * Gets a list of user ids and names which are wanting to be friend
     * with the current user.
     * 
     * @param Request $request
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function getInvitations(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        try
        {
            $inviterIds = $this->mRepo->getInvitationIds($userId);
            
            $inviters = $this->mRepo->getUsers($inviterIds);
            
            $acceptedInvitationIds = $this->mRepo->getAcceptedInvitations($userId);
            
            $newFriends = $this->mRepo->getUsers($acceptedInvitationIds);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in getInvitations.', 2, $e);
        }
        
        $invitersList = array();
        
        foreach ($inviters as $inviter)
        {
            $invitersList[] = array('id' => $inviter->getId(), 'name' => $inviter->getName());
        }
        
        $friendsList = array();
        
        foreach ($newFriends as $friend)
        {
            $friendsList[] = array(
                'id' => $friend->getId(),
                'name' => $friend->getName(),
                'longitude' => $friend->getLongitude(),
                'latitude' => $friend->getLatitude(),
                'lastUpdate' => $friend->getLastUpdate()
            );
        }
        
        $response = array(
            'status' => 'Ok',
            'message' => 'Fetched invitations !',
            'invitations' => $invitersList,
            'newFriends' => $friendsList
        );
        
        return new JsonResponse($response);
    }
    
    /**
     * Accpets the invitation from the user with in POST parameter friend_id.
     * 
     * @param Request $request
     * @throws InvalidRequestException
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function acceptInvitation(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $this->getPostParam($request, 'friend_id');
        
        try
        {
            // We check that the friend invited the user
            $invitersIds = $this->mRepo->getInvitationIds($userId);
            
            if (!in_array($friendId, $invitersIds))
            {
                throw new InvalidRequestException('Not invited by user with id ' . $friendId .' !');
            }
            
            $this->mRepo->removeInvitation($friendId, $userId);
        
            $this->mRepo->addFriendshipLink($userId, $friendId);
            $this->mRepo->addFriendshipLink($friendId, $userId);
            
            $this->mRepo->addAcceptedInvitation($userId, $friendId);
        
            $user = $this->mRepo->getUser($friendId);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in acceptInvitation.', 2, $e);
        }
        
        $response = array(
            'status' => 'Ok',
            'message' => 'Accepted invitation !',
            'id' => $user->getId(),
            'name' => $user->getName(),
            'longitude' => $user->getLongitude(),
            'latitude' => $user->getLatitude(),
            'lastUpdate' => $user->getLastUpdate()
        );
        
        return new JsonResponse($response);
    }
    
    /**
     * Declines an invitation.
     * 
     * @param Request $request
     * @throws ControlLogicException
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function declineInvitation(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $this->getPostParam($request, 'friend_id');
        
        try
        {
            $this->mRepo->removeInvitation($friendId, $userId);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in declineInvitation.', 2, $e);
        }
        
        $response = array('status' => 'Ok', 'message' => 'Declined invitation !');
        
        return new JsonResponse($response);
    }
    
    public function ackAcceptedInvitation(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $this->getPostParam($request, 'friend_id');
        
        try
        {
            $this->mRepo->removeAcceptedInvitation($userId, $friendId);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in ackAcceptedInvitation.', 2, $e);
        }
        
        $response = array('status' => 'Ok', 'message' => 'Acknowledged accepted invitation !');
        
        return new JsonResponse($response);
    }
    
    /**
     * Removes a friend.
     * 
     * @param Request $request
     * @throws ControlLogicException
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function removeFriend(Request $request)
    {
        $userId = User::getIdFromRequest($request);
        
        $friendId = $this->getPostParam($request, 'friend_id');
        
        try
        {
            $this->mRepo->removeFriendshipLink($userId, $friendId);
            $this->mRepo->removeFriendshipLink($friendId, $userId);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in removeFriend.', 2, $e);
        }
        
        $response = array('status' => 'Ok', 'message' => 'Removed friend !');
        
        return new JsonResponse($response);
    }
    
    /**
     * Gets a list of user names and ids where name begins with post parameter
     * search_text (ignores case). 
     * 
     * @param Request $request
     * @throws InvalidRequestException
     * @return \Symfony\Component\HttpFoundation\JsonResponse
     */
    public function findUsers(Request $request)
    {
        // We check that we are authenticated.
        $id = User::getIdFromRequest($request);
        
        $partialName = $this->getPostParam($request, 'search_text');
        
        try
        {
            $friendsIds = $this->mRepo->getFriendsIds($id);
            
            $users = $this->mRepo->findUsersByPartialName($partialName, $friendsIds);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in findUsers.', 2, $e);
        }
        
        $data = array();
        foreach ($users as $user)
        {
            $data[] = array('id' => $user->getId(), 'name' => $user->getName());
        }
        
        $response = array('status' => 'Ok', 'message' => 'Fetched users !', 'list' => $data);
        
        return new JsonResponse($response);
    }
    
    /**
     * Utility function getting a post parameter and throwing a ControlException
     * if the parameter is not set in the request.
     *
     * @param Request $request
     * @param string $param
     * @throws InvalidRequestException
     * @return string
     */
    private function getPostParam(Request $request, $param)
    {
        $value = $request->request->get($param);
        
        if ($value === null)
        {
            throw new InvalidRequestException('Post parameter ' . $param . ' is not set !');
        }
        
        return $value;
    }
}
