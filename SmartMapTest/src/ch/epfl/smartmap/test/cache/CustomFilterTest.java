/**
 * 
 */
package ch.epfl.smartmap.test.cache;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;

import android.test.AndroidTestCase;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.CustomFilter;
import ch.epfl.smartmap.cache.Filter;
import ch.epfl.smartmap.cache.FilterContainer;

public class CustomFilterTest extends AndroidTestCase {

    long id = 3;
    String name = "family";
    String otherName = "friends";
    Set<Long> ids = new HashSet<Long>(Arrays.asList((long) 3, (long) 20));
    Set<Long> otherIds = new HashSet<Long>(Arrays.asList((long) 3));

    FilterContainer container;
    FilterContainer otherContainer;

    @Before
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ServiceContainer.forceInitSmartMapServices(this.getContext());

        this.initContainers();
    }

    @After
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // new container
        this.initContainers();
        // new cache
        ServiceContainer.setCache(new Cache());
    }

    public void testCreateFilterWithNullValues() {
        container.setIds(null);
        container.setName(null);
        ServiceContainer.getCache().putFilter(container);
        CustomFilter filter =
            (CustomFilter) ServiceContainer.getCache().getFilter(Filter.DEFAULT_FILTER_ID + 1);
        assertEquals(Filter.NO_NAME, filter.getName());
        assertEquals(Filter.NO_IDS, filter.getIds());
    }

    private void initContainers() {
        container = new FilterContainer(id, name, ids, true);
        otherContainer = new FilterContainer(id, otherName, otherIds, false);
    }

}
