<?php

require_once __DIR__.'/../vendor/autoload.php';

$app = new Silex\Application();

// Options
$app['debug'] = true;


// Database connection
$app->register(new Silex\Provider\DoctrineServiceProvider(), array(
    'db.options' => array(
        'driver' => 'pdo_mysql',
        'host' => 'localhost',
        'dbname' => 'smartmap',
        'user' => 'smartmap',
        'password' => 'salut23',
        'charset' => 'utf8'
    )
));


// Injecting controllers
$app->register(new Silex\Provider\ServiceControllerServiceProvider());

$app['authentication.controller'] = $app->share(function() use($app) {
    return new SmartMap\Control\AuthenticationController();
});

$app['authorization.controller'] = $app->share(function() use($app) {
    return new SmartMap\Control\AuthorizationController();
});

$app['data.controller'] = $app->share(function() use($app) {
    return new SmartMap\Control\DataController();
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


$app->run();
