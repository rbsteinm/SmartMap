package ch.epfl.smartmap.cache;

/**
 * A class to represent the user's friends
 * 
 * @author ritterni
 */
public class Friend implements User {

	private long id; // the user's unique ID
	private String name; // the user's name as it will be displayed
	private String phoneNumber;
	private String email;
	private Point position;
	private String positionName;

	public static final String NO_NUMBER = "No phone number specified";
	public static final String NO_EMAIL = "No email address specified";
	public static final String POSITION_UNKNOWN = "Unknown position";

	/**
	 * Friend constructor
	 * 
	 * @param userID
	 *            The id of the contact we're creating
	 * @param userName
	 *            The name of the friend
	 * @param userNumber
	 *            The friend's phone number
	 * @author ritterni
	 */
	public Friend(long userID, String userName) {
		id = userID;
		name = userName;
		phoneNumber = NO_NUMBER;
		email = NO_EMAIL;
		position = new Point(0, 0);
		positionName = POSITION_UNKNOWN;
	}

	@Override
	public long getID() {
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

    @Override
    public String getPositionName() {
        return positionName;
    }

    @Override
    public void setPositionName(String posName) {
        positionName = posName;
    }
}