<?php

namespace SmartMap\DBInterface;

use Doctrine\DBAL\Connection;

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
    private static $TABLE_ACCEPTED_INVITATIONS = 'accepted_invitations';
    private static $TABLE_REMOVED_FRIENDS = 'removed_friends';
    
    private $mDb;
    
    /**
     * Constructs a UserRepository with a Doctrine\DBAL\Connection object.
     * 
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
     *
     * @param $fbId
     * @return bool
     * @throws DatabaseException
     */
    public function getUserIdFromFb($fbId)
    {
        $req = "SELECT idusers FROM " . self::$TABLE_USER . " WHERE fbid = ?";
        
        try
        {
            $userData = $this->mDb->fetchAssoc($req, array((int) $fbId));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error retrieving user in getUserFromFbId.', 1, $e);
        }
        
        if (!$userData)
        {
            return false;
        }
        else
        {
            return (int) $userData['idusers'];
        }
    }
    
    // Users management
    
    /**
     * Gets a user from the database, given it's id.
     * 
     * @param Long $id
     * @throws DatabaseException when the user is not found
     * @return \SmartMap\DBInterface\User
     */
    public function getUser($id)
    {
        $req = "SELECT * FROM " . self::$TABLE_USER . " WHERE idusers = ?";
        
        try
        {
            $userData = $this->mDb->fetchAssoc($req, array((int) $id));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error retrieving user in getUser.', 1, $e);
        }
        
        if (!$userData)
        {
            throw new DatabaseException('No user found with id ' . $id . ' in method getUser.');
        }
        
        try
        {
            $user = new User(
                (int) $userData['idusers'],
                (int) $userData['fbid'],
                $userData['name'],
                $userData['visibility'],
                (double) $userData['longitude'],
                (double) $userData['latitude'],
                $userData['last_update']
            );
        }
        catch (\InvalidArgumentException $e)
        {
            throw new DatabaseException('User with invalid state in database with id ' . $id . '.');
        }
                        
        return $user;
    }
    
    /**
     * Gets a list of users, given a list of ids.
     * 
     * @param array $ids
     * @param array $visibility
     * @throws DatabaseException if $ids or $visibility is not of the right type
     * @return User
     */
    public function getUsers($ids, $visibility = array('VISIBLE', 'INVISIBLE'))
    {
        if (!is_array($ids) OR !is_array($visibility))
        {
            throw new DatabaseException('Arguments $ids and $visibility must be arrays.');
        }
        
        // If $ids is empty, we will find no user
        if (count($ids) == 0)
        {
            return array();
        }
        
        $req = "SELECT * FROM " . self::$TABLE_USER . " WHERE idusers IN (?) AND visibility in (?)";
        
        try
        {
            $stmt = $this->mDb->executeQuery($req, array($ids, $visibility),
                                             array(\Doctrine\DBAL\Connection::PARAM_INT_ARRAY,
                                                   \Doctrine\DBAL\Connection::PARAM_STR_ARRAY));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error retrieving users in getUsers.', 1, $e);
        }
        
        return $this->userArrayFromStmt($stmt);
    }
    
    /**
     * Gets a list of users whose name is starting by $partialName
     * 
     * @param String $partialName
     * @throws DatabaseException
     * @return User
     */
    public function findUsersByPartialName($partialName, $excludedIds)
    {
        $length = strlen($partialName);
        
        if ($length == 0)
        {
            return array();
        }
        
        if (!is_array($excludedIds))
        {
            throw new DatabaseException('Parameter $excludedIds must be array in findUsersByPartialName.');
        }
        
        try
        {
            // We need to do two cases because mysql doesn't like IN () conditions with nothing in array.
            if (!empty($excludedIds))
            {
                $req = "SELECT * FROM " . self::$TABLE_USER . " WHERE idusers NOT IN (?) AND SUBSTR(LOWER(name), 1, ?)".
                     " = ? LIMIT 10";
                
                $stmt = $this->mDb->executeQuery($req,
                                                 array($excludedIds, $length, strtolower($partialName)),
                                                 array(\Doctrine\DBAL\Connection::PARAM_INT_ARRAY,
                                                     \PDO::PARAM_INT,
                                                     \PDO::PARAM_STR));
            }
            else
            {
                $req = "SELECT * FROM " . self::$TABLE_USER . " WHERE SUBSTR(LOWER(name), 1, ?) = ? LIMIT 10";
                
                $stmt = $this->mDb->executeQuery($req,
                    array($length, strtolower($partialName)),
                    array(\PDO::PARAM_INT, \PDO::PARAM_STR));
            }
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error retrieving users in findUsersByPartialName.', 1, $e);
        }
        
        return $this->userArrayFromStmt($stmt);
    }
    
    /**
     * Adds a new user in the database. The id value from the parameter
     * is not used. Returns the created user with it's id properly set.
     * 
     * @param User $user
     * @throws DatabaseException
     * @return User
     */
    public function createUser(User $user)
    {
        try
        {
            // We do not need to check the validity of parameters as it is done in the User class.
            $this->mDb->insert(self::$TABLE_USER,
                array(
                    'fbid' => $user->getFbid(),
                    'name' => $user->getName(),
                    'visibility' => $user->getVisibility(),
                    'longitude' => $user->getLongitude(),
                    'latitude' => $user->getLatitude(),
                    'last_update' => date(User::$DATE_FORMAT)
                ));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error creating user in createUser.', 1, $e);
        }
        
        $user->setId($this->mDb->fetchColumn('SELECT LAST_INSERT_ID()', array(), 0));
        
        return $user;
    }
    
    /**
     * Updates an existing user in the database. Modifiable entries are
     * name, visibility, longitude and latitude.
     * 
     * @param User $user
     * @throws DatabaseException
     */
    public function updateUser(User $user)
    {
        try
        {
            // We do not need to check the validity of parameters as it is done in the User class.
            $this->mDb->update(self::$TABLE_USER, 
                array(
                    'name' => $user->getName(),
                    'visibility' => $user->getVisibility(),
                    'longitude' => $user->getLongitude(),
                    'latitude' => $user->getLatitude(),
                    'last_update' => date(User::$DATE_FORMAT)
                ), array('idusers' => $user->getId()));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error updating user in updateUser.', 1, $e);
        }
    }
    
    // Friendship management
    
    /**
     * Gets the ids of the friends of the user with id $userId,
     * where the status of their friendhsip is in the array $status
     * (can be 'ALLOWED', 'DISALLOWED' or 'BLOCKED'),
     * and the following stauts in the array $follow (can be
     * 'FOLLOWED' or 'UNFOLLOWED').
     * 
     * @param long $userId
     * @param array $status
     * @param array $follow
     * @throws DatabaseException
     * @return array
     */
    public function getFriendsIds($userId,
                                  $status = array('ALLOWED', 'DISALLOWED'),
                                  $follow = array('FOLLOWED', 'UNFOLLOWED'))
    {
        if (!is_array($status) OR !is_array($follow))
        {
            throw new DatabaseException('Arguments $status and $follow must be arrays.');
        }
        
        $req = "SELECT id2 FROM " . self::$TABLE_FRIENDSHIP . " WHERE id1 = ? AND ".
        "status IN (?) AND follow IN (?)";
        
        try
        {
            $stmt = $this->mDb->executeQuery($req,
                                             array((int) $userId, $status, $follow),
                                             array(
                                                   \PDO::PARAM_INT,
                                                   \Doctrine\DBAL\Connection::PARAM_STR_ARRAY, 
                                                   \Doctrine\DBAL\Connection::PARAM_STR_ARRAY)
                                            );
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error getting friends ids in getFriendsIds.', 1, $e);
        }
        
        $ids = array();
        
        while ($id = $stmt->fetch())
        {
            $ids[] = (int) $id['id2'];
        }
        
        return $ids;
    }
    
    /**
     * Add a friendship link between two users, with status set to ALLOWED
     * and follow to FOLLOWED.
     * 
     * @param long $idUser
     * @param long $idFriend
     * @throws DatabaseException if the friendship link already exists.
     */
    public function addFriendshipLink($idUser, $idFriend)
    {
        // We first check that there is not already a friendship link.
        $req = "SELECT * FROM " . self::$TABLE_FRIENDSHIP . " WHERE id1 = ? AND id2 = ?";
        $data = $this->mDb->fetchAssoc($req, array((int) $idUser, (int) $idFriend));
        
        if ($data)
        {
            throw new DatabaseException('This friendship link already exists !');
        }
        
        try
        {
            $this->mDb->insert(self::$TABLE_FRIENDSHIP,
                array(
                    'id1' => (int) $idUser,
                    'id2' => (int) $idFriend,
                    'status' => 'ALLOWED',
                    'follow' => 'FOLLOWED'
                ));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error adding a firendship link in addFriendshipLink.', 1, $e);
        }
    }
    
    public function removeFriendshipLink($idUser, $idFriend)
    {
        try
        {
            $this->mDb->delete(self::$TABLE_FRIENDSHIP, array(
                'id1' => (int) $idUser,
                'id2' => (int) $idFriend
            ));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error removing a firendship link in removeFriendshipLink.', 1, $e);
        }
    }
    
    /**
     * Sets the status of a friendship link.
     * 
     * @param long $idUser
     * @param long $idFriend
     * @param string $status
     * @throws DatabaseException when the status is invalid, i.e. not ALLOWED, DISALLOWED or BLOCKED
     */
    public function setFriendshipStatus($idUser, $idFriend, $status)
    {
        if (!in_array($status, array('ALLOWED', 'DISALLOWED', 'BLOCKED')))
        {
            throw new DatabaseException('Invalid value for status !');
        }
        
        try
        {
            $this->mDb->update(self::$TABLE_FRIENDSHIP,
                               array('status' => $status),
                               array('id1' => (int) $idFriend, 'id2' => (int) $idUser)
                              );
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error setting friendship status in setFriendshipStatus.', 1, $e);
        }
    }
    
    /**
     * Sets the status of a list of friendship links.
     * @param long $idUser
     * @param array $idsFriends
     * @param array $status
     * @throws DatabaseException when either $status or $idsFreinds is invalid.
     */
    public function setFriendshipsStatus($idUser, $idsFriends, $status)
    {
        if (!in_array($status, array('ALLOWED', 'DISALLOWED', 'BLOCKED')))
        {
            throw new DatabaseException('Invalid value for status !');
        }
        
        if (!is_array($idsFriends))
        {
            throw new DatabaseException('Argument $idsFriends must be an array !');
        }
        
        $req = "UPDATE " . self::$TABLE_FRIENDSHIP .
               " SET status = ? WHERE id1 IN (?) AND id2 = ?";
        
        try
        {
            $stmt = $this->mDb->executeQuery($req,
                array($status, $idsFriends, $idUser),
                array(\PDO::PARAM_STR,
                    \Doctrine\DBAL\Connection::PARAM_INT_ARRAY,
                    \PDO::PARAM_INT
                ));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error setting friendships status in setFriendshipsStatus.', 1, $e);
        }
    }
    
    /**
     * Sets the follow status of a friendship link.
     * @param long $idUser
     * @param long $friendId
     * @param string $follow
     * @throws DatabaseException when $follow is invalid.
     */
    public function setFriendshipFollow($idUser, $friendId, $follow)
    {
        if (!in_array($follow, array('FOLLOWED', 'UNFOLLOWED')))
        {
            throw new DatabaseException('Invalid value for follow status !');
        }
        
        try
        {
            $this->mDb->update(self::$TABLE_FRIENDSHIP,
                array('follow' => $follow),
                array('id1' => (int) $idUser, 'id2' => (int) $friendId)
            );
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error setting frienship follow status in setFreindshipFollow.', 1, $e);
        }
    }
    
    // Invitations management

    /**
     * Gets a list of the ids of users who sent a friend invitation to
     * the user with id $userId.
     *
     * @param $userId
     * @return array
     * @throws DatabaseException
     */
    public function getInvitationIds($userId)
    {
        $req = "SELECT id1 FROM " . self::$TABLE_INVITATIONS .  " WHERE id2 = ?";
        
        try
        {
            $stmt = $this->mDb->executeQuery($req, array((int) $userId));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error getting invitations ids in getInvitationIds.', 1, $e);
        }
        
        $ids = array();
        
        while ($id = $stmt->fetch())
        {
            $ids[] = (int) $id['id1'];
        }
        
        return $ids;
    }
    
    /**
     * Adds an nvitation from the user with id $idUser to the user with
     * id $idFriend.
     * 
     * @param int $idUser
     * @param int $idFriend
     * @throws DatabaseException
     */
    public function addInvitation($idUser, $idFriend)
    {
        try
        {
            $this->mDb->insert(self::$TABLE_INVITATIONS, array(
                'id1' => (int) $idUser,
                'id2' => (int) $idFriend
            ));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error adding invitation in addInvitations.', 1, $e);
        }
    }
    
    /**
     * Removes the invitation from the user with id $idUser to the user
     * with id $friendId.
     * 
     * @param long $idUser
     * @param long $idFriend
     * @throws DatabaseException
     */
    public function removeInvitation($idUser, $idFriend)
    {
        try
        {
            $this->mDb->delete(self::$TABLE_INVITATIONS, array(
                'id1' => (int) $idUser,
                'id2' => (int) $idFriend
            ));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error removing invitation in removeInvitations.', 1, $e);
        }
    }
    
    /**
     * Adds an accepted invitation.
     * 
     * @param long $idUser
     * @param long $idFriend
     * @throws DatabaseException
     */
    public function addAcceptedInvitation($idUser, $idFriend)
    {
        try
        {
            $this->mDb->insert(self::$TABLE_ACCEPTED_INVITATIONS, array(
                'id1' => (int) $idUser,
                'id2' => (int) $idFriend
            ));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in addAcceptedInvitation.', 1, $e);
        }
    }
    
    /**
     * Gets the accepted invitations and deletes them from the database as the info is only needed once.
     * 
     * @param long $idUser
     * @throws DatabaseException
     * @return array
     */
    public function getAcceptedInvitations($idUser)
    {
        $req = "SELECT id1 FROM " . self::$TABLE_ACCEPTED_INVITATIONS .  " WHERE id2 = ?";
        
        try
        {
            $stmt = $this->mDb->executeQuery($req, array((int) $idUser));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in addAcceptedInvitation.', 1, $e);
        }
        
        $ids = array();
        
        while ($id = $stmt->fetch())
        {
            $ids[] = (int) $id['id1'];
        }
        
        return $ids;
    }

    /**
     * Remove an accepted friend invitation.
     *
     * @param $idUser
     * @param $friendId
     * @throws DatabaseException
     */
    public function removeAcceptedInvitation($idUser, $friendId)
    {
        try
        {
            $this->mDb->delete(self::$TABLE_ACCEPTED_INVITATIONS, array(
                'id1' => (int) $friendId,
                'id2' => (int) $idUser
            ));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in removeAcceptedInvitation.', 1, $e);
        }
    }

    /**
     * Add a removed friend notification.
     *
     * @param $idUser
     * @param $idFriend
     * @throws DatabaseException
     */
    public function addRemovedFriend($idUser, $idFriend)
    {
        try
        {
            $this->mDb->insert(self::$TABLE_REMOVED_FRIENDS, array(
                'id1' => (int) $idUser,
                'id2' => (int) $idFriend
            ));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in addRemovedFriend.', 1, $e);
        }
    }

    /**
     * Get an array of the id of the friends that removed the user.
     *
     * @param $idUser
     * @return array
     * @throws DatabaseException
     */
    public function getRemovedFriends($idUser)
    {
        $req = "SELECT id1 FROM " . self::$TABLE_REMOVED_FRIENDS .  " WHERE id2 = ?";

        try
        {
            $stmt = $this->mDb->executeQuery($req, array((int) $idUser));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in getRemovedFriends.', 1, $e);
        }

        $ids = array();

        while ($id = $stmt->fetch())
        {
            $ids[] = (int) $id['id1'];
        }

        return $ids;
    }

    /**
     * Remove a removed friend notification.
     *
     * @param $idUser
     * @param $friendId
     * @throws DatabaseException
     */
    public function removeRemovedFriend($idUser, $friendId)
    {
        try
        {
            $this->mDb->delete(self::$TABLE_REMOVED_FRIENDS, array(
                'id1' => (int) $friendId,
                'id2' => (int) $idUser
            ));
        }
        catch (\Exception $e)
        {
            throw new DatabaseException('Error in removeRemovedFriend.', 1, $e);
        }
    }

    // Utility functions

    /**
     * Gets an array of users given a database query statement.
     *
     * @param $stmt
     * @return array
     * @throws
     */
    private function userArrayFromStmt($stmt)
    {
        $users = array();
        
        while ($userData = $stmt->fetch())
        {
            try
            {
                $users[] = new User(
                    (int) $userData['idusers'],
                    (int) $userData['fbid'],
                    $userData['name'],
                    $userData['visibility'],
                    (double) $userData['longitude'],
                    (double) $userData['latitude'],
                    $userData['last_update']
                );
            }
            catch (\Exception $e)
            {
                throw DatabaseException('User with invalid state in database with id ' . $userData['idusers'] . '.');
            }
        }
        
        return $users;
    }
}
