<?php
/**
 * Created by PhpStorm.
 * User: matthieu
 * Date: 13.12.14
 * Time: 18:11
 */
namespace SmartMap\Control;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;


/**
 * This class handles client-server authentication.
 *
 * @author SpicyCH
 *
 * @author Pamoi (code reviewed - 01.11.2014)
 */
interface AuthenticationControllerInterface
{
    /**
     * Authenticates the caller to the SmartMap server.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     * @throws \SmartMap\DBInterface\DatabaseException
     */
    public function authenticate(Request $request);
}