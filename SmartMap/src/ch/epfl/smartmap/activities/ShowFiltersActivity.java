package ch.epfl.smartmap.activities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Filter;
import ch.epfl.smartmap.cache.ImmutableFilter;
import ch.epfl.smartmap.gui.FilterListItemAdapter;

/**
 * An Activity that displays the different filters
 * 
 * @author hugo-S
 * @author rbsteinm
 */
public class ShowFiltersActivity extends ListActivity {

    private List<Filter> mFilterList;
    private Cache mCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_show_filters);

        this.getActionBar().setBackgroundDrawable(
            new ColorDrawable(this.getResources().getColor(R.color.main_blue)));
        mCache = ServiceContainer.getCache();
        mFilterList = new ArrayList<Filter>(mCache.getAllFilters());

        // mock stuff, filter list should be taken from the cache (or database?)
        // MockDB.fillFilters();
        // mFilterList = MockDB.FILTER_LIST;

        this.setListAdapter(new FilterListItemAdapter(this.getBaseContext(), mFilterList));
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {

        super.onResume();
        mFilterList = new ArrayList<Filter>(mCache.getAllFilters());
        this.setListAdapter(new FilterListItemAdapter(this.getBaseContext(), mFilterList));
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {

        super.onStart();
        mFilterList = new ArrayList<Filter>(mCache.getAllFilters());
        this.setListAdapter(new FilterListItemAdapter(this.getBaseContext(), mFilterList));
    }

    public void addNewFilterDialog(MenuItem item) {
        // inflate the alertDialog
        LayoutInflater inflater = this.getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.new_filter_alert_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New filter");
        builder.setView(alertLayout);

        // Add positive button
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText editText =
                    (EditText) alertLayout.findViewById(R.id.show_filters_alert_dialog_edittext);
                String filterName = editText.getText().toString();
                Long newFilterId =
                    mCache
                        .putFilter(new ImmutableFilter(Filter.NO_ID, filterName, new HashSet<Long>(), true));
                // Start a new instance of ModifyFilterActivity passing it the
                // new filter's name
                Intent intent = new Intent(ShowFiltersActivity.this, ModifyFilterActivity.class);
                intent.putExtra("FILTER", newFilterId);
                ShowFiltersActivity.this.startActivity(intent);
            }
        });

        // Add negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        // display the AlertDialog
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.show_filters, menu);
        return true;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        Filter filter = mFilterList.get(position);
        Intent intent = new Intent(this.getBaseContext(), ModifyFilterActivity.class);
        intent.putExtra("FILTER", filter.getId());
        ShowFiltersActivity.this.startActivity(intent);
        super.onListItemClick(listView, view, position, id);
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
