<?php

namespace SmartMap\Control;

use Silex\Application;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;

class AuthenticationController
{
    public function authenticate(Request $request, Application $app)
    {
        // TODO by Robin
        return new JsonResponse(array('request' => 'OK'));
    }
}
