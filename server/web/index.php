<?php

require_once __DIR__.'/../vendor/autoload.php';

$app = new Silex\Application();

// Options
$app['debug'] = true;

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

$app->get('/auth', 'authentication.controller:authenticate');





$app->run();
