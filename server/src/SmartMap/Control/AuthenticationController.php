<?php

namespace SmartMap\Control;

use Silex\Application;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;

use SmartMap\DBInterface\User;
use SmartMap\DBInterface\UserRepository;

class AuthenticationController
{
    private $mRepo;
    
    function __construct(UserRepository $repo)
    {
        $this->mRepo = $repo;
    }
    
    public function authenticate(Request $request, Application $app)
    {
        // TODO by Robin
        
        return new JsonResponse();
    }
    
    public function registerUser(Request $request, Application $app)
    {
        // TODO by Robin
        
        // Here you should create a user (id does not matter but must be > 0)
        $user = new User(1, 'hahahahastayinalive123', 'Julien', 'VISIBLE', 1.0, 2.0);
        
        // This creates the user in the database
        $user = $this->mRepo->createUser($user);
        
        // Should be replaced by Json
        return $user->getId();
    }
}
