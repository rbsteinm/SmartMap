<?php

namespace SmartMap\DBInterface;

class User
{
    private $mId;
    private $mHash;
    private $mName;
    private $mVisibility;
    private $mLongitude;
    private $mLatitude;
    
    function __construct($id, $hash, $name, $visibility, $longitude, $latitude)
    {
        // Checking for validity
        $this->checkId($id);
        $this->checkVisibility($visibility);
        
        $this->mId = $id;
        $this->mHash = $hash;
        $this->mName = $name;
        $this->mVisibility = $visibility;
        $this->mLongitude = $longitude;
        $this->mLatitude = $latitude;
    }
    
    public function getId()
    {
        return $this->mId;
    }
    
    public function setId($id)
    {
        $this->checkId($id);
        
        $this->mId = $id;
        
        return $this;
    }
    
    public function getHash()
    {
        return $this->mHash;
    }
    
    public function getName()
    {
        return $this->mName;
    }
    
    public function setName($name)
    {
        $this->mName = $name;
        
        return $this;
    }
    
    public function getVisibility()
    {
        return $this->mVisibility;
    }
    
    public function setVisibility($visibility)
    {
        $this->checkVisibility($visibility);
        
        $this->mVisibility = $visibility;
        
        return $this;
    }
    
    public function getLongitude()
    {
        return $this->mLongitude;
    }
    
    public function setLongitude($longitude)
    {
        $this->mLongitude = $longitude;
        
        return $this;
    }
    
    public function getLatitude()
    {
        return $this->mLatitude;
    }
    
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
