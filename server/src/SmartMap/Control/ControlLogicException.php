<?php

namespace SmartMap\Control;

/**
 * A ControlLogicException indicates an error caused by an invalid behaviour of the server code.
 * The exception message provides debugging information.
 *
 * @author Pamoi
 */
class ControlLogicException extends \Exception
{
    function __construct($message = "", $code = 0, \Exception $previous = null)
    {
        parent::__construct($message, $code, $previous);
    }
}
