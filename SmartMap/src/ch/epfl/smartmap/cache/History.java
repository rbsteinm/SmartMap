package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Date;

/**
 * Represents an history of search results.
 * 
 * @author jfperren
 */
public interface History {
    void addEntry(Displayable item, Date date);

    ArrayList<Displayable> getEntriesForIndex(int index);

    Date getDateForIndex(int index);

    int nbOfDates();

    boolean isEmpty();

    /**
     * Represents an entry in the history of searches : Tuple of a searchResult
     * and a Date
     * 
     * @author jfperren
     */
    public static class HistoryEntry {
        private final Displayable mItem;
        private final Date mDate;

        /**
         * Constructor
         * 
         * @param friend
         * @param date
         */
        public HistoryEntry(Displayable item, Date date) {
            mItem = item;
            mDate = date;
        }

        public Displayable getItem() {
            return mItem;
        }

        public Date getDate() {
            return mDate;
        }
    }
}
