package fr.epicture.epicture.api.flickr.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FlickrSQLite extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "account";

    public static final String ACCOUNT_COL_DB_ID = "DB_ID";
    public static final int ACCOUNT_NUM_COL_DB_ID = 0;

    public static final String ACCOUNT_COL_FULLNAME = "FULLNAME";
    public static final int ACCOUNT_NUM_COL_FULLNAME = 1;

    public static final String ACCOUNT_COL_TOKEN = "TOKEN";
    public static final int ACCOUNT_NUM_COL_TOKEN = 2;

    public static final String ACCOUNT_COL_TOKEN_SECRET = "TOKENSECRET";
    public static final int ACCOUNT_NUM_COL_TOKEN_SECRET = 3;

    public static final String ACCOUNT_COL_NSID = "NSID";
    public static final int ACCOUNT_NUM_COL_NSID = 4;

    public static final String ACCOUNT_COL_USERNAME = "USERNAME";
    public static final int ACCOUNT_NUM_COL_USERNAME = 5;

    private static final String TODO_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + ACCOUNT_COL_DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ACCOUNT_COL_FULLNAME + " TEXT NOT NULL, "
            + ACCOUNT_COL_TOKEN + " TEXT NOT NULL, "
            + ACCOUNT_COL_TOKEN_SECRET + " TEXT NOT NULL, "
            + ACCOUNT_COL_NSID + " TEXT NOT NULL, "
            + ACCOUNT_COL_USERNAME + " TEXT NOT NULL);";

    public FlickrSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_NAME + ";");
        onCreate(db);
    }

}
