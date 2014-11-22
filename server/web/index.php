<?php

require_once __DIR__.'/../vendor/autoload.php';

use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;

use Monolog\Logger;
use Monolog\Handler\StreamHandler;

use Silex\Application;

$app = new Application();

// Options
$options = json_decode(file_get_contents(__DIR__ . '/../config.json'), true);

$app['debug'] = $options['debug'];

// Database connection
$app->register(new Silex\Provider\DoctrineServiceProvider(), array(
    'db.options' => array(
        'driver' => $options['db']['driver'],
        'host' => $options['db']['host'],
        'dbname' => $options['db']['dbname'],
        'user' => $options['db']['user'],
        'password' => $options['db']['password'],
        'charset' => $options['db']['charset']
    )
));

// Enabling sessions
$app->register(new Silex\Provider\SessionServiceProvider());

// Injecting repositories
$app['user.repository'] = $app->share(function() use($app) {
    return new SmartMap\DBInterface\UserRepository($app['db']);
});

$app['event.repository'] = $app->share(function() use($app) {
    return new SmartMap\DBInterface\EventRepository($app['db']);
});

// Injecting logging service
$app['logging'] = $app->share(function() use($app, $options) {
    $logger = new Logger('logging');
    $logger->pushHandler(new StreamHandler('../' . $options['monolog']['logfile'], Logger::INFO));
   return $logger;
});

// Injecting controllers
$app->register(new Silex\Provider\ServiceControllerServiceProvider());

$app['authentication.controller'] = $app->share(function() use($app, $options) {
    return new SmartMap\Control\AuthenticationController($app['user.repository'],
                                                         $options['facebook']['appId'],
                                                         $options['facebook']['appSecret']);
});

$app['authorization.controller'] = $app->share(function() use($app) {
    return new SmartMap\Control\AuthorizationController($app['user.repository']);
});

$app['data.controller'] = $app->share(function() use($app) {
    return new SmartMap\Control\DataController($app['user.repository']);
});

$app['profile.controller'] = $app->share(function() use($app) {
    return new SmartMap\Control\ProfileController();
});

$app['event.controller'] = $app->share(function() use($app) {
    return new SmartMap\Control\EventController($app['event.repository']);
});

// Error management
$app->error(function (SmartMap\Control\ControlException $e, $code) use ($app) {
    $app['logging']->addDebug('Deprecated ControlException thrown: ' . $e->__toString());
    return new JsonResponse(array('status' => 'error', 'message' => 'An internal server error occured.', 500,
        array('X-Status-Code' => 200)));
});

$app->error(function (SmartMap\Control\InvalidRequestException $e, $code) use ($app) {
    $app['logging']->addWarning('Invalid request: ' . $e->__toString());
    return new JsonResponse(array('status' => 'error', 'message' => $e->getMessage()), 200,
        array('X-Status-Code' => 200));
});

$app->error(function (SmartMap\Control\ServerFeedbackException $e, $code) use ($app) {
    $app['logging']->addWarning('Invalid request with feedback: ' . $e->__toString());
    return new JsonResponse(array('status' => 'feedback', 'message' => $e->getMessage()), 200,
        array('X-Status-Code' => 200));
});

$app->error(function (SmartMap\Control\ControlLogicException $e, $code) use ($app) {
    $app['logging']->addError($e->__toString());
    if ($app['debug'] == true) {
        return;
    }
    return new JsonResponse(array('status' => 'error', 'message' => 'An internal server error occurred.'), 500,
        array('X-Status-Code' => 200));
});

$app->error(function (\Exception $e, $code) use ($app) {
    $app['logging']->addCritical('Unexpected exception: ' . $e->__toString());
    if ($app['debug'] == true) {
        return;
    }
    return new JsonResponse(array('status' => 'error', 'message' => 'An internal error occurred'), 500,
        array('X-Status-Code' => 200));
});


// Routing
$app->post('/auth', 'authentication.controller:authenticate');

$app->post('/registerUser', 'authentication.controller:registerUser');

$app->post('/allowFriend', 'authorization.controller:allowFriend');

$app->post('/disallowFriend', 'authorization.controller:disallowFriend');

$app->post('/allowFriendList', 'authorization.controller:allowFriendList');

$app->post('/disallowFriendList', 'authorization.controller:disallowFriendList');

$app->post('/followFriend', 'authorization.controller:followFriend');

$app->post('/unfollowFriend', 'authorization.controller:unfollowFriend');

$app->post('/getUserInfo', 'data.controller:getUserInfo');

$app->post('/getProfilePicture', 'profile.controller:getProfilePicture');

$app->post('/inviteFriend', 'data.controller:inviteFriend');

$app->post('/getInvitations', 'data.controller:getInvitations');

$app->post('/acceptInvitation', 'data.controller:acceptInvitation');

$app->post('/declineInvitation', 'data.controller:declineInvitation');

$app->post('/ackAcceptedInvitation', 'data.controller:ackAcceptedInvitation');

$app->post('/ackRemovedFriend', 'data.controller:ackRemovedFriend');

$app->post('/removeFriend', 'data.controller:removeFriend');

$app->post('/listFriendsPos', 'data.controller:listFriendsPos');

$app->post('/updatePos', 'data.controller:updatePos');

$app->post('/findUsers', 'data.controller:findUsers');

$app->post('/getFriendsIds', 'data.controller:getFriendsIds');

$app->post('/createEvent', 'event.controller:createEvent');

$app->post('/updateEvent', 'event.controller:updateEvent');

$app->post('/getPublicEvents', 'event.controller:getPublicEvents');

if ($app['debug'] == true)
{
    $app->post('/fakeAuth', 'authentication.controller:fakeAuth');
}

// Logging of requests
$app->before(function(Request $request, Application $app) {
    $app['logging']->addInfo('New request: ' . $request->getRequestUri() .
        ' from ip ' . $request->getClientIp() . '.');
});

$app->run();
