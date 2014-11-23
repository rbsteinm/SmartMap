package ch.epfl.smartmap.test.severcom;

import android.test.InstrumentationTestCase;

/** A TestCase with support for Mockito */
public class MockTestCase extends InstrumentationTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // XXX: Hack required to make Mockito work on Android
        System.setProperty("dexmaker.dexcache", this.getInstrumentation().getTargetContext().getCacheDir().getPath());
    }

}