/**
 * 
 */
package ch.epfl.smartmap.background;

import java.util.NoSuchElementException;

import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.InvitationManager;
import ch.epfl.smartmap.database.DatabaseHelper;
import ch.epfl.smartmap.search.CachedOnlineSearchEngine;
import ch.epfl.smartmap.servercom.SmartMapClient;

/**
 * This class is a container for the different services used by the SmartMap
 * app. It's member must be manually set at the starting of the app.
 * It provides a common interface to get services and thus simplifies future
 * changes.
 * It also allows to switch the returned instances for behavior modification and
 * testing.
 * 
 * @author Pamoi
 */
public class ServiceContainer {
    private static SmartMapClient mNetworkClient;
    private static DatabaseHelper mDBHelper;
    private static Cache mCache;
    private static InvitationManager mInvitationManager;
    private static CachedOnlineSearchEngine mSearchEngine;
    private static SettingsManager mSettingsManager;

    /**
     * Get the network client service.
     * 
     * @return SmartMapClient
     */
    public static SmartMapClient getNetworkClient() {
        if (mNetworkClient == null) {
            throw new NoSuchElementException("Network client is not set.");
        }
        return mNetworkClient;
    }

    /**
     * Set the network client service.
     * 
     * @param client
     */
    public static void setNetworkClient(SmartMapClient client) {
        mNetworkClient = client;
    }

    /**
     * Get the database helper service.
     * 
     * @return DatabaseHelper
     */
    public static DatabaseHelper getDatabase() {
        if (mDBHelper == null) {
            throw new NoSuchElementException("Database Helper is not set.");
        }
        return mDBHelper;
    }

    /**
     * Set the database helper service.
     * 
     * @param db
     */
    public static void setDatabaseHelper(DatabaseHelper db) {
        mDBHelper = db;
    }

    /**
     * Get the cache service.
     * 
     * @return Cache
     */
    public static Cache getCache() {
        if (mCache == null) {
            throw new NoSuchElementException("Cache is not set.");
        }
        return mCache;
    }

    /**
     * Set the cache service.
     * 
     * @param cache
     */
    public static void setCache(Cache cache) {
        mCache = cache;
    }

    /**
     * Get the invitation manager service.
     * 
     * @return InvitationManager
     */
    public static InvitationManager getInvitationManager() {
        if (mInvitationManager == null) {
            throw new NoSuchElementException("InvitationManager is not set.");
        }
        return mInvitationManager;
    }

    /**
     * Set the invitation manager service.
     * 
     * @param im
     */
    public static void setInvitationManager(InvitationManager im) {
        mInvitationManager = im;
    }

    /**
     * Get the search engine service.
     * 
     * @return
     */
    public static CachedOnlineSearchEngine getSearchEngine() {
        if (mSearchEngine == null) {
            throw new NoSuchElementException("Search engine is not set.");
        }
        return mSearchEngine;
    }

    /**
     * Set the search engine service.
     * 
     * @param se
     */
    public static void setSearchEngine(CachedOnlineSearchEngine se) {
        mSearchEngine = se;
    }

    /**
     * Get the settings manager service.
     * 
     * @return
     */
    public static SettingsManager getSettingsManager() {
        if (mSettingsManager == null) {
            throw new NoSuchElementException("Settings manager is not set.");
        }
        return mSettingsManager;
    }

    /**
     * Set the settings manager service.
     * 
     * @param sm
     */
    public static void setSettingsManager(SettingsManager sm) {
        mSettingsManager = sm;
    }
}
