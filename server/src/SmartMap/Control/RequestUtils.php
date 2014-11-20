<?php

namespace SmartMap\Control;


use Symfony\Component\HttpFoundation\Request;

/**
 * This class contains static utility methods that access request fields
 * and throws appropriate exceptions if they are not set.
 *
 * @package SmartMap\Control
 *
 * @author Pamoi
 */
class RequestUtils
{
    /**
     * Get a post parameter from a request and throws an InvalidRequestException
     * if this parameter is not set in the request if $throw is true (default).
     *
     * @param Request $request
     * @param $name
     * @return mixed
     * @throws InvalidRequestException
     */
    public static function getPostParam(Request $request, $name, $throw = true)
    {
        $value = $request->request->get($name);

        if ($value === null)
        {
            if ($throw == true)
            {
                throw new InvalidRequestException('Post parameter ' . $name . ' is not set !');
            }
        }

        return $value;
    }

    /**
     * Get the current user id from a request, and throws an InvalidRequestException
     * if the session does not contain the field 'userId'.
     *
     * @param Request $request
     * @return mixed
     * @throws InvalidRequestException
     */
    public static function getIdFromRequest(Request $request)
    {
        if (!$request->hasSession())
        {
            throw new InvalidRequestException('Trying to access session but the session is not started.');
        }

        $session = $request->getSession();

        // The userId is set in the session when successfully authenticated
        $id = $session->get('userId');

        if ($id == null)
        {
            throw new InvalidRequestException('The user is not authenticated.');
        }

        return $id;
    }
} 