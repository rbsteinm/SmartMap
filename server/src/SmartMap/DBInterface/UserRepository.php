<?php

namespace SmartMap\DBInterface;

use Doctrine\DBAL\Connection;

use SmartMap\Control\ControlException;

/**
 * Models the user repo.
 *
 * @author Pamoi
 *
 * @author SpicyCH (code reviewed - 03.11.2014) : added javadoc, need to unit test, corrected a typo:
 * it was getLatitude, it is now getLongitude.
 */
class UserRepository
{
    private static $TABLE_USER = 'users';
    private static $TABLE_FRIENDSHIP = 'friendships';
    private static $TABLE_INVITATIONS = 'invitations';
    
    private $mApp;
    
    /**
     * Constructs a UserRepository with a Doctrine\DBAL\Connection object.
     * @param Connection $db
     */
    function __construct(Connection $db)
    {
        $this->mDb = $db;
    }
    
    // Authentication
    
    /**
     * Gets a user id given it's facebook id, or returns false if such user
     * does not exist.
     * @param Long $fbId
     * @return false if we couldn't fetch the user's data and the user id otherwise.
     */
    public function getUserIdFromFb($fbId)
    {
        $req = "SELECT idusers FROM " . self::$TABLE_USER . " WHERE fbid = ?";
        $userData = $this->mDb->fetchAssoc($req, array((int) $fbId));
        
        if (!$userData)
        {
            return false;
        }
        else
        {
            return $userData['idusers'];
        }
    }
    
    // Users management
    
    /**
     * Gets a user from the database, given it's id.
     * @param Long $id
     * @throws \Exception when the user is not found
     * @return \SmartMap\DBInterface\User
     */
    public function getUser($id)
    {
        $req = "SELECT * FROM " . self::$TABLE_USER . " WHERE idusers = ?";
        $userData = $this->mDb->fetchAssoc($req, array((int) $id));
        
        if (!$userData)
        {
            throw new \Exception('No user found with id ' . $id . '.');
        }
        
        $user = new User(
                            $userData['idusers'], 
                            $userData['fbid'],
                            $userData['name'],
                            $userData['visibility'],
                            $userData['longitude'],
                            $userData['latitude']
                        );
                        
        return $user;
    }
    
