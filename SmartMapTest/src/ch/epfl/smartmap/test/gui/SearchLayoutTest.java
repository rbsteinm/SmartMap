package ch.epfl.smartmap.test.gui;

import ch.epfl.smartmap.gui.SearchLayout;

public class SearchLayoutTest extends android.test.ActivityInstrumentationTestCase2
{
    public SearchLayoutTest()
    {
        super(SearchLayout.class);
    }

    // The standard JUnit 3 setUp method run for for every test
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        getActivity(); // prevent error "No activities found. Did you forget to launch the activity by calling getActivity()"
    }
    
    
}