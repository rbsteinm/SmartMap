package ch.epfl.smartmap.test.background;

import org.junit.Test;

import android.content.Intent;
import android.test.ServiceTestCase;
import ch.epfl.smartmap.background.UpdateService;

public class UpdateServiceTest extends ServiceTestCase<UpdateService> {
    private Intent testIntent;

    public UpdateServiceTest(Class<UpdateService> serviceClass) {
        super(serviceClass);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        testIntent = new Intent(this.getContext(), UpdateService.class);
    }

    @Test
    public void testOnStartIntentInt() {
        testIntent = new Intent(this.getContext(), UpdateService.class);
        this.getContext().startService(testIntent);
    }
}