package ch.epfl.smartmap.test.cache;

import org.junit.Test;

import ch.epfl.smartmap.cache.SettingsManager;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

public class SettingsManagerTest extends AndroidTestCase {

    private Context mContext;
    private SettingsManager mManager;
    private final long mID = 123456;
    private final String mEmail = "abc@cde.com";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = new RenamingDelegatingContext(getContext(), "test_");
        mManager = SettingsManager.initialize(mContext);
    }

    @Test
    public void testGetUserID() {
        mManager.setUserID(mID);
        assertTrue(mManager.getUserID() == mID);
    }

    @Test
    public void testGetEmail() {
        mManager.setEmail(mEmail);
        assertTrue(mManager.getEmail().equals(mEmail));
    }

    @Test
    public void testGetFacebookID() {
        // Checking if default values are returned
        assertTrue(mManager.getFacebookID() == SettingsManager.DEFAULT_FB_ID);
    }
}
