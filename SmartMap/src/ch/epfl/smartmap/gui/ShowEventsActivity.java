package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;

/**
 * This activity shows the events and offers to filter them.
 *
 * @author SpicyCH
 *
 */
public class ShowEventsActivity extends ListActivity {

    // Mock
    private List<User> mockUsersList;
    private Friend julien;
    private Friend robin;
    private Friend alain;
    private List<Event> mockEventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events);

        // Filling up mock stuff
        mockUsersList = new ArrayList<User>();
        julien = new Friend(1, "Julien Perrenoud");
        julien.setOnline(true);
        mockUsersList.add(julien);
        robin = new Friend(2, "Robin Genolet");
        robin.setOnline(false);
        mockUsersList.add(robin);
        alain = new Friend(2, "Alain Milliet");
        alain.setOnline(false);
        mockUsersList.add(alain);

        mockEventsList = new ArrayList<Event>();
        //Point p = new Point(0, 0);
        //Event e1 = new UserEvent("Swag party", 2, new GregorianCalendar(), new GregorianCalendar(2014, 12, 1),
        //        p);
        //Event e2 = new UserEvent("Ranked gangbang", 3, new GregorianCalendar(), new GregorianCalendar(2014, 11, 1),
        //        new Point(2, 2));
        //mockEventsList.add(e1);
        //mockEventsList.add(e2);

        // Create custom Adapter and pass it to the Activity
        EventsListItemAdapter adapter = new EventsListItemAdapter(this, mockEventsList);
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
