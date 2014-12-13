<?php

namespace SmartMap\Control;

use Silex\Application;

use Symfony\Component\HttpFoundation\Session\Session;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;

use SmartMap\DBInterface\User;
use SmartMap\DBInterface\UserRepositoryInterface;

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
 * This class handles client-server authentication.
 *
 * @author SpicyCH
 *        
 * @author Pamoi (code reviewed - 01.11.2014)
 */
class AuthenticationController implements AuthenticationControllerInterface
{
    private static $GRAPH_API_URL = 'http://graph.facebook.com/v2.2/';
    private static $PICTURE_REQUEST = '/picture?width=480&height=480';
    
    private $mAppSecret;
    private $mAppId;
    private $mRepo;
    
    function __construct(UserRepositoryInterface $repo, $appId, $appSecret) {
        $this->mRepo = $repo;
        $this->mAppId = $appId;
        $this->mAppSecret = $appSecret;
    }

    /**
     * Authenticates the caller to the SmartMap server.
     *
     * @param Request $request
     * @return JsonResponse
     * @throws ControlLogicException
     * @throws InvalidRequestException
     * @throws \SmartMap\DBInterface\DatabaseException
     */
    public function authenticate(Request $request)
    {
        FacebookSession::setDefaultApplication($this->mAppId, $this->mAppSecret);
        
        $name = $request->request->get('name');
        $facebookToken = $request->request->get('facebookToken');
        $facebookId = $request->request->get('facebookId');
        
        // Check if function called with proper parameters
        if ($name == null OR $facebookToken == null OR $facebookId == null)
        {
            throw new InvalidRequestException('Missing POST parameter.');
        }
        
        // Check if token is valid and matches name + ID
        $fbSession = new FacebookSession($facebookToken);
        
        // Validate session
        try
        {
            $fbSession->validate();
        }
        catch (FacebookRequestException $ex)
        {
            // Session not valid, Graph API returned an exception with the reason.
            throw new InvalidRequestException('Invalid Facebook session.');
        }
        catch (\Exception $ex)
        {
            // Graph API returned info, but it may mismatch the current app or have expired.
            throw new InvalidRequestException('Mismatch or expired facebook data.');
        }
        
        // At this point the session is valid. We check the session is associated with the user name and id.
        try
        {
            $user_profile = (new FacebookRequest($fbSession, 'GET', '/me'))->execute()->getGraphObject( 
                             GraphUser::className());
            if ($name != $user_profile->getName() OR $facebookId != $user_profile->getId())
            {
                throw new InvalidRequestException('Id and name do not match the fb access token.');
            }
        }
        catch (FacebookRequestException $e)
        {
            throw new ControlLogicException($e->getMessage());
        }
        
        // OK, at this point user is successfully authenticated!
        
        // Configure the session
        $session = $request->getSession();
        if ($session == null)
        {
            throw new InvalidRequestException('Session is null. Did you send session cookie ?');
        }
        
        // Set session parameter or create new user
        $userId = $this->mRepo->getUserIdFromFb($facebookId);
        if (!$userId)
        {
            // The first parameter of the new User is not relevant as it will be set in the call
            // to createUser
            $user = new User(1 , $facebookId, $name, 'VISIBLE', 0.0, 0.0);
            $user = $this->mRepo->createUser($user);
            $session->set('userId', $user->getId());

            // We set the variable used for the response
            $userId = $user->getId();
            
            // Getting the user facebook profile image to set it as default.
            $pic = file_get_contents(self::$GRAPH_API_URL . $user->getFbId() . self::$PICTURE_REQUEST);
        
            file_put_contents(ProfileController::$PICTURES_PATH . $user->getId() . '.jpg', $pic);
        }
        else
        {
            $session->set('userId', $userId);
        }
        
        $response = array('status' => 'Ok', 'message' => 'Successfully authenticated !', 'id' => $userId);
        
        return new JsonResponse($response);
    }
    
    /*
     * Development method used to simplify tests
     */
    public function fakeAuth(Request $request)
    {
        $id = $request->request->get('user_id');
        if ($id === null)
        {
            throw new \InvalidRequestException('Field user_id is not set !');
        }
        $session = $request->getSession();
        if ($session == null)
        {
            throw new \InvalidRequestException('Session is null.');
        }
        $session->set('userId', $id);
        return new JsonResponse (array(
                        'status' => 'Ok',
                        'message' => 'Fakeauthentified as id ' . $session->get('userId')
        ));
    }
}
