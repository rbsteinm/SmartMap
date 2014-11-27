package ch.epfl.smartmap.servercom;

import android.os.AsyncTask;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.User;

/**
 * A class to retrieve the name of the event creator from his id
 * 
 * @author marion-S
 */
public class EventCreatorNameRetriever {
	private final Event mEvent;

	public EventCreatorNameRetriever(Event event) {
		// TODO Constructeur de copie pour Event?
		mEvent = event;
	}

	/**
	 * Executes the AsyncTask
	 * 
	 * @throws SmartMapClientException
	 *             if the retrieved name is null, meaning that the server request failed n the AsynTask
	 */
	public void setEventCreatorName() throws SmartMapClientException {
		new SetEventCreatorName().execute(mEvent.getCreator());
		if (mEvent.getCreatorName() == null) {
			throw new SmartMapClientException("Network error");
		}
	}

	/**
	 * AsyncTask to retrieve the event creator name by executing the request getUserInfo. doInBackGround
	 * returns null if there was an error while executing getUserInfo
	 * 
	 * @author marion-S
	 */
	private class SetEventCreatorName extends AsyncTask<Long, Void, String> {

		@Override
		protected String doInBackground(Long... params) {
			User user = null;
			try {
				user = NetworkSmartMapClient.getInstance().getUserInfo(params[0]);
			} catch (SmartMapClientException e) {
				return null;
			}
			return user.getName();

		}

		@Override
		protected void onPostExecute(String name) {
			mEvent.setCreatorName(name);
		}

	}

}