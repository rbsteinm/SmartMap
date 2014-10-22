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
    
    public function getUserInfo(Request $request)
    {
        $id = $request->request->get('user_id');
        if ($id === null)
        {
            throw new \InvalidArgumentException('Post parameter user_id not set !');
        }
        
        $user = $this->mRepo->getUser($id);
        
        $response = array(
            'status' => 'Ok',
            'message' => '',
            'id' => $user->getId(),
            'name' => $user->getName(),
            'visibility' => $user->getVisibility(),
            'longitude' => $user->getLongitude(),
            'latitude' => $user->getLatitude()
        );
        
        return new JsonResponse($response);
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
