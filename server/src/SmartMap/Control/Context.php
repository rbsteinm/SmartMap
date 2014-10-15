<?php

namespace SmartMap\Control;

/*
 * @author Pamoi
 * 
 * This interface describes an abstraction over PHP global variables.
 * It gives acces to session and post variables.
 */
interface Context
{
    /* Returns true if the current session is authenticated,
     * false otherwise.
     */
    public function isAuthenticated();
    
    /* Gives the id of the current user.
     * 
     * @throws ContextException if the user is not authenticated
     */
    public function getUserId();
    
    /* Gets the post parameter for a given name. 
     * 
     * @return The value of the post parameter
     * 
     * @throws ContextException if the field is not set.
     */
    public function getPost($fieldName);
}
