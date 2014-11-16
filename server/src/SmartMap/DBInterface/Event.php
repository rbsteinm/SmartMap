<?php

namespace SmartMap\DBInterface;

class Event
{
    private $mId;
    
    private $mCreatorId;
    
    private $startingDate;
    private $endingDate;
    
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
        
    }
}
