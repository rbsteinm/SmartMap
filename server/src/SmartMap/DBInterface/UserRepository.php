<?php

namespace SmartMap\DBInterface;

use Doctrine\DBAL\Connection;

class UserRepository
{
    private static $TABLE_USER = 'users';
    private static $TABLE_FRIENDSHIP = 'friendships';
    private static $TABLE_INVITATIONS = 'invitations';
    
    private $mApp;
    
    /* Constructs a UserRepository with a Doctrine\DBAL\Connection object.
     */
    function __construct(Connection $db)
    {
        $this->mDb = $db;
    }
    
    /* Gets a user from the database, given it's id.
     */
    public function getUser($id)
    {
        $req = "SELECT * FROM " . self::$TABLE_USER . " WHERE idusers = ?";
        $userData = $this->mDb->fetchAssoc($req, array((int) $id));
        
        if (!$userData)
        {
            throw new \Exception('No user found with id ' . $id);
        }
        
        $user = new User(
                            $userData['idusers'], 
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
    public function getUsers($ids, $visibility = array('VISIBLE', 'INVISIBLE'))
    {
        if (!is_array($ids) OR !is_array($visibility))
        {
            throw new \InvalidArgumentException('$ids and $visibility must be arrays');
        }
        
        // If $ids is empty, we will find no user
        if (count($ids) == 0)
        {
            return array();
        }
        
        $req = "SELECT * FROM " . self::$TABLE_USER . " WHERE idusers IN (?) AND visibility in (?)";
        $stmt = $this->mDb->executeQuery($req, array($ids, $visibility),
                                         array(\Doctrine\DBAL\Connection::PARAM_INT_ARRAY,
                                               \Doctrine\DBAL\Connection::PARAM_STR_ARRAY));
        
        $users = array();
        
        while ($userData = $stmt->fetch())
        {
            $users[] = new User(
                                    $userData['idusers'], 
                                    $userData['hash'],
                                    $userData['name'],
                                    $userData['visibility'],
                                    $userData['longitude'],
                                    $userData['latitude']
                                );
        }
        
        return $users;
    }
    
    /* Adds a new user in the database. The id value from the parameter
     * is not used. Returns the created user with it's id properly set.
     */
    public function createUser(User $user)
    {
        $this->mDb->insert(self::$TABLE_USER,
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
        $this->mDb->update(self::$TABLE_USER, 
            array(
                'name' => $user->getName(),
                'visibility' => $user->getVisibility(),
                'longitude' => $user->getLongitude(),
                'latitude' => $user->getLatitude()
            ), array('idusers' => $user->getId()));
    }
    
    /* Gets the ids of the friends of the user with id $userId,
     * where the status of their friendhsip is in the array $status
     * (can be 'ALLOWED', 'DISALLOWED' or 'BLOCKED'),
     * and the following stauts in the array $follow (can be
     * 'FOLLOWED' or 'UNFOLLOWED').
     */
    public function getFriendsIds($userId,
                                  $status = array('ALLOWED', 'DISALLOWED'),
                                  $follow = array('FOLLOWED', 'UNFOLLOWED'))
    {
        if (!is_array($status) OR !is_array($follow))
        {
            throw new \InvalidArgumentException('Parameters $status and $follow must be arrays.');
        }
        
        $req = "SELECT id2 FROM " . self::$TABLE_FRIENDSHIP . " WHERE id1 = ? AND ".
        "status IN (?) AND follow IN (?)";
        $stmt = $this->mDb->executeQuery($req,
                                         array((int) $userId, $status, $follow),
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
    
    /* Add a friendship link between two users, with status set to ALLOWED
     * and follow to FOLLOWED.
     */
    public function addFriendshipLink($idUser, $idFriend)
    {
        $this->mDb->insert(self::$TABLE_FRIENDSHIP,
                           array(
                            'id1' => (int) $idUser,
                            'id2' => (int) $idFriend,
                            'status' => 'ALLOWED',
                            'follow' => 'FOLLOWED'
                          ));
    }
    
    /* Sets the status of a friendship link.
     */
    public function setFriendshipStatus($idUser, $idFriend, $status)
    {
        if (!in_array($status, array('ALLOWED', 'DISALLOWED', 'BLOCKED')))
        {
            throw new \Exception('Invalid value for status');
        }
        
        $this->mDb->update(self::$TABLE_FRIENDSHIP,
                           array('status' => $status),
                           array('id1' => (int) $idFriend, 'id2' => (int) $idUser)
                          );
    }
    
    /* Sets the status of a list of friendship links.
     */
    public function setFriendshipsStatus($idUser, $idsFriends, $status)
    {
        if (!in_array($status, array('ALLOWED', 'DISALLOWED', 'BLOCKED')))
        {
            throw new \Exception('Invalid value for status');
        }
        
        if (!is_array($idsFriends))
        {
            throw new \Exception('Parameter $idsFriends must be an array !');
        }
        
        $req = "UPDATE " . self::$TABLE_FRIENDSHIP .
               " SET status = ? WHERE id1 IN (?) AND id2 = ?";
        $stmt = $this->executeQuery($req,
                                    array($status, $idFriends, $idUser),
                                    array(\PDO::PARAM_STR,
                                          \Doctrine\DBAL\Connection::PARAM_INT_ARRAY,
                                          \PDO::PARAM_INT,));
    }
    
    /* Sets the follow status of a friendship link.
     */
    public function setFriendshipFollow($idUser, $friendId, $follow)
    {
        if (!in_array($follow, array('FOLLOWED', 'UNFOLLOWED')))
        {
            throw new \Exception('Invalid value for follow status');
        }
        
        $this->mDb->update(self::$TABLE_FRIENDSHIP,
                           array('follow' => $follow),
                           array('id1' => (int) $idUser, 'id2' => (int) $idFriend)
                          );
    }
    
    /* Gets a list of the ids of users who sent a friend invitation to 
     * the user with id $userId.
     */
    public function getInvitationIds($userId)
    {
        $req = "SELECT id1 FROM " . self::$TABLE_INVITATIONS .  " WHERE id2 = ?";
        $stmt = $this->mDb->executeQuery($req, array((int) $userId));
        
        $ids = array();
        
        while ($id = $stmt->fetch())
        {
            $ids[] = $id['id1'];
        }
        
        return $ids;
    }
    
    /* Adds an nvitation from the user with id $idUser to the user with
     * id $idFriend.
     */
    public function addInvitation($idUser, $idFriend)
    {
        $this->mDb->insert(self::$TABLE_INVITATIONS,
                          array(
                            'id1' => (int) $idUser,
                            'id2' => (int) $idFriend
                         ));
    }
    
    /* Removes the invitation from the user with id $idUser to the user
     * with id $friendId.
     */
    public function removeInvitation($idUser, $idFriend)
    {
        $this->mDb->delete(self::$TABLE_INVITATIONS, array(
                                'id1' => (int) $idUser,
                                'id2' => (int) $idFriend
                           ));
    }
}
