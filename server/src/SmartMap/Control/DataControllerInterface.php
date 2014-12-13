<?php
/**
 * Created by PhpStorm.
 * User: matthieu
 * Date: 13.12.14
 * Time: 18:02
 */
namespace SmartMap\Control;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;


/**
 *
 *
 * @author Pamoi
 *
 * @author SpicyCH (code reviewed - 03.11.2014) : good logic, but need unit testing!
 */
interface DataControllerInterface
{
    /**
     * Updates the user's position.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function updatePos(Request $request);

    /**
     * Gets the position of followed friends allowing it.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function listFriendsPos(Request $request);

    /**
     * Get the user's friends ids.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function getFriendsIds(Request $request);

    /**
     * Gets the information for the user whose id is given in user_id POST parameter.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function getUserInfo(Request $request);

    /**
     * Sends an invitation to the user with id in post parameter.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     * @throws ServerFeedbackException
     */
    public function inviteFriend(Request $request);

    /**
     * Gets a list of the pending invitations, a list of the friends that
     * accepted the user's invitation and a list of the ids of the friends
     * that removed the user.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function getInvitations(Request $request);

    /**
     * Accepts an invitation.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function acceptInvitation(Request $request);

    /**
     * Declines an invitation.
     *
     * @param Request $request
     * @throws ControlLogicException
     * @return JsonResponse
     */
    public function declineInvitation(Request $request);

    /**
     * Acknowledges an accepted invitation so it is no more sent in getInvitations.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function ackAcceptedInvitation(Request $request);

    /**
     * Removes a friend.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws ServerFeedbackException
     */
    public function removeFriend(Request $request);

    /**
     * Acknowledges a removed friend so it is no longer sent in getInvitations
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function ackRemovedFriend(Request $request);

    /**
     * Gets a list of user names and ids where name begins with post parameter
     * search_text (ignores case).
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function findUsers(Request $request);
}