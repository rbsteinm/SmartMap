<?php

namespace SmartMap\Control;

class ContextException extends \Exception
{
    function __construct($message)
    {
        parent::__construct($message);
    }
}
