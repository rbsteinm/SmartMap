<?php session_start();

require 'vendor/autoload.php';

use SmartMap\Routing\Router;
use SmartMap\Routing\DefaultRouter;

use SmartMap\Control\Context;
use SmartMap\Control\PHPContext;

$context = new PHPContext($_POST, $_SESSION);

$controller = null; // TODO

$router = new DefaultRouter($context, $controller);

echo $router->getResponse($_SERVER['REQUEST_URI']);
