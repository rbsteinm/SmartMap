package ch.epfl.smartmap.test.activities;

import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.activities.StartActivity;

/**
 * @author Alain
 */
public class StartActivityTest extends ActivityInstrumentationTestCase2<StartActivity> {
	public StartActivityTest() {
		super(StartActivity.class);
	}

	// The standard JUnit 3 setUp method run for for every test
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.getActivity(); // prevent error
		// "No activities found. Did you forget to launch the activity by calling getActivity()"
	}

	@Override
	protected void tearDown() throws Exception {
		this.getActivity().finish();
	}

	/**
	 * Click on startActivity shouldn't do anything
	 * 
	 * @throws Exception
	 */
	public void testContainerClick() throws Exception {
		// if ((Session.getActiveSession() == null) || Session.getActiveSession().getPermissions().isEmpty())
		// {
		// onView(withId(R.id.container)).perform(click()).check(matches(isDisplayed()));
		// } else {
		// // OK
		// }
	}

	/*
	 * Test if facebook button is displayed or not (depends if first log in or
	 * not)
	 */
	public void testFacebookButtonVisibility() throws Exception {
		// Same as TestLogoClick()
		// if (Session.getActiveSession() == null
		// || Session.getActiveSession().getPermissions().isEmpty()) {
		// onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
		// }
	}

	/**
	 * Click on logo shouldn't do anything
	 * 
	 * @throws Exception
	 */
	public void testLogoClick() throws Exception {
		// this methods doesn't start properly because device get stuck in
		// mainActivity after first test (ISSUE)
		// if (Session.getActiveSession() == null
		// || Session.getActiveSession().getPermissions().isEmpty()) {
		// onView(withId(R.id.logo)).perform(click()).check(
		// matches(isDisplayed()));
		// }
	}

	/**
	 * Welcome text should never be displayed, it is display during animation
	 * but not before neither after
	 * 
	 * @throws Exception
	 */
	public void testWelcomeApparence() throws Exception {
		// Same as TestLogoClick()
		// onView(withId(R.id.welcome)).check(matches(not(isDisplayed())));
	}

}