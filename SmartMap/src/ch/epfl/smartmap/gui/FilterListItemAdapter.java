/**
 * 
 */
package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Filter;
import ch.epfl.smartmap.cache.ImmutableFilter;

/**
 * Customized adapter that displays a list of filters in a target activity
 * 
 * @author hugo-S
 */
public class FilterListItemAdapter extends ArrayAdapter<Filter> {

    private final Context mContext;
    private final List<Filter> mItemsArrayList;
    private final Cache mCache;

    public FilterListItemAdapter(Context context, List<Filter> filtersList) {
        super(context, R.layout.gui_friend_list_item, filtersList);
        mContext = context;
        mItemsArrayList = new ArrayList<Filter>(filtersList);
        mCache = ServiceContainer.getCache();
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View,
     * android.view.ViewGroup) callback function automatically called one time
     * for each user in the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Create inflater,get item to construct
        FilterViewHolder viewHolder;
        final Filter filter = mItemsArrayList.get(position);

        if (convertView == null) {
            LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gui_filter_list_item, parent, false);
            viewHolder = new FilterViewHolder();

            viewHolder.setFilterName((TextView) convertView.findViewById(R.id.activity_show_filters_name));
            viewHolder.setFilterId(filter.getId());
            viewHolder.setFollowSwitch((Switch) convertView
                .findViewById(R.id.activity_show_filters_follow_switch));
            viewHolder.setSubtitle((TextView) convertView.findViewById(R.id.activity_show_filters_subtitle));

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (FilterViewHolder) convertView.getTag();
        }

        if (filter != null) {
            viewHolder.getFilterName().setText(filter.getName());

            viewHolder.getSubtitle().setText(filter.getFriendIds().size() + " people inside this filter");

            viewHolder.getFollowSwitch().setTextOn("YES");
            viewHolder.getFollowSwitch().setTextOff("NO");
            viewHolder.getFollowSwitch().setChecked(filter.isActive());

            viewHolder.getFollowSwitch().setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mCache.putFilter(new ImmutableFilter(filter.getId(), filter.getName(), filter
                                .getFriendIds(), true));
                        } else {
                            mCache.putFilter(new ImmutableFilter(filter.getId(), filter.getName(), filter
                                .getFriendIds(), false));
                        }
                    }
                });
        }

        return convertView;
    }

    /**
     * @author hugo-S
     *         ViewHolder pattern implementation for smoother scrolling
     *         in lists populated by {@link ch.epfl.smartmap.gui.FilterListItemAdapter}
     */
    public static class FilterViewHolder {
        private TextView mFilterName;
        private long mFilterId;
        private Switch mFollowSwitch;
        private TextView mSubtitle;

        public long getFilterId() {
            return mFilterId;
        }

        public TextView getFilterName() {
            return mFilterName;
        }

        public Switch getFollowSwitch() {
            return mFollowSwitch;
        }

        public TextView getSubtitle() {
            return mSubtitle;
        }

        public void setFilterId(long filterId) {
            mFilterId = filterId;
        }

        public void setFilterName(TextView filterName) {
            mFilterName = filterName;
        }

        public void setFollowSwitch(Switch followSwitch) {
            mFollowSwitch = followSwitch;
        }

        public void setSubtitle(TextView subtitle) {
            mSubtitle = subtitle;
        }

    }

}
