package ch.epfl.smartmap.test.cache;

import org.junit.Test;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import ch.epfl.smartmap.cache.SettingsManager;

public class SettingsManagerTest extends AndroidTestCase {

    private Context mContext;
    private SettingsManager mManager;
    private final long mID = 123456;
    private final String mEmail = "abc@cde.com";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = new RenamingDelegatingContext(this.getContext(), "test_");
        mManager = SettingsManager.initialize(mContext);
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

    @Test
    public void testGetUserID() {
        mManager.setUserID(mID);
        assertTrue(mManager.getUserID() == mID);
    }
}
