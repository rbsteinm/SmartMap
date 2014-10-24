<?php

namespace SmartMap\DBInterface;

use Symfony\Component\HttpFoundation\Request;

class User
{
    private $mId;
    private $mFbId;
    private $mName;
    private $mVisibility;
    private $mLongitude;
    private $mLatitude;
    
    function __construct($id, $fbId, $name, $visibility, $longitude, $latitude)
    {
        // Checking for validity
        $this->checkId($id);
        $this->checkId($fbId);
        $this->checkVisibility($visibility);
        
        $this->mId = $id;
        $this->mHash = $fbId;
        $this->mName = $name;
        $this->mVisibility = $visibility;
        $this->mLongitude = $longitude;
        $this->mLatitude = $latitude;
    }
    
    /* Gets the current user id from the server request. Throws an exception
     * if the session parameter userId is not set.
     */
    public static function getIdFromRequest(Request $request)
    {
        if (!$request->hasSession())
        {
            throw new ControlException('Trying to access session but the session is not started');
        }
        
        $session = $request->getSession();
        
        $id = $session->get('userId');
        
        if ($id == null)
        {
            throw new ControlException('The user is not authenticated.');
        }
        
        return $id;
    }
    
    /* Get the user's id
     */
    public function getId()
    {
        return $this->mId;
    }
    
    /* Set the user's id
     */
    public function setId($id)
    {
        $this->checkId($id);
        
        $this->mId = $id;
        
        return $this;
    }
    
    /* Get the user's facebook id
     */
    public function getFbId()
    {
        return $this->mFbId;
    }
    
    /* Set the user's facebook id
     */
    public function setFbId($fbId)
    {
        $this->mFbId = $fbId;
        
        return $this;
    }
    
    /* Get the user's name
     */
    public function getName()
    {
        return $this->mName;
    }
    
    /* Set the user's name
     */
    public function setName($name)
    {
        $this->mName = $name;
        
        return $this;
    }
    
    /* Get the user's visibility
     */
    public function getVisibility()
    {
        return $this->mVisibility;
    }
    
    /* Set the user's visibility
     */
    public function setVisibility($visibility)
    {
        $this->checkVisibility($visibility);
        
        $this->mVisibility = $visibility;
        
        return $this;
    }
    
    /* Get the user's longitude coordinate
     */
    public function getLongitude()
    {
        return $this->mLongitude;
    }
    
    /* Set the user's longitude coordinate
     */
    public function setLongitude($longitude)
    {
        $this->mLongitude = $longitude;
        
        return $this;
    }
    
    /* Get the user's latitude coordinate
     */
    public function getLatitude()
    {
        return $this->mLatitude;
    }
    
    /* Set the user's latitude coordinate
     */
    public function setLatitude($latitude)
    {
        $this->mLatitude = $latitude;
        
        return $this;
    }
    
    private function checkId($id)
    {
        if ($id <= 0)
        {
            throw new \InvalidArgumentException('id must be greater than 0');
        }
    }
    
    private function checkVisibility($visibility)
    {
        if (!($visibility == 'VISIBLE' OR $visibility == 'INVISIBLE'))
        {
            throw new \InvalidArgumentException('Visibility must be VISIBLE or INVISIBLE.');
        }
    }
}
