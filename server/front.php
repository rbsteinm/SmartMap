<?php session_start();

require 'vendor/autoload.php';

use SmartMap\Routing\Router;
use SmartMap\Routing\DefaultRouter;

use SmartMap\Control\Context;
use SmartMap\Control\PHPContext;
use SmartMap\Control\Handler;

$context = new PHPContext($_POST, $_SESSION);

$handler = null; // TODO

$router = new DefaultRouter($context, $handler);

echo $router->getResponse($_SERVER['REQUEST_URI']);
