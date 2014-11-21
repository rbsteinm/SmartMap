<?php

namespace SmartMap\Control;

/**
 * -- deprecated
 *
 * A ControlException is thrown when an error occurs between the server and the client (bad parameters,
 * missing parameters and so on).
 *
 * @author Pamoi
 *
 * @author SpicyCH (code reviewed - 02.11.2014)
 */
class ControlException extends \Exception
{
    function __construct($message)
    {
        parent::__construct($message);
    }
}
