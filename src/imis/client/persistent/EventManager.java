package imis.client.persistent;

import imis.client.model.Event;
import imis.client.persistent.Consts.ColumnName;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class EventManager {
  private static final String TAG = "EventManager";

  public static long updateEvents(Context context, List<JsonObject> serverEvents,
      long lastSyncMarker) {
    Log.d(TAG, "updateEvents()");
    long currentSyncMarker = lastSyncMarker;

    for (JsonObject eventJson : serverEvents) {

      JsonElement syncEl = eventJson.get(Event.JSON_SYNC);
      long sync = (syncEl == null) ? -1 : syncEl.getAsLong();

      if (sync > currentSyncMarker) {
        // Pamatuje si cas nejnovejsi zmeny
        currentSyncMarker = sync;
      }

      Event event = Event.jsonToEvent(eventJson);
      addEvent(context, false, event);

    }

    return currentSyncMarker;
  }

  /**
   * @param context
   * @param dirty
   *          true - pokud pridano uzivatelem, false ziskano od serveru
   * @param event
   * @return
   */
  public static long addEvent(Context context, boolean dirty, Event event) {
    Log.d(TAG, "addEvent()");
    ContentValues values = event.getAsContentValues();
    values.put(ColumnName.COLUMN_DIRTY, dirty);
    ContentResolver resolver = context.getContentResolver();
    Uri uri = resolver.insert(DataQuery.CONTENT_URI, values);
    return Long.valueOf(uri.getLastPathSegment());
  }

  public static int deleteEvent(Context context, long id) {
    Log.d(TAG, "deleteEvent()");
    Uri uri = Uri.withAppendedPath(DataQuery.CONTENT_URI, String.valueOf(id));
    ContentResolver resolver = context.getContentResolver();
    return resolver.delete(uri, null, null);
  }
  
  public static int deleteAllEvents(Context context) {
    Log.d(TAG, "deleteAllEvents()");
    Uri uri = DataQuery.CONTENT_URI;
    ContentResolver resolver = context.getContentResolver();
    return resolver.delete(uri, null, null);
  }

  public static Event getEvent(Context context, long id) {
    Log.d(TAG, "getEvent()");
    ContentResolver resolver = context.getContentResolver();
    Cursor cursor = resolver.query(DataQuery.CONTENT_URI, DataQuery.PROJECTION,
        DataQuery.SELECTION, new String[] { String.valueOf(id) }, null);
    Event event = null;
    while (cursor.moveToNext()) {
      event = Event.cursorToEvent(cursor);
    }
    cursor.close();
    return event;
  }
  
  public static List<Event> getAllEvents(Context context) {
    Log.d(TAG, "getAllEvents()");
    ContentResolver resolver = context.getContentResolver();
    Cursor cursor = resolver.query(DataQuery.CONTENT_URI, DataQuery.PROJECTION, null, null, null);
    List<Event> events = new ArrayList<Event>();
    Event event = null;
    while (cursor.moveToNext()) {
      event = Event.cursorToEvent(cursor);
      events.add(event);
    }
    cursor.close();
    return events;
  }

  public static int markEventAsDeleted(Context context, long id) {
    Log.d(TAG, "markEventAsDeleted()");
    Uri uri = Uri.withAppendedPath(DataQuery.CONTENT_URI, String.valueOf(id));
    ContentResolver resolver = context.getContentResolver();
    ContentValues values = new ContentValues();
    values.put(ColumnName.COLUMN_DELETED, true);
    values.put(ColumnName.COLUMN_DIRTY, true);
    return resolver.update(uri, values, null, null);
  }

  public static int updateEvent(Context context, Event event) {
    Log.d(TAG, "updateEvent()");
    Uri uri = Uri.withAppendedPath(DataQuery.CONTENT_URI, String.valueOf(event.get_id()));
    ContentResolver resolver = context.getContentResolver();
    ContentValues values = event.getAsContentValues();// TODO pozor co vse
                                                      // aktual.
    values.put(Event.COL_DIRTY, true);
    return resolver.update(uri, values, null, null);
  }

  final public static class DataQuery {

    // uri zdroje dat
    public static final Uri CONTENT_URI = Uri.parse(Consts.SCHEME + Consts.AUTHORITY + "/"
        + EventDatabaseHelper.TABLE_EVENTS);

    // vybere vsechny sloupce
    public static final String[] PROJECTION = { ColumnName.COLUMN_ID, ColumnName.COLUMN_SERVER_ID,
        ColumnName.COLUMN_DIRTY, ColumnName.COLUMN_DELETED, ColumnName.COLUMN_ICP,
        ColumnName.COLUMN_DATUM, ColumnName.COLUMN_KOD_PO, ColumnName.COLUMN_DRUH,
        ColumnName.COLUMN_CAS, ColumnName.COLUMN_IC_OBS, ColumnName.COLUMN_TYP,
        ColumnName.COLUMN_DATUM_ZMENY, ColumnName.COLUMN_POZNAMKA };

    // vyber podle id ukolu
    public static final String SELECTION = ColumnName.COLUMN_ID + "=?";

  }

}