<?php

namespace SmartMap\DBInterface;

use Symfony\Component\HttpFoundation\Request;

use SmartMap\Control\InvalidRequestException;

/**
 * Models a user.
 *
 * @author Pamoi
 *
 * @author SpicyCH (code reviewed - 03.11.2014) : added javadoc, need to unit test
 */
class User
{
    public static $DATE_FORMAT = 'Y-m-d H:i:s';
    
    private $mId;
    private $mFbId;
    private $mName;
    private $mVisibility;
    private $mLongitude;
    private $mLatitude;
    private $mLastUpdate;
    
    /**
     * Constructor
     * @param int $id
     * @param int $fbId
     * @param String $name
     * @param string $visibility
     * @param double $longitude
     * @param double $latitude
     * @param string $lastUpdate A string representation of a date in format $DATE_FORMAT
     */
    function __construct(
        $id,
        $fbId,
        $name,
        $visibility = "VISIBLE",
        $longitude = 0.0,
        $latitude = 0.0,
        $lastUpdate = null)
    {
        // Checking for validity of attributes
        $this->checkId($id);
        $this->checkId($fbId);
        $this->checkName($name);
        $this->checkVisibility($visibility);
        $this->checkLongitude($longitude);
        $this->checkLatitude($latitude);
        if ($lastUpdate == null)
        {
            $lastUpdate = date(self::$DATE_FORMAT);
        }
        
        $this->mId = $id;
        $this->mFbId = $fbId;
        $this->mName = $name;
        $this->mVisibility = $visibility;
        $this->mLongitude = $longitude;
        $this->mLatitude = $latitude;
        $this->mLastUpdate = $this->checkLastUpdate($lastUpdate);
    }
    
    /**
     * Get the user's id.
     * @return a Long representing the user id
     */
    public function getId()
    {
        return $this->mId;
    }
    
    /**
     * Set the user's id.
     * @param Long $id
     * @return \SmartMap\DBInterface\User
     */
    public function setId($id)
    {
        $this->checkId($id);
        
        $this->mId = $id;
        
        return $this;
    }
    
    /**
     * Get the user's facebook id.
     * @return the facebook id associated to this user
     */
    public function getFbId()
    {
        return $this->mFbId;
    }
    
    /**
     * Set the user's facebook id.
     * @param Long $fbId the facebook id to associate to the user
     * @return \SmartMap\DBInterface\User
     */
    public function setFbId($fbId)
    {
    	$this->checkId($fbId);
    	
        $this->mFbId = $fbId;
        
        return $this;
    }
    
    /**
     * Get the user's name.
     * @return a String for the user's name
     */
    public function getName()
    {
        return $this->mName;
    }
    
    /**
     * Set the user's name.
     * @param String $name
     * @return \SmartMap\DBInterface\User
     */
    public function setName($name)
    {
    	$this->checkName($name);
    	
        $this->mName = $name;
        
        return $this;
    }
    
    /**
     * Get the user's visibility.
     * @return the visibility: VISIBLE or INVISIBLE
     */
    public function getVisibility()
    {
        return $this->mVisibility;
    }
    
    /**
     * Set the user's visibility.
     * @param enum $visibility the visibility, VISIBLE or INVISIBLE
     * @return \SmartMap\DBInterface\User
     */
    public function setVisibility($visibility)
    {
        $this->checkVisibility($visibility);
        
        $this->mVisibility = $visibility;
        
        return $this;
    }
    
    /**
     * Get the user's longitude coordinate.
     * @return the longitude
     */
    public function getLongitude()
    {
        return $this->mLongitude;
    }
    
    /**
     * Set the user's longitude coordinate.
     * @param double $longitude the longitude to set
     * @return \SmartMap\DBInterface\User
     */
    public function setLongitude($longitude)
    {
    	$this->checkLongitude($longitude);
    	
        $this->mLongitude = $longitude;
        
        return $this;
    }
    
    /**
     * Get the user's latitude coordinate.
     * @return the latitude
     */
    public function getLatitude()
    {
        return $this->mLatitude;
    }
    
    /**
     * Set the user's latitude coordinate.
     * @param unknown $latitude the latitude
     * @return \SmartMap\DBInterface\User
     */
    public function setLatitude($latitude)
    {
    	$this->checkLatitude($latitude);
    	
        $this->mLatitude = $latitude;
        
        return $this;
    }
    
    public function getLastUpdate()
    {
        return $this->mLastUpdate;
    }
    
    /** Checks the validity of an id parameter.
     * @param Long $id
     * @throws \InvalidArgumentException if the id is below 1
     */
    private function checkId($id)
    {
        if ($id <= 0)
        {
            throw new \InvalidArgumentException('Id must be greater than 0.');
        }
    }
    
   /** Checks the validity of a name parameter
    * @param string $name
    * @throws \InvalidArgumentException
    */
    private function checkName($name)
    {
    	$length = strlen($name);
    	if ($length < 2 OR $length > 60)
    	{
    		throw new \InvalidArgumentException('Name must be between 2 and 60 characters.');
    	}
    }
    
    /** Checks the validity of a visibility parameter.
     * @param string $visibility
     * @throws \InvalidArgumentException if the visibility is not VISIBLE or INVISIBLE
     */
    private function checkVisibility($visibility)
    {
        if (!($visibility == 'VISIBLE' OR $visibility == 'INVISIBLE'))
        {
            throw new \InvalidArgumentException('Visibility must be VISIBLE or INVISIBLE.');
        }
    }
    
    /** Checks the validity of a longitude parameter.
     * @param double $longitude
     * @throws \InvalidArgumentException
     */
    private function checkLongitude($longitude)
    {
    	if ($longitude < -180.0 OR $longitude > 180.0)
    	{
    		throw new \InvalidArgumentException('Longitude must be between -180 and 180.');
    	}
    }
    
    /** Checks the validity of a latitude parameter.
     * @param double $latitude
     * @throws \InvalidArgumentException
     */
    private function checkLatitude($latitude)
    {
        if ($latitude < -90.0 OR $latitude > 90.0)
        {
            throw new \InvalidArgumentException('Latitude must be between -90 and 90.');
        }
    }
    
    private function checkLastUpdate($date)
    {
        try
        {
            $dt = new \DateTime($date);
        }
        catch (\Exception $e)
        {
            throw new \InvalidArgumentException('Last update date must be in format ' . self::$DATE_FORMAT . '.');
        }
        if ($dt->getTimestamp() > time())
        {
            throw new \InvalidArgumentException('Last update date must not be in the future.');
        }
        // If we could create a date from the string, we return it in the right format.
        return $dt->format(self::$DATE_FORMAT);
    }
}
