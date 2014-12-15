package ch.epfl.smartmap.test.activities;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.mockito.Mockito;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.ModifyFilterActivity;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Filter;
import ch.epfl.smartmap.cache.User;

public class ModifyFilterActivityTest extends ActivityInstrumentationTestCase2<ModifyFilterActivity> {

    Filter filter;
    User user1;
    User user2;
    User user3;
    Set<User> allFriends;

    public ModifyFilterActivityTest() {
        super(ModifyFilterActivity.class);
        // ServiceContainer.forceInitSmartMapServices(this.getInstrumentation().getTargetContext());
        allFriends = new HashSet<User>(Arrays.asList(user1));

        // filter =
        // Filter.createFromContainer(new FilterContainer(3, "Family", new HashSet<Long>(Arrays.asList(user1
        // .getId())), true));
        Intent intent =
            new Intent(this.getInstrumentation().getTargetContext().getApplicationContext(),
                ModifyFilterActivity.class);
        intent.putExtra("FILTER", filter.getId());
        this.setActivityIntent(intent);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // this.getActivity();

        // // ServiceContainer.initSmartMapServices(this.getActivity());
        // ServiceContainer.forceInitSmartMapServices(this.getInstrumentation().getTargetContext());
        // user1 = User.createFromContainer(MockContainers.ALAIN);
        // user2 = User.createFromContainer(MockContainers.JULIEN);
        // user3 = User.createFromContainer(MockContainers.ROBIN);
        // allFriends = new HashSet<User>(Arrays.asList(user1, user2, user3));
        // //
        //
        // //
        Cache newCache = Mockito.mock(Cache.class);
        Mockito.when(newCache.getFilter(filter.getId())).thenReturn(filter);
        Mockito.when(newCache.getUser(user1.getId())).thenReturn(user1);
        Mockito.when(newCache.getUser(user2.getId())).thenReturn(user2);
        Mockito.when(newCache.getUser(user3.getId())).thenReturn(user3);
        Mockito.when(newCache.getAllFriends()).thenReturn(allFriends);
        ServiceContainer.setCache(newCache);
        //

        this.getActivity();

    }

    public void test1() {
        onView(withId(R.id.action_remove_filter)).check(matches(isDisplayed()));
    }
}
