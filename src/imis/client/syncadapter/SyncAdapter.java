package imis.client.syncadapter;

import imis.client.model.Event;
import imis.client.network.NetworkUtilities;
import imis.client.persistent.EventManager;

import java.util.List;

import org.apache.http.HttpStatus;

import com.google.gson.JsonObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
  private static final String TAG = "SyncAdapter";
  private static final String SYNC_MARKER_KEY = "mkz.sync.app.marker";
  private static final String AUTH_TOKEN = "qwerty";
  private String text = "Synchronizace nebyla provedena";

  private final AccountManager accountManager;

  private final Context context;

  public SyncAdapter(Context context, boolean autoInitialize) {
    super(context, autoInitialize);
    Log.d(TAG, "SyncAdapter()");
    this.context = context;
    accountManager = AccountManager.get(context);
  }

  @Override
  public void onPerformSync(Account account, Bundle extras, String authority,
      ContentProviderClient provider, SyncResult syncResult) {
    Log.d(TAG, "onPerformSync()");

    // long lastSyncMarker = getServerSyncMarker(account);
    int httpCode = -1;
    // ziska vsechny lokalni zmeny
    List<Event> dirtyEvents = EventManager.getDirtyEvents(context);
    for (Event event : dirtyEvents) {
      if (event.isDeleted()) {
        // mazani
        httpCode = NetworkUtilities.deleteEvent(event.getServer_id());
        if (httpCode == HttpStatus.SC_OK) {// lepsi kod OK
          EventManager.deleteEvent(context, event.get_id());
        }
        else {
          // TODO neuspech
        }
      }
      else if (event.hasServer_id()) {
        // update
        httpCode = NetworkUtilities.updateEvent(event);
        if (httpCode == HttpStatus.SC_ACCEPTED) {
          Log.d(TAG, "onPerformSync() update ok");
        }
        else {
          // TODO neuspech
        }
      }

      else if (event.hasServer_id() == false) {
        // pridani nove udalosti
        httpCode = NetworkUtilities.createEvent(event);
        if (httpCode == HttpStatus.SC_CREATED) {
          EventManager.updateEventServerId(context, event.get_id(), event.getServer_id());
        }
        else {
          // TODO neuspech
        }
      }

      /*
       * else { // odesle novy nebo zmeneny httpCode =
       * NetworkUtilities.createOrUpdateEvent(event); if (httpCode ==
       * HttpStatus.SC_NO_CONTENT) {
       * 
       * } else { // TODO neuspech } }
       */
      /*
       * else if (event.hasServer_id()) { }
       * 
       * } else if (event.hasServer_id() == false) { //Log.d(TAG,
       * "onPerformSync() event.hasServer_id() " + event.hasServer_id()); }
       */
    }
    // Log.d(TAG, "onPerformSync() dirtyEvents: " + dirtyEvents);
    Log.d(TAG, "onPerformSync() end");
    // List<JsonObject> serverEvents;

    // serverEvents = NetworkUtilities.getEvents();

    // EventManager.updateEvents(context, serverEvents, lastSyncMarker);

  }

  /**
   * This helper function fetches the last known high-water-mark we received
   * from the server - or 0 if we've never synced.
   * 
   * @param account
   *          the account we're syncing
   * @return the change high-water-mark
   */
  private long getServerSyncMarker(Account account) {
    Log.d(TAG, "getServerSyncMarker()");
    String markerString = accountManager.getUserData(account, SYNC_MARKER_KEY);
    if (!TextUtils.isEmpty(markerString)) {
      return Long.parseLong(markerString);
    }
    return 0;
  }

  /**
   * Save off the high-water-mark we receive back from the server.
   * 
   * @param account
   *          The account we're syncing
   * @param marker
   *          The high-water-mark we want to save.
   */
  private void setServerSyncMarker(Account account, long marker) {
    Log.d(TAG, "setServerSyncMarker()");
    accountManager.setUserData(account, SYNC_MARKER_KEY, Long.toString(marker));
  }

}
