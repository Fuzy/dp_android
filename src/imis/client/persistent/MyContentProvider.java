package imis.client.persistent;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import imis.client.AppConsts;
import imis.client.model.Employee;
import imis.client.model.Event;
import imis.client.model.Record;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Contains methods for accessing to persistent storage (SQLite database).
 */
public class MyContentProvider extends ContentProvider {
    private static final String TAG = MyContentProvider.class.getSimpleName();

    private MyDatabaseHelper database;
    private static final int EVENTS = 1;
    private static final int EVENT_ID = 2;
    private static final int RECORDS = 3;
    private static final int RECORD_ID = 4;
    private static final int EMPLOYEES = 5;
    private static final int EMPLOYEE_ID = 6;

    private static final String TABLE_EVENTS = MyDatabaseHelper.TABLE_EVENTS;
    private static final String TABLE_RECORDS = MyDatabaseHelper.TABLE_RECORDS;
    private static final String TABLE_EMPLOYEES = MyDatabaseHelper.TABLE_EMPLOYEES;
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AppConsts.AUTHORITY1, TABLE_EVENTS, EVENTS);
        sURIMatcher.addURI(AppConsts.AUTHORITY1, TABLE_RECORDS, RECORDS);
        sURIMatcher.addURI(AppConsts.AUTHORITY1, TABLE_EVENTS + "/#", EVENT_ID);
        sURIMatcher.addURI(AppConsts.AUTHORITY1, TABLE_RECORDS + "/#", RECORD_ID);

        sURIMatcher.addURI(AppConsts.AUTHORITY2, TABLE_EMPLOYEES, EMPLOYEES);
        sURIMatcher.addURI(AppConsts.AUTHORITY2, TABLE_EMPLOYEES + "/#", EMPLOYEE_ID);

        sURIMatcher.addURI(AppConsts.AUTHORITY3, TABLE_EMPLOYEES, EMPLOYEES);
        sURIMatcher.addURI(AppConsts.AUTHORITY3, TABLE_EMPLOYEES + "/#", EMPLOYEE_ID);
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        Log.d(TAG, "applyBatch()");
        ContentProviderResult[] result = new ContentProviderResult[operations
                .size()];
        int i = 0;
        // Opens the database object in "write" mode.
        SQLiteDatabase db = database.getWritableDatabase();
        // Begin a transaction
        db.beginTransaction();
        try {
            for (ContentProviderOperation operation : operations) {
                // Chain the result for back references
                result[i++] = operation.apply(this, result, i);
            }

            db.setTransactionSuccessful();
        } catch (OperationApplicationException e) {
            Log.d(TAG, "batch failed: " + e.getLocalizedMessage());
        } finally {
            db.endTransaction();
        }
        Log.d(TAG, "applyBatch() result[0] " + result[0]);
        return result;
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate()");
        database = new MyDatabaseHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete() uri " + uri);
        int uriType = sURIMatcher.match(uri);
        int rowsDeleted = 0;
        String id;
        SQLiteDatabase sqlDB = database.getWritableDatabase();

        switch (uriType) {
            case EVENTS:
                rowsDeleted = sqlDB.delete(TABLE_EVENTS, selection, selectionArgs);
                break;
            case RECORDS:
                rowsDeleted = sqlDB.delete(TABLE_RECORDS, selection, selectionArgs);
                break;
            case EMPLOYEES:
                rowsDeleted = sqlDB.delete(TABLE_EMPLOYEES, selection, selectionArgs);
                break;
            case EVENT_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = sqlDB.delete(TABLE_EVENTS, EventManager.EventQuery.SELECTION_ID, new String[]{id});
                break;
            case EMPLOYEE_ID:
                id = uri.getLastPathSegment();
                rowsDeleted = sqlDB.delete(TABLE_EMPLOYEES, EventManager.EventQuery.SELECTION_ID, new String[]{id});
                break;
            default:
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(TAG, "delete() rowsDeleted " + rowsDeleted);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert()" + "uri = [" + uri + "], values = [" + values + "]");
        int uriType = sURIMatcher.match(uri);

        long id = 0;
        // ziska odkaz na databazi
        SQLiteDatabase sqlDB = database.getWritableDatabase();

        switch (uriType) {
            case EVENTS:
                // muze vlozit jen 1 zaznam
                id = sqlDB.insert(TABLE_EVENTS, null, values);
                break;
            case EMPLOYEES:
                id = sqlDB.insert(TABLE_EMPLOYEES, null, values);
                break;
            case RECORDS:
                id = sqlDB.insert(TABLE_RECORDS, null, values);
            default:
                break;
        }

        uri = Uri.withAppendedPath(uri, String.valueOf(id));
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(TAG, "insert() uri " + uri);
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Log.d(TAG, "uri = [" + uri + "], projection = [" + projection + "], " +
                "selection = [" + selection + "], selectionArgs = [" + Arrays.toString(selectionArgs) + "], " +
                "sortOrder = [" + sortOrder + "]");
        Log.d(TAG, "query() uri " + uri);
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();


        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case EVENTS:
                // vsechny radky tabulky
                queryBuilder.setTables(TABLE_EVENTS);
                break;
            case RECORDS:
                // vsechny radky tabulky
                queryBuilder.setTables(TABLE_RECORDS);
                break;
            case EMPLOYEES:
                queryBuilder.setTables(TABLE_EMPLOYEES);
                break;
            default:
                break;
        }

        SQLiteDatabase db = database.getWritableDatabase();

        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null,
                sortOrder);
        Log.d(TAG, "query() cursor size " + cursor.getCount());

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update uri " + uri);
        int rowsUpdated = 0;


        int uriType = sURIMatcher.match(uri);
        String id;
        SQLiteDatabase sqlDB = database.getWritableDatabase();

        switch (uriType) {
            case EVENTS:
                rowsUpdated = sqlDB.update(TABLE_EVENTS, values, selection, selectionArgs);
                break;
            case RECORDS:
                rowsUpdated = sqlDB.update(TABLE_RECORDS, values, selection, selectionArgs);
                break;
            case RECORD_ID:
                id = uri.getLastPathSegment();
                rowsUpdated = sqlDB.update(TABLE_RECORDS, values, Record.COL_ID + "=" + id, null);
                break;
            case EMPLOYEE_ID:
                id = uri.getLastPathSegment();
                rowsUpdated = sqlDB.update(TABLE_EMPLOYEES, values, Employee.COL_ID + "=" + id, null);
                break;
            case EMPLOYEES:
                rowsUpdated = sqlDB.update(TABLE_EMPLOYEES, values, selection, selectionArgs);
                if (rowsUpdated > 0) uri = Uri.withAppendedPath(uri, selectionArgs[0]);
                break;
            case EVENT_ID:
                id = uri.getLastPathSegment();
                rowsUpdated = sqlDB.update(TABLE_EVENTS, values, Event.COL_ID + "=" + id, null);
                break;
            default:
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

}
