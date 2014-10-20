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
	public static final String NO_NUMBER = "No phone number specified";
	public static final String NO_EMAIL = "No email address specified";
	
	/**
	 * Friend constructor
	 * @param userID The id of the contact we're creating
	 * @param userName The name of the friend
	 * @param userNumber The friend's phone number
	 * @author ritterni
	 */
	public Friend(int userID, String userName) {
		id = userID;
		name = userName;
		phoneNumber = NO_NUMBER;
		email = NO_EMAIL;
		position = new Point(0, 0);
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
    public void setNumber(String newNumber) {
        phoneNumber = newNumber;        
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