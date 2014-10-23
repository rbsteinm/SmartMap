<?php

/**
 * /!\ This file must stay private. Making it public enables any one to impersonate SmartMap FB app
 * Time spent: 
 * -Installing composer + wamp: 1h (so you can't access localhost if skype is opened... wtf)
 * 
 */

namespace SmartMap\Control;

use Silex\Application;
use Symfony\Component\HttpFoundation\Session\Session;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;
use SmartMap\DBInterface\User;
use SmartMap\DBInterface\UserRepository;

use Facebook\FacebookSession;
use Facebook\FacebookRedirectLoginHelper;
use Facebook\FacebookRequest;
use Facebook\FacebookResponse;
use Facebook\FacebookSDKException;
use Facebook\FacebookRequestException;
use Facebook\FacebookAuthorizationException;
use Facebook\GraphObject;
use Facebook\GraphUser;


/**
 * This class handles client-server authentication
 * @author SpicyCH
 *
 */
class AuthenticationController {
    private static $APP_ID = '305881779616905';
    
    // This is the APP_SECRET, which is highly confidential as it could enable someone to impersonate the app
    private static $APP_SECRET = 'b851a1eb3edcaf637f92fbb2af2b3b47';
    private $mRepo;
    
    function __construct(UserRepository $repo) {
        $this->mRepo = $repo;
    }
    
    public function authenticate(Request $request) {
        // TODO by Robin
        try {
            FacebookSession::setDefaultApplication ( self::$APP_ID, self::$APP_SECRET );
            $name = $request->request->get('name');
            $facebookToken = $request->request->get('facebookToken');
            $facebookId = $request->request->get('facebookId');
            
            // Check if function called with proper parameters
            if($name == null or $facebookToken == null or $facebookId == null) {
                throw new \InvalidArgumentException();
            }
            
            // Check if token is valid and matches name + ID
            $session = new FacebookSession($facebookToken);
            
            // Validate session
            try {
                $session->validate();
            } catch (FacebookRequestException $ex) {
                // Session not valid, Graph API returned an exception with the reason.
                return new JsonResponse (array('status' => "ERROR: " . $ex->getMessage()));
            } catch (\Exception $ex) {
                // Graph API returned info, but it may mismatch the current app or have expired.
                return new JsonResponse (array('status' => "ERROR: " . $ex->getMessage()));
            }
            
            // At this point the session is valid. We check the session is associated with the user name and id.
            try {
            
                $user_profile = (new FacebookRequest(
                                $session, 'GET', '/me'
                ))->execute()->getGraphObject(GraphUser::className());
            
                if ($name != $user_profile->getName() OR $facebookId != $user_profile->getId()) {
                    throw new \UnexpectedValueException("Id and name do not match the fb access token");
                }
                
            
            } catch(FacebookRequestException $e) {
                return new JsonResponse (array('status' => "ERROR: " . $e->getMessage()));
            } catch (\UnexpectedValueException $e) {
                return new JsonResponse (array('status' => "ERROR: the name or the id associated with this token is " .
                                "not the one received via POST"));
            }
            
            // OK, user is successfully authenticated!
            
            // Configure the $_SESSION
            $session = new Session();
            $session->start();
            $session->set('LOGGED_IN', 'TRUE');
            
            // Refresh user's info in DB (TODO, rm $facebookToken)
            $user = new User ( $facebookId, $facebookToken, $name, 'VISIBLE', 0.0, 0.0 );
            
            // Update DB
            // TODO check if we can retrieve the user using his id. if not, create a new one in DB
            $user0 = $this->mRepo->getUser($facebookId);
            
            return new JsonResponse (array('status' => "OK"));
            
        } catch (\InvalidArgumentException $e0) {
            return new JsonResponse (array('status' => "ERROR: to authenticate, please send the following " .
                            "POST parameters: name, facebookId and facebookToken"));
        } catch (\Exception $e1) {
            return new JsonResponse (array('status' => "ERROR: " . $e1->getMessage()));
        }
    }
    public function registerUser(Request $request, Application $app) {
        // TODO by Robin
        

        // Here you should create a user (id does not matter but must be > 0)
        $user = new User ( 1, 'hahahahastayinalive123', 'Julien', 'VISIBLE', 1.0, 2.0 );
        
        // This creates the user in the database
        $user = $this->mRepo->createUser ( $user );
        
        // Should be replaced by Json
        return $user->getId ();
    }
    
    private function checkToken(User $user) {
        
    }
}
