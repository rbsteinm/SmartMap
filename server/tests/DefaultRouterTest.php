<?php

class DefaultRouterTest extends PHPUnit_Framework_TestCase
{
    public function testRoute()
    {
        echo PHP_EOL . "====> Routing <====" . PHP_EOL . PHP_EOL;
        $context = $this->getMock('SmartMap\Control\Context');
        
        $controller = $this->getMock('SmartMap\Control\Controller');
        
        $controller->method('authenticate')
             ->willReturn('1');
             
        $controller->method('listFriendsPos')
             ->willReturn('2');
             
        $controller->method('followFriend')
             ->willReturn('3');
             
        $controller->method('unfollowFriend')
             ->willReturn('4');
             
        $controller->method('allowFriend')
             ->willReturn('5');
             
        $controller->method('disallowFriend')
             ->willReturn('6');
             
        $controller->method('allowFriendList')
             ->willReturn('7');
             
        $controller->method('disallowFriendList')
             ->willReturn('8');
             
        $controller->method('inviteFriend')
             ->willReturn('9');
             
        $controller->method('getInvitations')
             ->willReturn('10');
             
        $controller->method('acceptInvitation')
             ->willReturn('11');
             
        $controller->method('getUserInfo')
             ->willReturn('12');
        
        
        $router = new SmartMap\Routing\DefaultRouter($context, $controller);
        
        echo 'Testing authentication route' . PHP_EOL;
        $this->AssertEquals('1', $router->getResponse("/auth"));
        
        echo 'Testing listFriendsPos route' . PHP_EOL;
        $this->AssertEquals('2', $router->getResponse("/listFriendsPos"));
        
        echo 'Testing followFriend route' . PHP_EOL;
        $this->AssertEquals('3', $router->getResponse("/followFriend"));
        
        echo 'Testing unfollowFriend route' . PHP_EOL;
        $this->AssertEquals('4', $router->getResponse("/unfollowFriend"));
        
        echo 'Testing allowFriend route' . PHP_EOL;
        $this->AssertEquals('5', $router->getResponse("/allowFriend"));
        
        echo 'Testing disallowFriend route' . PHP_EOL;
        $this->AssertEquals('6', $router->getResponse("/disallowFriend"));
        
        echo 'Testing allowFriendList route' . PHP_EOL;
        $this->AssertEquals('7', $router->getResponse("/allowFriendList"));
        
        echo 'Testing disallowFriendList route' . PHP_EOL;
        $this->AssertEquals('8', $router->getResponse("/disallowFriendList"));
        
        echo 'Testing inviteFriend route' . PHP_EOL;
        $this->AssertEquals('9', $router->getResponse("/inviteFriend"));
        
        echo 'Testing getInvitations route' . PHP_EOL;
        $this->AssertEquals('10', $router->getResponse("/getInvitations"));
        
        echo 'Testing acceptInvitation route' . PHP_EOL;
        $this->AssertEquals('11', $router->getResponse("/acceptInvitation"));
        
        echo 'Testing getUserInfo route' . PHP_EOL;
        $this->AssertEquals('12', $router->getResponse("/getUserInfo"));
    }
}
