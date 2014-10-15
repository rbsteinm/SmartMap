<?php session_start();

require 'vendor/autoload.php';

use SmartMap\Routing\Router;
use SmartMap\Routing\DefaultRouter;

$router = new DefaultRouter($_SESSION, $_POST);

echo $router->getResponse($_SERVER['REQUEST_URI']);
