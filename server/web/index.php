<?php

require_once __DIR__.'/../vendor/autoload.php';

use Symfony\Component\HttpFoundation\JsonResponse;

$app = new Silex\Application();

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

// Injecting controllers
$app->register(new Silex\Provider\ServiceControllerServiceProvider());

$app->register(new Silex\Provider\MonologServiceProvider(), array(
    'monolog.logfile' => __DIR__.'/../'.$options['monolog']['logfile'],
    'monolog.name' => $options['monolog']['name']
));

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

// Error management
$app->error(function (SmartMap\Control\ControlException $e, $code) use ($app) {
    $app['monolog']->addDebug('Deprecated ControlException thrown: ' . $e->__toString());
    return new JsonResponse(array('status' => 'error', 'message' => 'An internal server error occured.', 500,
        array('X-Status-Code' => 200)));
});

$app->error(function (SmartMap\Control\InvalidRequestException $e, $code) use ($app) {
    $app['monolog']->addWarning('Invalid request: ' . $e->__toString());
    return new JsonResponse(array('status' => 'error', 'message' => $e->getMessage()), 200,
        array('X-Status-Code' => 200));
});

$app->error(function (SmartMap\Control\ControlLogicException $e, $code) use ($app) {
    $app['monolog']->addError($e->__toString());
    if ($app['debug'] == true) {
        return;
    }
    return new JsonResponse(array('status' => 'error', 'message' => 'An internal server error occured.'), 500,
        array('X-Status-Code' => 200));
});

$app->error(function (\Exception $e, $code) use ($app) {
    $app['monolog']->addError('Unexpected exception: ' . $e->__toString());
    if ($app['debug'] == true) {
        return;
    }
    return new JsonResponse(array('status' => 'error', 'message' => 'An internal error occured'), 500,
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

$app->post('/inviteFriend', 'data.controller:inviteFriend');

$app->post('/getInvitations', 'data.controller:getInvitations');

$app->post('/acceptInvitation', 'data.controller:acceptInvitation');

$app->post('/declineInvitation', 'data.controller:declineInvitation');

$app->post('/ackAcceptedInvitation', 'data.controller:ackAcceptedInvitation');

$app->post('/removeFriend', 'data.controller:removeFriend');

$app->post('/listFriendsPos', 'data.controller:listFriendsPos');

$app->post('/updatePos', 'data.controller:updatePos');

$app->post('/findUsers', 'data.controller:findUsers');

if ($app['debug'] == true)
{
    $app->post('/fakeAuth', 'authentication.controller:fakeAuth');
}


$app->run();
