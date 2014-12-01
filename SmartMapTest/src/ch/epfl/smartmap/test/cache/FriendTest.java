package ch.epfl.smartmap.test.cache;

import org.junit.Test;

import android.graphics.BitmapFactory;
import android.test.AndroidTestCase;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Friend;

public class FriendTest extends AndroidTestCase {
	private final long id = 1111;
	private final String name = "test name";
	private final String name2 = "other name";
	private final String number = "0790000000";
	private final String email = "test@test.com";
	private final double x = 1.23;
	private final double y = 3.21;
	private Friend friend;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		friend = new Friend(id, name);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		friend.deletePicture(this.getContext());
	}

	@Test
	public void testDeletePic() {
		friend.setPicture(BitmapFactory.decodeResource(this.getContext()
				.getResources(), R.drawable.ic_search), this.getContext());
		friend.deletePicture(this.getContext());
		assertTrue(friend.getPicture(this.getContext()).sameAs(
				BitmapFactory.decodeResource(this.getContext().getResources(),
						Friend.DEFAULT_PICTURE)));
	}

	@Test
	public void testGetDefaultPic() {
		assertTrue(friend.getPicture(this.getContext()).sameAs(
				BitmapFactory.decodeResource(this.getContext().getResources(),
						Friend.DEFAULT_PICTURE)));
	}

	@Test
	public void testGetID() {
		assertTrue(friend.getID() == id);
	}

	@Test
	public void testGetName() {
		assertTrue(friend.getName().equals(name));
	}

	@Test
	public void testSetEmail() {
		friend.setEmail(email);
		assertTrue(friend.getEmail().equals(email));
	}

	@Test
	public void testSetName() {
		friend.setName(name2);
		assertTrue(friend.getName().equals(name2));
	}

	@Test
	public void testSetNumber() {
		friend.setNumber(number);
		assertTrue(friend.getNumber().equals(number));
	}

	@Test
	public void testSetPic() {
		friend.setPicture(BitmapFactory.decodeResource(this.getContext()
				.getResources(), R.drawable.ic_search), this.getContext());
		assertTrue(friend.getPicture(this.getContext()).sameAs(
				BitmapFactory.decodeResource(this.getContext().getResources(),
						R.drawable.ic_search)));
	}

	@Test
	public void testSetX() {
		friend.setLongitude(x);
		assertTrue(friend.getLocation().getLongitude() == x);
	}

	@Test
	public void testSetY() {
		friend.setLatitude(y);
		assertTrue(friend.getLocation().getLatitude() == y);
	}
}