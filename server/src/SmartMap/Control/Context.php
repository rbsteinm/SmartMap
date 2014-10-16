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
     * or the field is not in the list.
     */
    public function getSession($fieldname);
    
    /* Gets the post parameter for a given name. 
     * 
     * @return The value of the post parameter
     * 
     * @throws ContextException if the field is not set.
     */
    public function getPost($fieldName);
    
    /* Gets the value of an option
     * 
     * @return The value of the option
     * 
     * @throws ContextException if the option is not set.
     */
    public function getOption($optionName);
}
