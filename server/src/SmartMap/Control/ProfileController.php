<?php

namespace SmartMap\Control;

use Silex\Application;

use Symfony\Component\HttpFoundation\Request;

class ProfileController implements ProfileControllerInterface
{
    public static $PICTURES_PATH = '../pictures/';

    /**
     * Get the profile picture of a user.
     *
     * @param Request $request
     * @param Application $app
     * @return \Symfony\Component\HttpFoundation\BinaryFileResponse
     * @throws InvalidRequestException
     */
    public function getProfilePicture(Request $request, Application $app)
    {
        // We check that we are authenticated.
        RequestUtils::getIdFromRequest($request);

        // We cast the value of the post parameter to int to avoid path injection
        $id = (int) RequestUtils::getPostParam($request, 'user_id');
        
        $imagePath = self::$PICTURES_PATH . $id . '.jpg';
        if (!file_exists($imagePath))
        {
            // This is should not happen in production !
            if ($app['debug'] == false)
            {
                $app['monolog']->addWarning('Missing profile picture for user with id ' . $id . ' !');
            }
            return $app->sendFile(self::$PICTURES_PATH . 'default.jpg');
        }
        
        return $app->sendFile($imagePath);
    }
}