<?php

require_once __DIR__.'/../vendor/autoload.php';

use Symfony\Component\HttpFoundation\JsonResponse;

$app = new Silex\Application();

// Options
$app['debug'] = true;


// Database connection
$app->register(new Silex\Provider\DoctrineServiceProvider(), array(
    'db.options' => array(
        'driver' => 'pdo_mysql',
        'host' => 'localhost',
        'dbname' => 'SmartMapDataBase',
        'user' => 'smartmap',
        'password' => 'salut23',
        'charset' => 'utf8'
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

$app['authentication.controller'] = $app->share(function() use($app) {
    return new SmartMap\Control\AuthenticationController($app['user.repository']);
});

$app['authorization.controller'] = $app->share(function() use($app) {
    return new SmartMap\Control\AuthorizationController($app['user.repository']);
});

$app['data.controller'] = $app->share(function() use($app) {
    return new SmartMap\Control\DataController($app['user.repository']);
});

// Error management
$app->error(function (SmartMap\Control\ControlException $e, $code) use ($app) {
    return new JsonResponse(array('status' => 'error', 'message' => $e->getMessage()));
});

$app->error(function (\Exception $e, $code) use ($app) {
    if ($app['debug'] == true) {
        return;
    }
    return new JsonResponse(array('status' => 'error', 'message' => 'An internal error occured'));
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

$app->post('/listFriendsPos', 'data.controller:listFriendsPos');

$app->post('/updatePos', 'data.controller:updatePos');

// Testing
$app->post('/fakeAuth', 'authentication.controller:fakeAuth');


$app->run();
