<?php

namespace SmartMap\Control;

use Silex\Application;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;

use SmartMap\DBInterface\User;
use SmartMap\DBInterface\UserRepository;
use SmartMap\DBInterface\DatabaseException;

class ProfileController
{
    private static $PICTURES_PATH = '../pictures/';
    
    public function getProfilePicture(Request $request, Application $app)
    {
        // We check that we are authenticated.
        User::getIdFromRequest($request);
        
        $id = $this->getPostParam($request, 'user_id');
        
        $imagePath = self::$PICTURES_PATH . $id . '.jpg';
        if (!file_exists($imagePath)) {
            throw new ControlLogicException('User with id ' . $id . 'has no profile picture !');
        }
        
        return $app->sendFile($imagePath);
    }
    
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