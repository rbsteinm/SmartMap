<?php
/**
 * Created by PhpStorm.
 * User: matthieu
 * Date: 13.12.14
 * Time: 18:12
 */
namespace SmartMap\Control;

use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;

interface EventControllerInterface
{
    /**
     * Creates a new event.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function createEvent(Request $request);

    /**
     * Updates an event. The user's id must be the same as the event's creator id.
     * This method will kindly update only the provided fields, so it is not
     * necessary to send all of them.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws ServerFeedbackException
     * @throws InvalidRequestException
     */
    public function updateEvent(Request $request);

    public function getPublicEvents(Request $request);

    /**
     * Adds the user to the event with id in post parameter event_id.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function joinEvent(Request $request);

    /**
     * Removes the user from the event with id in post parameter event_id.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function leaveEvent(Request $request);

    /**
     * Sends invitations to an event to a list of users.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function inviteUsersToEvent(Request $request);

    /**
     * Get the events to which the user is invited.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function getEventInvitations(Request $request);

    /**
     * Acknowledges an event invitation so it is not sent in getEventInvitations.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function ackEventInvitation(Request $request);

    /**
     * Get an event's information.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function getEventInfo(Request $request);
}