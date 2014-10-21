<?php

namespace SmartMap\DBInterface;

use Doctrine\DBAL\Connection;

class UserRepository
{
    private static $TABLE_NAME = 'users';
    
    private $mApp;
    
    function __construct(Connection $db)
    {
        $this->mDb = $db;
    }
    
    /* Gets a user from the database, given it's id.
     */
    public function getUser($id)
    {
        $req = "SELECT * FROM " . UserRepository::$TABLE_NAME . " WHERE idusers = ?";
        $userData = $this->mDb->fetchAssoc($req, array((int) $id));
        
        $user = new User($userData['idusers'], 
                         $userData['hash'],
                         $userData['name'],
                         $userData['visibility'],
                         $userData['longitude'],
                         $userData['latitude']
                        );
                        
        return $user;
    }
    
    public function getUsers($ids)
    {
        // TODO
    }
    
    /* Adds a new user in the database. The id value from the parameter
     * is not used. Returns the created user with it's id properly set.
     */
    public function createUser($user)
    {
        $this->mDb->insert(UserRepository::$TABLE_NAME,
            array(
                'hash' => $user->getHash(),
                'name' => $user->getName(),
                'visibility' => $user->getVisibility(),
                'longitude' => $user->getVisibility(),
                'latitude' => $user->getLatitude()
            ));
        
        $user->setId($this->mDb->fetchColumn('SELECT LAST_INSERT_ID()', array(), 0));
        
        return $user;
    }
    
    /* Updates an existing user in the database. Modifiable entries are
     * name, visibility, longitude and latitude.
     */
    public function updateUser($user)
    {
        $this->mDb->update(UserRepository::$TABLE_NAME, 
            array(
                'name' => $user->getName(),
                'visibility' => $user->getVisibility(),
                'longitude' => $user->getLongitude(),
                'latitude' => $user->getLatitude()
            ), array('id' => $user->getId()));
    }
}
