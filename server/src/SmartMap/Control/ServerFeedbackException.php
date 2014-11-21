<?php

namespace SmartMap\Control;


/**
 * A ServerFeedbackException is used to give the client feedback that is displayable to the user,
 * but that should ideally never happen (that's why it is an exception).
 *
 * @author Pamoi
 */
class ServerFeedbackException extends \Exception
{
    function __construct($message = "", $code = 0, \Exception $previous = null)
    {
        parent::__construct($message, $code, $previous);
    }
}