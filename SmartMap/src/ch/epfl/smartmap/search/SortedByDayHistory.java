package ch.epfl.smartmap.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.History;

/**
 * Represents an History of Search Queries sorting them by day.
 * 
 * @author jfperren
 */
public class SortedByDayHistory implements History {

    @SuppressWarnings("unused")
    private static final int MAX_HISTORY_ENTRY = 50;

    private final List<Queue<Displayable>> mHistoryEntries;
    private final List<Date> mDates;
    private int mSize;

    public SortedByDayHistory() {
        mHistoryEntries = new ArrayList<Queue<Displayable>>();
        mDates = new ArrayList<Date>();
        mSize = 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addEntry(Displayable item, Date date) {
        if (!mHistoryEntries.isEmpty() && (date.getDate() == mDates.get(0).getDate())) {
            // Same day as the first list of entries
            mHistoryEntries.get(0).add(item);
        } else {
            mHistoryEntries.add(0, new LinkedList<Displayable>(Arrays.asList(item)));
            mDates.add(0, date);
        }
        mSize++;
    }

    public boolean contains(Displayable item) {
        boolean contains = false;
        for (Queue<Displayable> e : mHistoryEntries) {
            contains = contains || e.contains(item);
        }
        return contains;
    }

    @Override
    public Date getDateForIndex(int index) {
        return mDates.get(index);
    }

    @Override
    public List<Displayable> getEntriesForIndex(int index) {
        return new ArrayList<Displayable>(mHistoryEntries.get(index));
    }

    @Override
    public boolean isEmpty() {
        return mSize == 0;
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.History#nbOfDates()
     */
    @Override
    public int nbOfDates() {
        return mDates.size();
    }
}