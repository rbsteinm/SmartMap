/**
 * 
 */
package ch.epfl.smartmap.test.cache;

import static ch.epfl.smartmap.test.database.MockContainers.DEFAULT;
import static ch.epfl.smartmap.test.database.MockContainers.FAMILY;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import android.test.AndroidTestCase;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Filter;
import ch.epfl.smartmap.cache.FilterContainer;

import com.google.common.collect.Sets;

public class FilterTest extends AndroidTestCase {

    Cache cache;

    @Before
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ServiceContainer.forceInitSmartMapServices(this.getContext());
        cache = ServiceContainer.getCache();
    }

    @After
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // new cache
        ServiceContainer.setCache(new Cache());
        cache = ServiceContainer.getCache();
        FAMILY.setId(Filter.NO_ID);
    }

    @Test
    public void testCreateCorrectSubclass() {
        cache.putFilter(FAMILY.setId(Filter.DEFAULT_FILTER_ID));
        cache.putFilter(FAMILY.setId(Filter.NO_ID));

        assertEquals(Filter.DEFAULT, cache.getDefaultFilter().getType());
        assertEquals(Filter.CUSTOM, cache.getFilter(FAMILY.getId()).getType());
    }

    @Test
    public void testDefaultFilterContainsAllFriends() {
        Set<Long> friendIds = Sets.newHashSet((long) 1, (long) 2, (long) 3);
        Cache mockCache = Mockito.mock(Cache.class);
        Mockito.doReturn(friendIds).when(mockCache).getFriendIds();
        mockCache.putFilter(DEFAULT);
        assertEquals(friendIds, mockCache.getDefaultFilter().getVisibleFriends());
    }

    @Test
    public void testGetValues() {
        cache.putFilter(FAMILY);
        Filter family = ServiceContainer.getCache().getFilter(FAMILY.getId());

        assertEquals(FAMILY.getId(), family.getId());
        assertEquals(FAMILY.getName(), family.getName());
        assertEquals(FAMILY.getIds(), family.getIds());
    }

    @Test
    public void testUpdateWithGoodParameters() {
        cache.putFilter(FAMILY);
        Filter family = cache.getFilter(FAMILY.getId());

        family.update(new FilterContainer(FAMILY.getId(), "New name", Sets.newHashSet((long) 4, (long) 6,
            (long) 7), false));

        assertEquals(FAMILY.getId(), family.getId());
        assertEquals("New name", family.getName());
        assertEquals(Sets.newHashSet((long) 4, (long) 6, (long) 7), family.getIds());
        assertEquals(false, family.isActive());
    }

    @Test
    public void testUpdateWithSameParameters() {
        cache.putFilter(FAMILY);
        Filter family = cache.getFilter(FAMILY.getId());
        assertFalse(family.update(family.getContainerCopy()));
    }

    @Test
    public void testUpdateWithWrongId() {
        cache.putFilter(FAMILY);
        Filter family = cache.getFilter(FAMILY.getId());
        try {
            family.update(FAMILY.setId(6));
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        }
    }
}
