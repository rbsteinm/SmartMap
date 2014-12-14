<?php
/**
 * Created by PhpStorm.
 * User: matthieu
 * Date: 13.12.14
 * Time: 18:05
 */
namespace SmartMap\Control;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;


/**
 * This class handles all the authorizations (allow, disallow friends, ...) between the users.
 *
 * @author Pamoi
 *
 * @author SpicyCH (code reviewed - 02.11.2014) : code looks fine, just added javadoc
 */
interface AuthorizationControllerInterface
{
    /**
     * Allow a friend to see the user's location.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function allowFriend(Request $request);

    /**
     * Disallow a friend to see the user's location.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function disallowFriend(Request $request);

    /**
     * Allow friends in a list of ids separated by commas to see
     * the user's location.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function allowFriendList(Request $request);

    /**
     * Disallow friends in a list of ids separaed by commas to see
     * the user's location.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function disallowFriendList(Request $request);

    /**
     * Follow a friend to be notified of his position.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function followFriend(Request $request);

    /**
     * Unfollow a friend to be no longer notified of his position.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function unfollowFriend(Request $request);

    /**
     * Set the visibility of the user.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function setVisibility(Request $request);

    /**
     * Block a friend with id in post parameter friend_id.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function blockFriend(Request $request);

    /**
     * Unblock a friend with id in post parameter friend_id.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function unblockFriend(Request $request);
}