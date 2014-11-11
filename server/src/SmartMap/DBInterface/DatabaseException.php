<?php

namespace SmartMap\DBInterface;

/**
 * A DatabaseException encapsulates library-specific exceptions occuring while accessing
 * the database. It provides useful informations for debugging.
 *
 * @author Pamoi
 */
class DatabaseException extends \Exception
{
    function __construct($message = "", $code = 0, \Exception $previous = null)
    {
        parent::__construct($message, $code, $previous);
    }
}
