<?php

namespace SmartMap\Control;

use Silex\Application;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;

use SmartMap\DBInterface\User;
use SmartMap\DBInterface\UserRepository;

class DataController
{
    private $mRepo;
    
    function __construct(UserRepository $repo)
    {
        $this->mRepo = $repo;
    }
    
    public function getUserInfo(Request $request, Application $app)
    {
        $user = $this->mRepo->getUser(4);
        
        return $user->getName();
    }
    
    public function inviteFriend(Request $request, Application $app)
    {
        
    }
    
    public function getInvitations(Request $request, Application $app)
    {
        
    }
    
    public function acceptInvitation(Request $request, Application $app)
    {
        
    }
}
