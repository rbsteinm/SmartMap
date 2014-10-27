/**
 * 
 */
package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hugo-S
 * 
 */
public class MockDB {

	private static Friend f1 = new Friend(6, "Cristophe Alain", 45.2547877,
			7.2142577);
	private static Friend f2 = new Friend(1, "Georges Smith", 46.2547877,
			3.2142577);
	private static Friend f3 = new Friend(2, "Marc Dupont", 42.2547877,
			5.214577);
	private static Friend f4 = new Friend(3, "Pierre-Yves Drap", 39.2547877,
			5.214577);
	private static Friend f5 = new Friend(4, "Catherine Joe", 50.2547877,
			2.004577);
	private static Friend f7 = new Friend(5, "Jean Emmanuel", 41.5547877,
			2.4255457);
	private static Friend f8 = new Friend(7, "Julia Syrano", 39.5547877,
			7.4255457);

	public MockDB() {

	}

	public List<Friend> getMockFriends() {

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
