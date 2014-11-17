package ch.epfl.smartmap.test.background;

//Import the uiautomator libraries
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;
/**
 * Test Notifications
 * @author agpmilli
 *
 */
public class NotificationsTest extends UiAutomatorTestCase {
	
	public static void swipeDownNotificationBar () {
	    UiDevice deviceInstance = UiDevice.getInstance();
	    int dHeight = deviceInstance.getDisplayHeight();
	    int dWidth = deviceInstance.getDisplayWidth();
	    System.out.println("height =" +dHeight);
	    System.out.println("width =" +dWidth);
	    int xScrollPosition = dWidth/2;
	    int yScrollStop = dHeight/2;
	    UiDevice.getInstance().swipe(
	        xScrollPosition, 
	        0, 
	        xScrollPosition, 
	        yScrollStop, 
	        100
	    );
	}
	
	/**
	 * Test if notifications are created with success
	 */
	public void testNotificationExist() {
		UiDevice.getInstance().openNotification();
	}
	
}
