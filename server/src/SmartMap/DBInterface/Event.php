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
        $this->mstartingDate = $this->checkDate($starting);
        $this->mEndingDate = $this->checkDate($ending);
        $this->mLongitude = $longitude;
        $this->mLatitude = $latitude;
        $this->mPositionName = $positionName;
        $this->mName = $name;
        $this->description = $description;
        
        
    }
    
    private function checkId($id)
    {
        if ($id <= 0)
        {
            throw new \InvalidArgumentException('Id must be greater than 0.');
        }
    }
    
    private function checkName($name)
    {
        $len = strlen($name);
        if ($len < 2 OR $len > 140)
        {
            throw new \InvalidArgumentException('Event name must be between 2 and 140 characters.');
        }
    }
    
    private function checkPositionName($name)
    {
        $len = strlen($name);
        if ($len < 2 OR $len > 60)
        {
            throw new \InvalidArgumentException('Position name must be between 2 and 60 characters.');
        }
    }
    
    private function checkDescription($desc)
    {
        $len = strlen($desc);
        if ($len > 255)
        {
            throw new \InvalidArgumentException('Position name must not be greater than 255 characters.');
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
     * @param unknown $date
     * @throws \InvalidArgumentException
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
        return $dt->format(self::$DATE_FORMAT);
    }
}
