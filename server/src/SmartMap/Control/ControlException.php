<?php

namespace SmartMap\Control;

class ControlException extends \Exception
{
    function __construct($message)
    {
        parent::__construct($message);
    }
}
