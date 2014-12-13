<?php
/**
 * Created by PhpStorm.
 * User: matthieu
 * Date: 13.12.14
 * Time: 18:07
 */
namespace SmartMap\DBInterface;


/**
 * Models the user repo.
 *
 * @author Pamoi
 *
 * @author SpicyCH (code reviewed - 03.11.2014) : added javadoc, need to unit test, corrected a typo:
 * it was getLatitude, it is now getLongitude.
 */
interface UserRepositoryInterface
{
    /**
     * Gets a user id given it's facebook id, or returns false if such user
     * does not exist.
     *
     * @param $fbId
     * @return bool
     * @throws DatabaseException
     */
    public function getUserIdFromFb($fbId);

    /**
     * Gets a user from the database, given it's id.
     *
     * @param Long $id
     * @throws DatabaseException when the user is not found
     * @return \SmartMap\DBInterface\User
     */
    public function getUser($id);

    /**
     * Gets a list of users, given a list of ids.
     *
     * @param array $ids
     * @param array $visibility
     * @throws DatabaseException if $ids or $visibility is not of the right type
     * @return User
     */
    public function getUsers($ids, $visibility = array('VISIBLE', 'INVISIBLE'));

    /**
     * Gets a list of users whose name is starting by $partialName
     *
     * @param String $partialName
     * @throws DatabaseException
     * @return User
     */
    public function findUsersByPartialName($partialName, $excludedIds);

    /**
     * Adds a new user in the database. The id value from the parameter
     * is not used. Returns the created user with it's id properly set.
     *
     * @param User $user
     * @throws DatabaseException
     * @return User
     */
    public function createUser(User $user);

    /**
     * Updates an existing user in the database. Modifiable entries are
     * name, visibility, longitude and latitude.
     *
     * @param User $user
     * @throws DatabaseException
     */
    public function updateUser(User $user);

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
    public function getFriendsIds($userId, $status = array('ALLOWED', 'DISALLOWED'), $follow = array('FOLLOWED', 'UNFOLLOWED'));

    /**
     * Add a bidirectional friendship link between two users, with status set to ALLOWED
     * and follow to FOLLOWED.
     *
     * @param long $idUser
     * @param long $idFriend
     * @throws DatabaseException if the friendship link already exists.
     */
    public function addFriendshipLink($idUser, $idFriend);

    /**
     * Removes a bidirectional friendship link. Returns true if the friendship link existed, false
     * otherwise.
     *
     * @param $idUser
     * @param $idFriend
     * @return int
     * @throws DatabaseException
     */
    public function removeFriendshipLink($idUser, $idFriend);

    /**
     * Sets the status of a friendship link.
     *
     * @param int $idUser
     * @param int $idFriend
     * @param string $status
     * @throws DatabaseException when the status is invalid, i.e. not ALLOWED, DISALLOWED or BLOCKED
     */
    public function setFriendshipStatus($idUser, $idFriend, $status);

    /**
     * Sets the status of a list of friendship links.
     * @param int $idUser
     * @param array $idsFriends
     * @param array $status
     * @throws DatabaseException when either $status or $idsFriends is invalid.
     */
    public function setFriendshipsStatus($idUser, $idsFriends, $status);

    /**
     * Sets the follow status of a friendship link.
     * @param int $idUser
     * @param int $friendId
     * @param string $follow
     * @throws DatabaseException when $follow is invalid.
     */
    public function setFriendshipFollow($idUser, $friendId, $follow);

    /**
     * Gets a list of the ids of users who sent a friend invitation to
     * the user with id $userId.
     *
     * @param $userId
     * @return array
     * @throws DatabaseException
     */
    public function getInvitationIds($userId);

    /**
     * Get a list of the ids of users who are invited by the user with id $userId.
     *
     * @param $userId
     * @return array
     * @throws DatabaseException
     */
    public function getInvitedIds($userId);

    /**
     * Adds an nvitation from the user with id $idUser to the user with
     * id $idFriend.
     *
     * @param int $idUser
     * @param int $idFriend
     * @throws DatabaseException
     */
    public function addInvitation($idUser, $idFriend);

    /**
     * Removes the invitation from the user with id $idUser to the user
     * with id $friendId.
     *
     * @param long $idUser
     * @param long $idFriend
     * @throws DatabaseException
     */
    public function removeInvitation($idUser, $idFriend);

    /**
     * Adds an accepted invitation.
     *
     * @param long $idUser
     * @param long $idFriend
     * @throws DatabaseException
     */
    public function addAcceptedInvitation($idUser, $idFriend);

    /**
     * Gets the accepted invitations and deletes them from the database as the info is only needed once.
     *
     * @param long $idUser
     * @throws DatabaseException
     * @return array
     */
    public function getAcceptedInvitations($idUser);

    /**
     * Remove an accepted friend invitation.
     *
     * @param $idUser
     * @param $friendId
     * @throws DatabaseException
     */
    public function removeAcceptedInvitation($idUser, $friendId);

    /**
     * Add a removed friend notification.
     *
     * @param $idUser
     * @param $idFriend
     * @throws DatabaseException
     */
    public function addRemovedFriend($idUser, $idFriend);

    /**
     * Get an array of the id of the friends that removed the user.
     *
     * @param $idUser
     * @return array
     * @throws DatabaseException
     */
    public function getRemovedFriends($idUser);

    /**
     * Remove a removed friend notification.
     *
     * @param $idUser
     * @param $friendId
     * @throws DatabaseException
     */
    public function removeRemovedFriend($idUser, $friendId);
}