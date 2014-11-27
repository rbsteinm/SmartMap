<?php

namespace SmartMap\Control;

use SmartMap\DBInterface\DatabaseException;
use SmartMap\DBInterface\EventRepository;
use SmartMap\DBInterface\Event;

use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;

class EventController {

    private $mRepo;

    public function __construct(EventRepository $repo)
    {
        $this->mRepo = $repo;
    }

    /**
     * Creates a new event.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function createEvent(Request $request)
    {
        $userId = RequestUtils::getIdFromRequest($request);

        $startingDate = RequestUtils::getPostParam($request, 'starting');
        $endingDate = RequestUtils::getPostParam($request, 'ending');

        $longitude = RequestUtils::getPostParam($request, 'longitude');
        $latitude = RequestUtils::getPostParam($request, 'latitude');
        $positionName = RequestUtils::getPostParam($request, 'positionName');

        $name = RequestUtils::getPostParam($request, 'name');
        $description = RequestUtils::getPostParam($request, 'description');

        try
        {
            $event = new Event(
                1,
                $userId,
                $startingDate,
                $endingDate,
                $longitude,
                $latitude,
                $positionName,
                $name,
                $description
            );

            $event = $this->mRepo->createEvent($event);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in createEvent.', 2, $e);
        }
        catch (\InvalidArgumentException $e)
        {
            throw new InvalidRequestException($e->getMessage());
        }

        $response = array('status' => 'Ok', 'message' => 'Created event.', 'id' => $event->getId());

        return new JsonResponse($response);
    }

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
    public function updateEvent(Request $request)
    {
        $eventId = RequestUtils::getPostParam($request, 'eventId');

        $userId = RequestUtils::getIdFromRequest($request);

        // As we may want to update only some fields, we do not throw if some are not set
        $startingDate = RequestUtils::getPostParam($request, 'starting', false);
        $endingDate = RequestUtils::getPostParam($request, 'ending', false);

        $longitude = RequestUtils::getPostParam($request, 'longitude', false);
        $latitude = RequestUtils::getPostParam($request, 'latitude', false);
        $positionName = RequestUtils::getPostParam($request, 'positionName', false);

        $name = RequestUtils::getPostParam($request, 'name', false);
        $description = RequestUtils::getPostParam($request, 'description', false);

        try
        {
            $event = $this->mRepo->getEvent($eventId);

            if ($userId != $event->getCreatorId())
            {
                throw new ServerFeedbackException('You cannot edit this event.');
            }

            // Update only the provided fields
            if ($startingDate != null) {
                $event->setStartingDate($startingDate);
            }
            if ($endingDate != null)
            {
                $event->setEndingDate($endingDate);
            }
            if ($longitude != null)
            {
                $event->setLongitude($longitude);
            }
            if ($latitude != null)
            {
                $event->setLatitude($latitude);
            }
            if ($positionName != null)
            {
                $event->setPositionName($positionName);
            }
            if ($name != null)
            {
                $event->setName($name);
            }
            if ($description != null)
            {
                $event->setDescription($description);
            }

            $this->mRepo->updateEvent($event);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in updateEvent.', 2, $e);
        }
        catch (\InvalidArgumentException $e)
        {
            throw new InvalidRequestException($e->getMessage());
        }

        $response = array('status' => 'Ok', 'message' => 'Updated event.');

        return new JsonResponse($response);
    }

    public function getPublicEvents(Request $request)
    {
        RequestUtils::getIdFromRequest($request);

        $longitude = RequestUtils::getPostParam($request, 'longitude');

        $latitude = RequestUtils::getPostParam($request, 'latitude');

        $radius = RequestUtils::getPostParam($request, 'radius');

        try
        {
            $events = $this->mRepo->getEventsInRadius($longitude, $latitude, $radius);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in getPublicEvents', 2, $e);
        }

        $eventsArray = array();

        foreach ($events as $event)
        {
            try
            {
                $participants = $this->mRepo->getEventParticipants($event->getId());
            }
            catch (DatabaseException $e)
            {
                throw new ControlLogicException('Error in getPublicEvents', 2, $e);
            }

            $eventsArray[] = $this->eventInfoArray($event, $participants);
        }

        $response = array('status' => 'Ok', 'message' => 'Fetched events.', 'events' => $eventsArray);

        return new JsonResponse($response);
    }

    /**
     * Adds the user to the event with id in post parameter event_id.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     */
    public function joinEvent(Request $request)
    {
        $userId = RequestUtils::getIdFromRequest($request);

        $eventId = RequestUtils::getPostParam($request, 'event_id');

        try
        {
            $this->mRepo->addUserToEvent($eventId, $userId);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in joinEvent.', 2, $e);
        }

        $response = array('status' => 'Ok', 'message' => 'Joined event.');

        return new JsonResponse($response);
    }

    public function inviteUsersToEvent(Request $request)
    {
        RequestUtils::getIdFromRequest($request);

        $eventId = RequestUtils::getPostParam($request, 'event_id');

        $userIds = RequestUtils::getIntArrayFromString(RequestUtils::getPostParam($request, 'users_ids'));

        try
        {
            $this->mRepo->addEventInvitations($eventId, $userIds);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in inviteUsersToEvent.', 2, $e);
        }

        $response = array('status' => 'Ok', 'message' => 'Invited users.');

        return new JsonResponse($response);
    }

    public function getEventInvitations(Request $request)
    {
        $userId = RequestUtils::getIdFromRequest($request);

        try
        {
            $eventsIds = $this->mRepo->getEventInvitations($userId);

            $eventsInfo = array();

            foreach ($eventsIds as $eventId)
            {
                $event = $this->mRepo->getEvent($eventId);

                $eventParticipants = $this->mRepo->getEventParticipants($eventId);

                $eventsInfo[] = $this->eventInfoArray($event, $eventParticipants);
            }
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in getEventInvitations.', 2, $e);
        }

        $response = array('status' => 'Ok', 'message' => 'Fetched events.', 'events' => $eventsInfo);

        return new JsonResponse($response);
    }

    public function ackEventInvitation(Request $request)
    {
        $userId = RequestUtils::getIdFromRequest($request);

        $eventId = RequestUtils::getPostParam($request, 'event_id');

        try
        {
            $this->mRepo->removeEventInvitation($eventId, $userId);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in ackEventInvitation.', 2, $e);
        }

        $response = array('status' => 'Ok', 'message' => 'Acknowledged event invitation.');

        return new JsonResponse($response);
    }

    public function getEventInfo(Request $request)
    {
        RequestUtils::getIdFromRequest($request);

        $eventId = RequestUtils::getPostParam($request, 'event_id');

        try
        {
            $event = $this->mRepo->getEvent($eventId);

            $participants = $this->mRepo->getEventParticipants($eventId);
        }
        catch (DatabaseException $e)
        {
            throw new ControlLogicException('Error in getEventInfo.', 2, $e);
        }

        $eventsInfo = $this->eventInfoArray($event, $participants);

        $response = array('status' => 'Ok', 'message' => 'Fetched event.', 'event' => $eventsInfo);

        return new JsonResponse($response);
    }

    private function eventInfoArray(Event $event, $participants = array())
    {
        return array(
            'id' => $event->getId(),
            'creatorId' => $event->getCreatorId(),
            'startingDate' => $event->getStartingDate(),
            'endingDate' => $event->getEndingDate(),
            'longitude' => $event->getLongitude(),
            'latitude' => $event->getLatitude(),
            'positionName' => $event->getPositionName(),
            'name' => $event->getName(),
            'description' => $event->getDescription(),
            'participants' => $participants
        );
    }
}
