<?php

namespace SmartMap\DBInterface;

use Doctrine\DBAL\Connection;

class UserRepository
{
    private static $TABLE_USER = 'users';
    private static $TABLE_FRIENDSHIP = 'friendships';
    
    private $mApp;
    
    function __construct(Connection $db)
    {
        $this->mDb = $db;
    }
    
    /* Gets a user from the database, given it's id.
     */
    public function getUser($id)
    {
        $req = "SELECT * FROM " . UserRepository::$TABLE_USER . " WHERE idusers = ?";
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
    
    /* Gets a list of users, given a list of ids.
     */
    public function getUsers($ids)
    {
        $req = "SELECT * FROM " . UserRepository::$TABLE_USER . " WHERE idusers IN (?)";
        $stmt = $this->mDb->executeQuery($req, array($ids),
                                         array(\Doctrine\DBAL\Connection::PARAM_INT_ARRAY));
        
        $users = array();
        
        while ($user = $stmt->fetch())
        {
            $users[] = $user;
        }
        
        return $users;
    }
    
    /* Adds a new user in the database. The id value from the parameter
     * is not used. Returns the created user with it's id properly set.
     */
    public function createUser(User $user)
    {
        $this->mDb->insert(UserRepository::$TABLE_USER,
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
    public function updateUser(User $user)
    {
        $this->mDb->update(UserRepository::$TABLE_USER, 
            array(
                'name' => $user->getName(),
                'visibility' => $user->getVisibility(),
                'longitude' => $user->getLongitude(),
                'latitude' => $user->getLatitude()
            ), array('id' => $user->getId()));
    }
    
    /* Gets the ids of the friends of the user with id $userId,
     * where the status of their friendhsip is in the array $status
     * (can be 'ALLOWED', 'DISALLOWED' or 'BLOCKED'),
     * and the following stauts in the array $follow (can be
     * 'FOLLOWED' or 'UNFOLLOWED').
     */
    public function getFriendsIds($userId, $status, $follow)
    {
        if (!is_array($status) OR !is_array($follow))
        {
            throw new \InvalidArgumentException('Parameters $status and $follow must be arrays.');
        }
        
        $req = "SELECT id2 FROM " . UserRepository::$TABLE_FRIENDSHIP . " WHERE id1 = ? AND ".
        "status IN (?) AND follow IN (?)";
        $stmt = $this->mDb->executeQuery($req, array((int) $userId, $status, $follow),
                                         array(
                                               \PDO::PARAM_INT,
                                               \Doctrine\DBAL\Connection::PARAM_STR_ARRAY, 
                                               \Doctrine\DBAL\Connection::PARAM_STR_ARRAY)
                                        );
        
        $ids = array();
        while ($id = $stmt->fetch())
        {
            $ids[] = $id['id2'];
        }
        
        return $ids;
    }
}
