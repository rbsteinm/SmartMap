<?php

use SmartMap\DBInterface\User;
use SmartMap\DBInterface\UserRepository;

use Doctrine\DBAL\DriverManager;
use Doctrine\DBAL\Configuration;

/** Tests for the UserRepository class.
 * To run them, run 
 * $> phpunit --bootstrap vendor/autoload.php --configuration tests/phpunit.xml tests/UserRepositoryTest.php
 * from the server directory. You need to have a database configured in phpunit.xml running during the tests.
 * 
 * @author Pamoi
 *
 */
class UserRepositoryTest extends PHPUnit_Extensions_Database_TestCase
{
	// only instantiate doctrine connection once
	static private $doctrine = null;
	
	// only instantiate PHPUnit_Extensions_Database_DB_IDatabaseConnection once per test
	private $conn = null;
	
	final public function getConnection()
	{
	    if ($this->conn === null) {
	        if (self::$doctrine == null) {
	            $config = new Configuration();
	            $connectionParams = array(
	                'dbname' => $GLOBALS['DB_DBNAME'],
	                'user' => $GLOBALS['DB_USER'],
	                'password' => $GLOBALS['DB_PASSWD'],
	                'host' => 'localhost',
	                'driver' => 'pdo_mysql',
	            );
	            self::$doctrine = DriverManager::getConnection($connectionParams, $config);
	        }
	        $this->conn = $this->createDefaultDBConnection(self::$doctrine->getWrappedConnection(),
	               $GLOBALS['DB_DBNAME']);
	    }
	
	    return $this->conn;
	}
	
	protected function getDataSet()
	{
	    return new PHPUnit_Extensions_Database_DataSet_YamlDataSet(
	        dirname(__FILE__)."/fixtures/UserRepositoryTest.yml");
	}
	
	public function testValidGetUserIdFromFb()
	{
		$repo = new UserRepository(self::$doctrine);
		
		$id = $repo->getUserIdFromFb(12345);
		
		$this->assertEquals(4, $id);
	}
	
	public function testInvalidGetUserIdFromFb()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $id = $repo->getUserIdFromFb(23);
	    
