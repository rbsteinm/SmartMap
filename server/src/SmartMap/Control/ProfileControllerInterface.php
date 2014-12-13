<?php
/**
 * Created by PhpStorm.
 * User: matthieu
 * Date: 13.12.14
 * Time: 18:03
 */
namespace SmartMap\Control;

use Silex\Application;
use Symfony\Component\HttpFoundation\Request;

interface ProfileControllerInterface
{
    /**
     * Get the profile picture of a user.
     *
     * @param Request $request
     * @param Application $app
     * @return \Symfony\Component\HttpFoundation\BinaryFileResponse
     * @throws InvalidRequestException
     */
    public function getProfilePicture(Request $request, Application $app);
}