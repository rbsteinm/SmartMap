/**
 * 
 */
package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hugo
 *
 */
public class MockDB {

	public MockDB() {

	}

	public List<Friend> getMockFriends() {
		
		Friend f1 = new Friend(6, "Cristophe Alain","" );
		f1.setLatitude(45.2547877);
		f1.setLongitude(7.2142577);
		


		Friend f2 = new Friend(1, "Georges Smith","");
		f2.setLatitude(46.2547877);
		f2.setLongitude(3.2142577);

		Friend f3 = new Friend(2, "Marc Dupont","");
		f3.setLatitude(42.2547877);
		f3.setLongitude(5.214577);

		Friend f4 = new Friend(3, "Pierre-Yves Drap","");
		f4.setLatitude(39.2547877);
		f4.setLongitude(5.214577);

		Friend f5 = new Friend(4, "Catherine Joe","");
		f5.setLatitude(50.2547877);
		f5.setLongitude(2.004577);

		Friend f7 = new Friend(5, "Jean Emmanuel","");
		f7.setLatitude(41.5547877);
		f7.setLongitude(2.4255457);

		Friend f8 = new Friend(7, "Julia Syrano","");
		f8.setLatitude(39.5547877);
		f8.setLongitude(7.4255457);

		ArrayList<Friend> friends = new ArrayList<Friend>();
		friends.add(f1);
		friends.add(f2);
		friends.add(f3);
		friends.add(f4);
		friends.add(f5);
		friends.add(f7);
		friends.add(f8);

		return friends;
	}
}

