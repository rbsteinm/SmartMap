package ch.epfl.smartmap.cache;

import java.util.Date;
import java.util.List;

/**
 * Represents an history of search results.
 * 
 * @author jfperren
 */
public interface History {
    void addEntry(Displayable item, Date date);

    Date getDateForIndex(int index);

    List<Displayable> getEntriesForIndex(int index);

    boolean isEmpty();

    int nbOfDates();

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

        public Date getDate() {
            return mDate;
        }

        public Displayable getItem() {
            return mItem;
        }
    }
}