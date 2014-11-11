package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Represents an History of Search Queries sorting them by day.
 * 
 * @author jfperren
 */
public class SortedByDayHistory implements History {

    @SuppressWarnings("unused")
    private static final int MAX_HISTORY_ENTRY = 50;

    private List<Queue<Friend>> mHistoryEntries;
    private List<Date> mDates;
    private int mSize;

    public SortedByDayHistory() {
        mHistoryEntries = new ArrayList<Queue<Friend>>();
        mDates = new ArrayList<Date>();
        mSize = 0;
    }

    @SuppressWarnings("deprecation")
    public void addEntry(Friend friend, Date date) {
        if (!mHistoryEntries.isEmpty()
            && date.getDate() == mDates.get(0).getDate()) {
            // Same day as the first list of entries
            mHistoryEntries.get(0).add(friend);
        } else {
            mHistoryEntries.add(0,
                new LinkedList<Friend>(Arrays.asList(friend)));
            mDates.add(0, date);
        }

        mSize++;
    }

    public ArrayList<Friend> getEntriesForIndex(int index) {
        return new ArrayList<Friend>(mHistoryEntries.get(index));
    }

    public Date getDateForIndex(int index) {
        return mDates.get(index);
    }

    public boolean isEmpty() {
        return mSize == 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ch.epfl.smartmap.cache.History#nbOfDates()
     */
    @Override
    public int nbOfDates() {
        return mDates.size();
    }
}