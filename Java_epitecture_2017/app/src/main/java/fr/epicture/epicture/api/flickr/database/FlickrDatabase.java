package fr.epicture.epicture.api.flickr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fr.epicture.epicture.api.flickr.FlickrAccount;
import fr.epicture.epicture.api.flickr.modele.TokenAccess;

public class FlickrDatabase {

    private static final int VERSION_BDD = 1;
    public static final String BDD_NAME = "flickr_account.db";

    private Context context;
    private SQLiteDatabase database;
    private FlickrSQLite mySQLite;

    public FlickrDatabase(Context context) {
        mySQLite = new FlickrSQLite(context, BDD_NAME, null, VERSION_BDD);
        this.context = context;
    }

    public void open() {
        database = mySQLite.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    public FlickrAccount insertAccount(FlickrAccount item) {
        List<FlickrAccount> result = getFlickrAccountResult("SELECT * FROM " + FlickrSQLite.TABLE_NAME
            + " WHERE " + FlickrSQLite.ACCOUNT_COL_NSID + " = '" + item.nsid + "'");

        if (result.size() == 0) {
            ContentValues values = new ContentValues();
            values.put(FlickrSQLite.ACCOUNT_COL_FULLNAME, item.fullname);
            values.put(FlickrSQLite.ACCOUNT_COL_TOKEN, item.token);
            values.put(FlickrSQLite.ACCOUNT_COL_TOKEN_SECRET, item.tokenSecret);
            values.put(FlickrSQLite.ACCOUNT_COL_NSID, item.nsid);
            values.put(FlickrSQLite.ACCOUNT_COL_USERNAME, item.getUsername());
            item.dbid = (int) database.insert(FlickrSQLite.TABLE_NAME, null, values);
        }
        return (item);
    }

    public int updateAccount(FlickrAccount item) {
        ContentValues values = new ContentValues();
        values.put(FlickrSQLite.ACCOUNT_COL_FULLNAME, item.fullname);
        values.put(FlickrSQLite.ACCOUNT_COL_TOKEN, item.token);
        values.put(FlickrSQLite.ACCOUNT_COL_TOKEN_SECRET, item.tokenSecret);
        values.put(FlickrSQLite.ACCOUNT_COL_NSID, item.nsid);
        values.put(FlickrSQLite.ACCOUNT_COL_USERNAME, item.getUsername());

        return (database.update(FlickrSQLite.TABLE_NAME, values, FlickrSQLite.ACCOUNT_COL_DB_ID + " = " + item.dbid, null));
    }

    public int deleteAccount(int dbid) {
        return (database.delete(FlickrSQLite.TABLE_NAME, FlickrSQLite.ACCOUNT_COL_DB_ID + " = " + dbid, null));
    }

    public List<FlickrAccount> getAccounts() {
        return (getFlickrAccountResult("SELECT * FROM " + FlickrSQLite.TABLE_NAME));
    }

    private List<FlickrAccount> getFlickrAccountResult(String query) {
        List<FlickrAccount> results = new ArrayList<>();
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                results.add(cursorToTokenAccess(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return (results);
    }

    private FlickrAccount cursorToTokenAccess(Cursor cursor) {
        TokenAccess item = new TokenAccess();
        item.dbid = cursor.getInt(FlickrSQLite.ACCOUNT_NUM_COL_DB_ID);
        item.fullname = cursor.getString(FlickrSQLite.ACCOUNT_NUM_COL_FULLNAME);
        item.token = cursor.getString(FlickrSQLite.ACCOUNT_NUM_COL_TOKEN);
        item.tokenSecret = cursor.getString(FlickrSQLite.ACCOUNT_NUM_COL_TOKEN_SECRET);
        item.nsid = cursor.getString(FlickrSQLite.ACCOUNT_NUM_COL_NSID);
        item.username = cursor.getString(FlickrSQLite.ACCOUNT_NUM_COL_USERNAME);
        return (new FlickrAccount(item));
    }
}
