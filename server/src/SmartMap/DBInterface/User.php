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
    
    function __construct(int $id, 
                         string $hash, 
                         string $name, 
                         string $visibility,
                         double $longitude,
                         double $latitude,
                        )
    {
        $this->mId = $id;
        $this->mHash = $hash;
        $this->mName = $name;
        $this->mVisibility = $visibility;
        $this->mLongitude = $longitude;
        $this->mLatitude = $latitude;
    }
    
    public function getId()
    {
        return $this->mId();
    }
    
    public function getHash()
    {
        return $this->mHash;
    }
    
    public function getName()
    {
        return $this->mName();
    }
    
    public function setName(string $name)
    {
        $this->mName = $name;
        
        return $this;
    }
    
    public function getVisibility()
    {
        return $this->mVisibility;
    }
    
    public function setVisibility(string $visibility)
    {
        if (!($visiblity == 'visible' OR $visiblity == 'invisible'))
        {
            // Exception or nothing...
        }
        else
        {
            $this->mVisibility = $visibility;
            
            return $this;
        }
    }
    
    public function getLongitude()
    {
        return $this->mLongitude;
    }
    
    public function setLongitude(double $longitude)
    {
        $this->mLongitude = $longitude;
        
        return $this;
    }
    
    public function getLatitude()
    {
        return $this->mLatitude;
    }
    
    public function setLatitude(double $latitude)
    {
        $this->mLatitude = $latitude;
        
        return $this;
    }
}