    /**
     * Gets a list of users, given a list of ids.
     * @param array $ids
     * @param array $visibility
     * @throws \InvalidArgumentException if $ids or $visibility is not of the right type
     * @return multitype:|multitype:\SmartMap\DBInterface\User
     */
    public function getUsers($ids, $visibility = array('VISIBLE', 'INVISIBLE'))
    {
        if (!is_array($ids) OR !is_array($visibility))
        {
            throw new \InvalidArgumentException('Arguments $ids and $visibility must be arrays.');
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
        
        return $this->userArrayFromStmt($stmt);
    }
    
    /**
     * Gets a list of users whose name is starting by $partialName
     * @param String $partialName
     * @return multitype:\SmartMap\DBInterface\User
     */
    public function findUsersByPartialName($partialName)
    {
        $length = strlen($partialName);
        
        $req = "SELECT * FROM " . self::$TABLE_USER . " WHERE SUBSTR(LOWER(name), 1, ?) = ? LIMIT 10";
        $stmt = $this->mDb->executeQuery($req,
                                         array($length, strtolower($partialName)),
                                         array(\PDO::PARAM_INT, \PDO::PARAM_STR));
        
        return $this->userArrayFromStmt($stmt);
    }
    
    /**
     * Adds a new user in the database. The id value from the parameter
     * is not used. Returns the created user with it's id properly set.
     * @param User $user
     * @return User
     */
    public function createUser(User $user)
    {
        $this->mDb->insert(self::$TABLE_USER,
            array(
                'fbid' => $user->getFbid(),
                'name' => $user->getName(),
                'visibility' => $user->getVisibility(),
                'longitude' => $user->getLongitude(),
                'latitude' => $user->getLatitude()
            ));
        
        $user->setId($this->mDb->fetchColumn('SELECT LAST_INSERT_ID()', array(), 0));
        
        return $user;
    }
    
    /**
     * Updates an existing user in the database. Modifiable entries are
     * name, visibility, longitude and latitude.
     * @param User $user
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
    
    // Friendship management
    
    /**
     * Gets the ids of the friends of the user with id $userId,
     * where the status of their friendhsip is in the array $status
     * (can be 'ALLOWED', 'DISALLOWED' or 'BLOCKED'),
     * and the following stauts in the array $follow (can be
     * 'FOLLOWED' or 'UNFOLLOWED').
     * @param Long $userId
     * @param array $status
     * @param array $follow
     * @throws \InvalidArgumentException
     * @return multitype:unknown
     */
    public function getFriendsIds($userId,
                                  $status = array('ALLOWED', 'DISALLOWED'),
                                  $follow = array('FOLLOWED', 'UNFOLLOWED'))
    {
        if (!is_array($status) OR !is_array($follow))
        {
            throw new \InvalidArgumentException('Arguments $status and $follow must be arrays.');
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
    
    /**
     * Add a friendship link between two users, with status set to ALLOWED
     * and follow to FOLLOWED.
     * @param Long $idUser
     * @param Long $idFriend
     * @throws \Exception if the friendship link already exists.
     */
    public function addFriendshipLink($idUser, $idFriend)
    {
        // We first check that there is not already a friendship link.
        $req = "SELECT * FROM " . self::$TABLE_FRIENDSHIP . " WHERE id1 = ? AND id2 = ?";
        $data = $this->mDb->fetchAssoc($req, array((int) $idUser, (int) $idFriend));
        
        if ($data)
        {
            throw new \Exception('This friendship link already exists !');
        }
        
        $this->mDb->insert(self::$TABLE_FRIENDSHIP,
                           array(
                            'id1' => (int) $idUser,
                            'id2' => (int) $idFriend,
                            'status' => 'ALLOWED',
                            'follow' => 'FOLLOWED'
                          ));
    }
    
    /**
     * Sets the status of a friendship link.
     * @param unknown $idUser
     * @param unknown $idFriend
     * @param unknown $status
     * @throws \Exception when the status is invalid, i.e. not ALLOWED, DISALLOWED or BLOCKED
     */
    public function setFriendshipStatus($idUser, $idFriend, $status)
    {
        if (!in_array($status, array('ALLOWED', 'DISALLOWED', 'BLOCKED')))
        {
            throw new \Exception('Invalid value for status !');
        }
        
        $this->mDb->update(self::$TABLE_FRIENDSHIP,
                           array('status' => $status),
                           array('id1' => (int) $idFriend, 'id2' => (int) $idUser)
                          );
    }
    
    /**
     * Sets the status of a list of friendship links.
     * @param Long $idUser
     * @param array $idsFriends
     * @param array $status
     * @throws \Exception when either $status or $idsFreinds is invalid.
     */
    public function setFriendshipsStatus($idUser, $idsFriends, $status)
    {
        if (!in_array($status, array('ALLOWED', 'DISALLOWED', 'BLOCKED')))
        {
            throw new \Exception('Invalid value for status !');
        }
        
        if (!is_array($idsFriends))
        {
            throw new \Exception('Argument $idsFriends must be an array !');
        }
        
        $req = "UPDATE " . self::$TABLE_FRIENDSHIP .
               " SET status = ? WHERE id1 IN (?) AND id2 = ?";
        $stmt = $this->mDb->executeQuery($req,
                                    array($status, $idsFriends, $idUser),
                                    array(\PDO::PARAM_STR,
                                          \Doctrine\DBAL\Connection::PARAM_INT_ARRAY,
                                          \PDO::PARAM_INT,));
    }
    
    /**
     * Sets the follow status of a friendship link.
     * @param unknown $idUser
     * @param unknown $friendId
     * @param unknown $follow
     * @throws \Exception when $follow is invalid.
     */
    public function setFriendshipFollow($idUser, $friendId, $follow)
    {
        if (!in_array($follow, array('FOLLOWED', 'UNFOLLOWED')))
        {
            throw new \Exception('Invalid value for follow status !');
        }
        
        $this->mDb->update(self::$TABLE_FRIENDSHIP,
                           array('follow' => $follow),
                           array('id1' => (int) $idUser, 'id2' => (int) $friendId)
                          );
    }
    
    // Invitations management
    
    /**
     * Gets a list of the ids of users who sent a friend invitation to 
     * the user with id $userId.
     * @param Long $userId
     * @return an array of user ids who sent an invitation to the user $userId
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
    
    /**
     * Adds an nvitation from the user with id $idUser to the user with
     * id $idFriend.
     * @param Long $idUser
     * @param Long $idFriend
     */
    public function addInvitation($idUser, $idFriend)
    {
        $this->mDb->insert(self::$TABLE_INVITATIONS,
                          array(
                            'id1' => (int) $idUser,
                            'id2' => (int) $idFriend
                         ));
    }
    
    /**
     * Removes the invitation from the user with id $idUser to the user
     * with id $friendId.
     * @param Long $idUser
     * @param Long $idFriend
     */
    public function removeInvitation($idUser, $idFriend)
    {
        $this->mDb->delete(self::$TABLE_INVITATIONS, array(
                                'id1' => (int) $idUser,
                                'id2' => (int) $idFriend
                           ));
    }
    
    // Utility functions
    private function userArrayFromStmt($stmt)
    {
        $users = array();
        
        while ($userData = $stmt->fetch())
        {
            $users[] = new User(
                                    $userData['idusers'], 
                                    $userData['fbid'],
                                    $userData['name'],
                                    $userData['visibility'],
                                    $userData['longitude'],
                                    $userData['latitude']
                                );
        }
        
        return $users;
    }
}
