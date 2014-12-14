package ch.epfl.smartmap.test.background;

import org.junit.Test;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import ch.epfl.smartmap.background.SettingsManager;

public class SettingsManagerTest extends AndroidTestCase {

    private Context mContext;
    private SettingsManager mManager;
    private final long mID = 123456;
    private final String mEmail = "abc@cde.com";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = new RenamingDelegatingContext(this.getContext(), "test_");
        mManager = new SettingsManager(mContext);
    }

    @Test
    public void testGetCookie() {
        mManager.setCookie("qwertzuiop");
        assertTrue(mManager.getCookie().equals("qwertzuiop"));
    }

    @Test
    public void testGetEmail() {
        mManager.setEmail(mEmail);
        assertTrue(mManager.getEmail().equals(mEmail));
    }

    @Test
    public void testGetFacebookID() {
        mManager.setFacebookID(123456789);
        assertEquals(mManager.getFacebookID(), 123456789);
    }

    @Test
    public void testGetToken() {
        mManager.setToken("123456");
        assertTrue(mManager.getToken().equals("123456"));
    }

    @Test
    public void testGetUserID() {
        mManager.setUserID(mID);
        assertEquals(mManager.getUserId(), mID);
    }

    @Test
    public void testGetUserName() {
        mManager.setUserName("New Name");
        assertTrue(mManager.getUserName().equals("New Name"));
    }
}