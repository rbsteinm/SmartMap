package ch.epfl.smartmap.cache;

import java.util.ArrayList;
import java.util.Date;

/**
 * Represents an history of search results.
 * 
 * @author jfperren
 */
public interface History {
    /**
     * Represents an entry in the history of searches : Tuple of a searchResult
     * and a Date
     * 
     * @author jfperren
     */
    public static class HistoryEntry {
        private final Friend mFriend;
        private final Date mDate;

        /**
         * Constructor
         * 
         * @param friend
         * @param date
         */
        public HistoryEntry(Friend friend, Date date) {
            mFriend = friend;
            mDate = date;
        }

        public Date getDate() {
            return mDate;
        }

        public Friend getFriend() {
            return mFriend;
        }
    }

    void addEntry(Friend friend, Date date);

    Date getDateForIndex(int index);

    ArrayList<Friend> getEntriesForIndex(int index);

    boolean isEmpty();

    int nbOfDates();
}
