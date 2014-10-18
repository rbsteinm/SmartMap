<?php session_start();

require 'vendor/autoload.php';

use SmartMap\Routing\Router;
use SmartMap\Routing\DefaultRouter;

use SmartMap\Control\Context;
use SmartMap\Control\PHPContext;
use SmartMap\Control\Handler;
use SmartMap\Control\DatabaseHandler;

header('Content-type: application/json');

$options = array();

$context = new PHPContext($options);

$handler = new DatabaseHandler($context);

$router = new DefaultRouter($handler);

echo $router->getResponse($_SERVER['REQUEST_URI']);
