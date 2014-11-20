<?php

namespace SmartMap\DBInterface;

class Event
{
    private $mId;
    
    private $mCreatorId;
    
    private $mStartingDate;
    private $mEndingDate;
    
    private $mLongitude;
    private $mLatitude;
    private $mPositionName;
    
    private $mName;
    private $mDescription;
    
    public function __construct(
        $id,
        $creatorId,
        $starting,
        $ending,
        $longitude,
        $latitude,
        $positionName,
        $name,
        $description)
    {
        // Checking parameters
        $this->checkId($id);
        $this->checkId($creatorId);
        $this->checkLongitude($longitude);
        $this->checkLatitude($latitude);
        $this->checkPositionName($positionName);
        $this->checkName($name);
        $this->checkDescription($description);
        
        $this->mId = $id;
        $this->mCreatorId = $creatorId;
        $this->mStartingDate = $this->checkDate($starting);
        $this->mEndingDate = $this->checkDate($ending);
        $this->mLongitude = $longitude;
        $this->mLatitude = $latitude;
        $this->mPositionName = $positionName;
        $this->mName = $name;
        $this->mDescription = $description;
        
        if (strtotime($this->mStartingDate) > strtotime($this->mEndingDate))
        {
            throw new \InvalidArgumentException('Event ending date must be after starting date !');
        }
    }
    
    /**
     * Get the event's id.
     */
    public function getId()
    {
        return $this->mId;
    }
    
    /**
     * Set an event's id.
     * 
     * @param int $id
     * @return \SmartMap\DBInterface\Event
     */
    public function setId($id)
    {
        $this->checkId($id);
        
        $this->mId = $id;
        
        return $this;
    }
    
    /**
     * Get the event's creator id.
     */
    public function getCreatorId()
    {
        return $this->mCreatorId;
    }
    
    /**
     * Set the event's creator id.
     * 
     * @param int $id
     * @return \SmartMap\DBInterface\Event
     */
    public function setCreatorId($id)
    {
        $this->checkId($id);
        
        $this->mCreatorId = $id;
        
        return $this;
    }
    
    /**
     * Get the event's starting date in format User::$DATE_FORMAT
     */
    public function getStartingDate()
    {
        return $this->mStartingDate;
    }
    
    /**
     * Set the event's starting date.
     * 
     * @param string $date
     * @return \SmartMap\DBInterface\Event
     */
    public function setStartingDate($date)
    {
        $this->mStartingDate = $this->checkDate($date);

        if (strtotime($this->mStartingDate) > strtotime($this->mEndingDate))
        {
            throw new \InvalidArgumentException('Event ending date must be after starting date !');
        }
        
        return $this;
    }
    
    /**
     * Get the event's ending date in format User::$DATE_FORMAT
     */
    public function getEndingDate()
    {
        return $this->mEndingDate;
    }
    
    /**
     * Sets the event's ending date.
     * 
     * @param string $date
     * @return \SmartMap\DBInterface\Event
     */
    public function setEndingDate($date)
    {
        $this->mEndingDate = $this->checkDate($date);

        if (strtotime($this->mStartingDate) > strtotime($this->mEndingDate))
        {
            throw new \InvalidArgumentException('Event ending date must be after starting date !');
        }
    
        return $this;
    }
    
    /**
     * Get the event's longitude.
     */
    public function getLongitude()
    {
        return $this->mLongitude;
    }
    
    /**
     * Set the event's longitude.
     * 
     * @param double $longitude
     * @return \SmartMap\DBInterface\Event
     */
    public function setLongitude($longitude)
    {
        $this->checkLongitude($longitude);
        
        $this->mLongitude = $longitude;
        
        return $this;
    }
    
    /**
     * Get the event's latitude.
     */
    public function getLatitude()
    {
        return $this->mLatitude;
    }
    
    /**
     * Set the event's latitude.
     * 
     * @param double $latitude
     * @return \SmartMap\DBInterface\Event
     */
    public function setLatitude($latitude)
    {
        $this->checkLatitude($latitude);
    
        $this->mLatitude = $latitude;
    
        return $this;
    }
    
    /**
     * Get the event's position name.
     */
    public function getPositionName()
    {
        return $this->mPositionName;
    }
    
    /**
     * Set the event's psition name.
     * 
     * @param string $name
     * @return \SmartMap\DBInterface\Event
     */
    public function setPositionName($name)
    {
        $this->checkPositionName($name);
        
        $this->mPositionName = $name;
        
        return $this;
    }
    
    /**
     * Get the event's name.
     */
    public function getName()
    {
        return $this->mName;
    }
    
    /**
     * Set the event's name.
     * 
     * @param string $name
     * @return \SmartMap\DBInterface\Event
     */
    public function setName($name)
    {
        $this->checkName($name);
    
        $this->mName = $name;
    
        return $this;
    }
    
    /**
     * Get the event's description.
     */
    public function getDescription()
    {
        return $this->mDescription;
    }
    
    /**
     * Set the event's description.
     * 
     * @param string $desc
     * @return \SmartMap\DBInterface\Event
     */
    public function setDescription($desc)
    {
        $this->checkDescription($desc);
        
        $this->mDescription = $desc;
        
        return $this;
    }
    
    
    /**
     * Checks the validity of an id.
     * 
     * @param int $id
     * @throws \InvalidArgumentException
     */
    private function checkId($id)
    {
        if ($id <= 0)
        {
            throw new \InvalidArgumentException('Id must be greater than 0.');
        }
    }
    
    /**
     * Checks the validity of an event name.
     * 
     * @param string $name
     * @throws \InvalidArgumentException
     */
    private function checkName($name)
    {
        $len = strlen($name);
        if ($len < 2 OR $len > 60)
        {
            throw new \InvalidArgumentException('Event name must be between 2 and 60 characters.');
        }
    }
    
    /**
     * Checks the validity of an event position name.
     * 
     * @param string $name
     * @throws \InvalidArgumentException
     */
    private function checkPositionName($name)
    {
        $len = strlen($name);
        if ($len < 2 OR $len > 60)
        {
            throw new \InvalidArgumentException('Position name must be between 2 and 60 characters.');
        }
    }
    
    /**
     * Checks the validity of an event description.
     * 
     * @param string $desc
     * @throws \InvalidArgumentException
     */
    private function checkDescription($desc)
    {
        $len = strlen($desc);
        if ($len > 255)
        {
            throw new \InvalidArgumentException('Description must not be longer than 255 characters.');
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
    
    /**
     * Checks the validity of starting and ending dates
     * 
     * @param string $date
     * @throws \InvalidArgumentException
     * @return string
     */
    public function checkDate($date)
    {
        try
        {
            $dt = new \DateTime($date);
        }
        catch (\Exception $e)
        {
            throw new \InvalidArgumentException('Invalid date format.');
        }
        
        // If we could create a date from the string, we return it in the right format.
        return $dt->format(User::$DATE_FORMAT);
    }
}
