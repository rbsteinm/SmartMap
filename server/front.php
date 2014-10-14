<?php

require 'vendor/autoload.php';

use SmartMap\Routing\Router;

$router = new Router($_SERVER['REQUEST_URI'], $_SESSION, $_POST);

echo $router->route();

echo 'Done';

?>
