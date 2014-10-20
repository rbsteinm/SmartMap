<?php

namespace SmartMap\Routing;

/*
 * @author Pamoi
 * 
 * This interface describes the Router component which has to provide
 * a response given a URI.
 */
interface Router
{
    /* Get a response for a given URI
     * 
     * @param $uri The URI for which we want a response.
     *
     * @return The response of the server as a String.
     */
    public function getResponse($uri);
}
