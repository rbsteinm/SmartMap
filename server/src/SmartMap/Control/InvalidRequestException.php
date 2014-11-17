<?php

namespace SmartMap\Control;

/**
 * An InvalidRequestException indicates that the request submitted to the server was invalid or not
 * complete enough for the server to do it's task. It's message is meant to be sent back to the client
 * as an error message.
 *
 * @author Pamoi
 */
class InvalidRequestException extends \Exception
{
    function __construct($message = "", $code = 0, \Exception $previous = null)
    {
        parent::__construct($message, $code, $previous);
    }
}
