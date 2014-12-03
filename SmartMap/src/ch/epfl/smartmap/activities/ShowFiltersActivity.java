package ch.epfl.smartmap.activities;

import java.util.List;

import android.app.ListActivity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Filter;
import ch.epfl.smartmap.cache.MockDB;
import ch.epfl.smartmap.gui.FilterListItemAdapter;

/**
 * An Activity that displays the different filters
 * 
 * @author hugo-S
 */
public class ShowFiltersActivity extends ListActivity {

    private List<Filter> mFilterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_show_filters);

        this.getActionBar().setBackgroundDrawable(
            new ColorDrawable(this.getResources().getColor(R.color.main_blue)));

        // mock stuff, filter list should be taken from the cache (or database?)
        MockDB.fillFilters();
        mFilterList = MockDB.FILTER_LIST;

        this.setListAdapter(new FilterListItemAdapter(this.getApplicationContext(), mFilterList));
    }

    // @Override
    // public void onListItemClick(ListView listView, View view, int position, long id) {
    // super.onListItemClick(listView, view, position, id);
    // // TODO
    // }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.show_filters, menu);
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
