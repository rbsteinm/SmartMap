<?php

namespace SmartMap\Control;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;

use SmartMap\DBInterface\UserRepository;
use SmartMap\DBInterface\User;
use SmartMap\DBInterface\DatabaseException;

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

    /**
     * Allow a friend to see the user's location.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function allowFriend(Request $request)
    {
        $userId = RequestUtils::getIdFromRequest($request);
        
        $friendId = RequestUtils::getPostParam($request, 'friend_id');
        
        try
        {
            $this->mRepo->setFriendshipStatus($userId, $friendId, 'ALLOWED');
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in allowFriend method.', 2, $e);
        }
        
        $response = array('status' => 'Ok', 'message' => 'Allowed friend !');
        
        return new JsonResponse($response);
    }

    /**
     * Disallow a friend to see the user's location.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function disallowFriend(Request $request)
    {
        $userId = RequestUtils::getIdFromRequest($request);
        
        $friendId = RequestUtils::getPostParam($request, 'friend_id');
        
        try
        {
            $this->mRepo->setFriendshipStatus($userId, $friendId, 'DISALLOWED');
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in disallowFriend method.', 2, $e);
        }
        
        $response = array('status' => 'Ok', 'message' => 'Disallowed friend !');
        
        return new JsonResponse($response);
    }

    /**
     * Allow friends in a list of ids separated by commas to see
     * the user's location.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function allowFriendList(Request $request)
    {
        $userId = RequestUtils::getIdFromRequest($request);
        
        $friendsIds = RequestUtils::getPostParam($request, 'friend_ids');
        
        $friendsIds = RequestUtils::getIntArrayFromString($friendsIds);
        
        try
        {
            $this->mRepo->setFriendshipsStatus($userId, $friendsIds, 'ALLOWED');
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in allowFriendList method.', 2, $e);
        }
        
        $response = array('status' => 'Ok', 'message' => 'Allowed friend list !');
        
        return new JsonResponse($response);
    }

    /**
     * Disallow friends in a list of ids separaed by commas to see
     * the user's location.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function disallowFriendList(Request $request)
    {
        $userId = RequestUtils::getIdFromRequest($request);
        
        $friendsIds = RequestUtils::getPostParam($request, 'friend_ids');
        
        $friendsIds = RequestUtils::getIntArrayFromString($friendsIds);
        
        try
        {
            $this->mRepo->setFriendshipsStatus($userId, $friendsIds, 'DISALLOWED');
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in disallowFriendList method.', 2, $e);
        }
        
        $response = array('status' => 'Ok', 'message' => 'Disallowed friend list !');
        
        return new JsonResponse($response);
    }

    /**
     * Follow a friend to be notified of his position.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function followFriend(Request $request)
    {
        $userId = RequestUtils::getIdFromRequest($request);
        
        $friendId = RequestUtils::getPostParam($request, 'friend_id');
        
        try
        {
            $this->mRepo->setFriendshipFollow($userId, $friendId, 'FOLLOWED');
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in followFriend method.', 2, $e);
        }
        
        $response = array('status' => 'Ok', 'message' => 'Followed friend !');
        
        return new JsonResponse($response);
    }

    /**
     * Unfollow a friend to be no longer notified of his position.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function unfollowFriend(Request $request)
    {
        $userId = RequestUtils::getIdFromRequest($request);
        
        $friendId = RequestUtils::getPostParam($request, 'friend_id');
        
        try
        {
            $this->mRepo->setFriendshipFollow($userId, $friendId, 'UNFOLLOWED');
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in unfollowFriend method.', 2, $e);
        }
        
        $response = array('status' => 'Ok', 'message' => 'Unfollowed friend !');
        
        return new JsonResponse($response);
    }

    public function setVisibility(Request $request)
    {
        $userId = RequestUtils::getIdFromRequest($request);

        $visibility = RequestUtils::getPostParam($request, 'visibility');

        try
        {
            $user = $this->mRepo->getUser($userId);

            $user->setVisibility($visibility);

            $this->mRepo->updateUser($user);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in setVisibility method.', 2, $e);
        }
        catch (\InvalidArgumentException $e)
        {
            throw new InvalidRequestException($e->getMessage());
        }

        $response = array('status' => 'Ok', 'message' => 'Visibility changed.');

        return new JsonResponse($response);
    }

    public function blockFriend(Request $request)
    {
        $userId = RequestUtils::getIdFromRequest($request);

        $friendId = RequestUtils::getPostParam($request, 'friend_id');

        try
        {
            $this->mRepo->setFriendshipStatus($userId, $friendId, 'DISALLOWED');

            $this->mRepo->setFriendshipFollow($userId, $friendId, 'UNFOLLOWED');
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in blockFriend.', 2, $e);
        }

        $response = array('status' => 'Ok', 'message' => 'Blocked friend.');

        return new JsonResponse($response);
    }

    public function unblockFriend(Request $request)
    {
        $userId = RequestUtils::getIdFromRequest($request);

        $friendId = RequestUtils::getPostParam($request, 'friend_id');

        try
        {
            $this->mRepo->setFriendshipStatus($userId, $friendId, 'ALLOWED');

            $this->mRepo->setFriendshipFollow($userId, $friendId, 'FOLLOWED');
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in unblockFriend.', 2, $e);
        }

        $response = array('status' => 'Ok', 'message' => 'Unblocked friend.');

        return new JsonResponse($response);
    }
}