	    $this->assertEquals(false, $id);
	}
	
	public function testGetExistingUsers()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $correct = new User(1, 274, 'Toto', 'VISIBLE', 42.05, 23.77);
	    
	    $user = $repo->getUser(1);
	    
	    $this->assertEquals($correct, $user);
	}
	
	/**
	 * @expectedException \Exception
	 * @expectedExceptionMessage No user found with id 56.
	 */
	public function testGetNonexistingUser()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $user = $repo->getUser(56);
	}
	
	public function testGetUsers()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $correct = array(
	        new User(1, 274, 'Toto', 'VISIBLE', 42.05, 23.77),
	        new User(2, 347876, 'Titi', 'INVISIBLE', 12.75645, 54.34556)
	    );
	    
	    $users = $repo->getUsers(array(1, 2));
	    
	    $this->assertEquals($correct, $users);
	}
	
	public function testGetNonexistingUsers()
	{
	    $repo = new UserRepository(self::$doctrine);
	     
	    $correct = array();
	     
	    $users = $repo->getUsers(array(25, 26, 27));
	     
	    $this->assertEquals($correct, $users);
	}
	
	public function testGetVisibleUsers()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $correct = array(
	        new User(1, 274, 'Toto', 'VISIBLE', 42.05, 23.77),
	        new User(4, 12345, 'Tutu', 'VISIBLE', 156.85, -89.765)
	    );
	    
	    $users = $repo->getUsers(array(1, 2, 3, 4), array('VISIBLE'));
	    
	    $this->assertEquals($correct, $users);
	}
	
	/**
	 * @expectedException \Exception
	 * @expectedExceptionMessage Arguments $ids and $visibility must be arrays.
	 */
	public function testGetUsersWithInvalidArguments()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $users = $repo->getUsers(array(1, 2, 3, 4), 'VISIBLE');
	}
	
	public function testFindUsersByPartialName1()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $correct = array(
	        new User(1, 274, 'Toto', 'VISIBLE', 42.05, 23.77),
	        new User(2, 347876, 'Titi', 'INVISIBLE', 12.75645, 54.34556),
	        new User(3, 987645, 'Tata', 'INVISIBLE', -12.4546, -73.76),
	        new User(4, 12345, 'Tutu', 'VISIBLE', 156.85, -89.765)
	    );
	    
	    $users = $repo->findUsersByPartialName('t');
	    
	    $this->assertEquals($correct, $users);
	}
	
	public function testFindUsersByPartialName2()
	{
	    $repo = new UserRepository(self::$doctrine);
	     
	    $correct = array(
	        new User(1, 274, 'Toto', 'VISIBLE', 42.05, 23.77)
	    );
	     
	    $users = $repo->findUsersByPartialName('tO');
	     
	    $this->assertEquals($correct, $users);
	}
	
	public function testFindUsersByPartialName3()
	{
	    $repo = new UserRepository(self::$doctrine);
	     
	    $correct = array();
	     
	    $users = $repo->findUsersByPartialName('toa');
	     
	    $this->assertEquals($correct, $users);
	}
	
	public function testCreateUser()
	{
	    $this->assertEquals(4, $this->getConnection()->getRowCount('users'), "Pre-Condition");
	    
	    $repo = new UserRepository(self::$doctrine);
	    
	    $user = new User(1, 56789, 'Tete', 'VISIBLE', 1.0, 2.0);
	    
	    $user = $repo->CreateUser($user);
	    
	    $this->assertEquals(5, $user->getId());
	    
	    $this->assertEquals(5, $this->getConnection()->getRowCount('users'), "Post-Condition");
	}
	
	public function testUpdateUser()
	{
	    $repo = new UserRepository(self::$doctrine);
	     
	    $user = new User(1, 123, 'Tete', 'INVISIBLE', 1.0, 2.0);
	    
	    $repo->updateUser($user);
	    
	    $correctRow = array(
	        'idusers' => 1,
	        'fbid' => 274,
	        'name' => 'Tete',
	        'visibility' => 'INVISIBLE',
	        'longitude' => 1.0,
	        'latitude' => 2.0
	    );
	    
	    $queryTable = $this->getConnection()->createQueryTable('users', 'SELECT * FROM users WHERE idusers = 1');
	    
	    $row = $queryTable->getRow(0);
	    
	    $this->assertEquals($correctRow, $row);
	}
	
	public function testGetFriendsIds()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $correct = array(2, 3);
	    
	    $ids = $repo->getFriendsIds(1);
	    
	    $this->assertEquals($correct, $ids);
	}
	
	public function testGetFriendsIdsWithStatus()
	{
	    $repo = new UserRepository(self::$doctrine);
	     
	    $correct = array(3);
	     
	    $ids = $repo->getFriendsIds(1, array('ALLOWED'));
	     
	    $this->assertEquals($correct, $ids);
	}
	
	public function testGetFriendsIdsWithStatusAndFollow()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $correct = array(2);
	    
	    $ids = $repo->getFriendsIds(1, array('ALLOWED', 'DISALLOWED'), array('FOLLOWED'));
	    
	    $this->assertEquals($correct, $ids);
	}
	
	/**
	 * @expectedException \Exception
	 * @expectedExceptionMessage Arguments $status and $follow must be arrays.
	 */
	public function testGetFriendsIdsWithInvalidArguments()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $repo->getFriendsIds(1, 'ALLOWED', 'FOLLOWED');
	}
	
	public function testGetFriendsIdsWithInvalidEnum()
	{
	    $repo = new UserRepository(self::$doctrine);
	     
	    $correct = array();
	     
	    $ids = $repo->getFriendsIds(1, array('Toto'));
	    
	    $this->assertEquals($correct, $ids);
	}
	
	public function testAddFriendshipLink()
	{
	    $this->assertEquals(4, $this->getConnection()->getRowCount('friendships'), "Pre-Condition");
	    
	    $repo = new UserRepository(self::$doctrine);
	    
	    $repo->addFriendshipLink(1, 4);
	    
	    $this->assertEquals(5, $this->getConnection()->getRowCount('friendships'), "Post-Condition");
	}
	
	/**
	 * @expectedException \Exception
	 * @expectedExceptionMessage This friendship link already exists !
	 */
	public function testAddAlreadyExistingFriendshipLink()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $repo->addFriendshipLink(1, 3);
	}
	
	public function testSetFriendshipStatus()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    // User 2 allows user 1 to see him. He is not allowed initially.
	    $repo->setFriendshipStatus(2, 1, 'ALLOWED');
	    
	    // The id1 and id2 are flipped because allowance is in dierction friend -> user (friend allows user to see him)
	    $queryTable = $this->getConnection()->createQueryTable('friendships',
	        'SELECT status FROM friendships WHERE id1 = 1 AND id2 = 2');
	    
	    $row = $queryTable->getRow(0);
	    
	    $this->assertEquals('ALLOWED', $row['status']);
	}
	
	/**
	 * @expectedException \Exception
	 * @expectedExceptionMessage Invalid value for status !
	 */
	public function testSetFriendshipStatusWithInvalidStatus()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $repo->setFriendshipStatus(2, 1, 'toto');
	}
	
	public function testSetFriendshipsStatus()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $friendsIds = array(2, 3);
	    
	    $repo->setFriendshipsStatus(1, $friendsIds, 'ALLOWED');
	    
	    $correctDataSet = new PHPUnit_Extensions_Database_DataSet_YamlDataSet(
	        dirname(__FILE__)."/fixtures/UserRepositorySetFriendshipsStatus.yml");
	    $correctTable = $correctDataSet->getTable('friendships');
	    
	    $queryTable = $this->getConnection()->createQueryTable('friendships', 'SELECT * FROM friendships');
	    
	    $this->assertTablesEqual($correctTable, $queryTable);
	}
	
	/**
	 * @expectedException \Exception
	 * @expectedExceptionMessage Invalid value for status !
	 */
	public function testSetFriendshipsStatusWithInvalidStatus()
	{
	    $repo = new UserRepository(self::$doctrine);
	     
	    $friendsIds = array(2, 3);
	     
	    $repo->setFriendshipsStatus(1, $friendsIds, 'toto');
	}
	
	/**
	 * @expectedException \Exception
	 * @expectedExceptionMessage Argument $idsFriends must be an array !
	 */
	public function testSetFriendshipsStatusWithNonArrayArgument()
	{
	    $repo = new UserRepository(self::$doctrine);
	
	    $friendsIds = 2;
	
	    $repo->setFriendshipsStatus(1, $friendsIds, 'ALLOWED');
	}
	
	public function testSetFriendshipFollow()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $repo->setFriendshipFollow(1, 2, 'UNFOLLOWED');
	    
	    $queryTable = $this->getConnection()->createQueryTable('friendships',
	        'SELECT follow FROM friendships WHERE id1 = 1 AND id2 = 2');
	    
	    $row = $queryTable->getRow(0);
	    
	    $this->assertEquals('UNFOLLOWED', $row['follow']);
	}
	
	/**
	 * @expectedException \Exception
	 * @expectedExceptionMessage Invalid value for follow status !
	 */
	public function testSetFriendshipFollowkWithInvalidStatus()
	{
	    $repo = new UserRepository(self::$doctrine);
	     
	    $repo->setFriendshipFollow(1, 2, 'toto');
	}
	
	public function testGetInvitationsIds()
	{
	    $repo = new UserRepository(self::$doctrine);
	    
	    $ids = $repo->getInvitationIds(4);
	    
	    $this->assertEquals(array(1), $ids);
	}
	
	public function testAddInvitation()
	{
	    $this->assertEquals(1, $this->getConnection()->getRowCount('invitations'), "Pre-Condition");
	    
	    $repo = new UserRepository(self::$doctrine);
	     
	    $ids = $repo->addInvitation(1, 5);
	    
	    $this->assertEquals(2, $this->getConnection()->getRowCount('invitations'), "Post-Condition");
	}
	
	public function testRemoveInvitation()
	{
	    $this->assertEquals(1, $this->getConnection()->getRowCount('invitations'), "Pre-Condition");
	     
	    $repo = new UserRepository(self::$doctrine);
	    
	    $ids = $repo->removeInvitation(1, 4);
	     
	    $this->assertEquals(0, $this->getConnection()->getRowCount('invitations'), "Post-Condition");
	}
}