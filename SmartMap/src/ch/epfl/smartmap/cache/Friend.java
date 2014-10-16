package ch.epfl.smartmap.cache;

/**
 * A class to represent the user's friends
 * @author ritterni
 */
public class Friend implements User {
	
	private int id; //the user's unique ID
	private String name; //the user's name as it will be displayed
	private String phoneNumber;
	private String email;
	private Point position;
	
	/**
	 * Friend constructor
	 * @param friendID the id of the contact we're creating
	 * @param friendName the name of the friend
	 * @author ritterni
	 */
	public Friend(int userID, String userName, String userNumber) {
		id = userID;
		name = userName;
		phoneNumber = userNumber;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getNumber() {
		return phoneNumber;
	}
	
	@Override
	public String getEmail() {
		return email;
	}
	
	@Override
	public Point getPosition() {
		return position;
	}
	
	@Override
	public void setName(String newName) {
		name = newName;
	}
	
	@Override
	public void setEmail(String newEmail) {
		email = newEmail;
	}

	@Override
	public void setX(double x) {
		position.setX(x);
		
	}

	@Override
	public void setY(double y) {
		position.setY(y);		
	}
}