package ch.epfl.smartmap.test.background;

import org.junit.Test;

import ch.epfl.smartmap.background.UpdateService;

import android.content.Intent;
import android.test.AndroidTestCase;

public class UpdateServiceTest extends AndroidTestCase {
    private Intent testIntent;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testIntent = new Intent(getContext(), UpdateService.class);
    }
    
    @Test
    public void testOnStartIntentInt() {
        testIntent = new Intent(getContext(), UpdateService.class);
        getContext().startService(testIntent);
    }
}
