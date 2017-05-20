package bredesh.medico.Camera;

/**
 * Created by Omri on 07-Mar-17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDBManager extends SQLiteOpenHelper{

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_TIME = "time";
    public static final String KEY_REPEATS = "repeats";
    public static final String URIVIDEO = "urivideo";
    public static final String SUNDAY = "SUNDAY";
    public static final String MONDAY = "MONDAY";
    public static final String TUESDAY = "TUESDAY";
    public static final String WEDNESDAY = "WEDNESDAY";
    public static final String THURSDAY = "THURSDAY";
    public static final String FRIDAY = "FRIDAY";
    public static final String SATURDAY = "SATURDAY";
    public static final String ALERT_TODAY = "alerttoday";

    private static final String DATABASE_NAME = "USER_ALERTS";
    private static final String TABLE_NAME = "ALERTS";
    private static final int VERSION = 1;

    public LocalDBManager(Context context) { super(context, DATABASE_NAME, null, VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALERTS_TABLE = "CREATE TABLE " + TABLE_NAME +"( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_NAME + " TEXT, "+
                KEY_TIME + " TEXT, "+
                KEY_REPEATS+ " INTEGER, "+
                URIVIDEO + " TEXT, "+
                SUNDAY + " INTEGER, "+
                MONDAY + " INTEGER, "+
                TUESDAY + " INTEGER, "+
                WEDNESDAY + " INTEGER, "+
                THURSDAY + " INTEGER, "+
                FRIDAY + " INTEGER, "+
                SATURDAY + " INTEGER, "+
                ALERT_TODAY + " TEXT )";

        //days: 1 - should alert; 0 - otherwise
        //ALERT_TODAY 0 - should alert 1 already alerted
        // create table
        db.execSQL(CREATE_ALERTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME + "");
        // create fresh table
        this.onCreate(db);
    }

    public void DeleteAllAlerts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME + "");
        onCreate(db);
        db.close();
    }

    public void addAlert(String alert_name, String alert_time, int repeats, String alert_uri, int[] days_to_alert){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, alert_name);
        values.put(KEY_TIME, alert_time);
        values.put(KEY_REPEATS, repeats);
        values.put(URIVIDEO, alert_uri);
        values.put(SUNDAY, days_to_alert[0]);
        values.put(MONDAY, days_to_alert[1]);
        values.put(TUESDAY, days_to_alert[2]);
        values.put(WEDNESDAY, days_to_alert[3]);
        values.put(THURSDAY, days_to_alert[4]);
        values.put(FRIDAY, days_to_alert[5]);
        values.put(SATURDAY, days_to_alert[6]);
        values.put(ALERT_TODAY, "");

        // 3. insert
        db.insert(TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public Cursor getAllAlerts() {
        Cursor cursor;
        SQLiteDatabase database = this.getReadableDatabase();
        try {
            cursor = database.query(TABLE_NAME, new String[]{KEY_ID, KEY_NAME, KEY_TIME, KEY_REPEATS, URIVIDEO
                    , SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY,ALERT_TODAY}, null, null, null, null, null);
        } catch (SQLiteException e) { onCreate(database); return getAllAlerts(); }
        return cursor;
    }

    public Cursor getAllAlertsByDay(String day) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + KEY_ID + ", " + KEY_NAME + ", " +  KEY_TIME +", " + KEY_REPEATS+", "+
                URIVIDEO+", "+ ALERT_TODAY + " FROM " + TABLE_NAME + " WHERE " + day +" = 1";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        return cursor;
    }

    //same parameters as ADD NEW ALERT (WITHOUT VIDEO/IMAGE URI) BUT WITH ROWID. WE CAN GET IT FROM THE CURSOR OF THE CLICKED ITEM
    public void updateRow(int rowID, String alert_name, String alert_time, int repeats, int[] days_to_alert){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, alert_name);
        values.put(KEY_TIME, alert_time);
        values.put(KEY_REPEATS, repeats);
        values.put(SUNDAY, days_to_alert[0]);
        values.put(MONDAY, days_to_alert[1]);
        values.put(TUESDAY, days_to_alert[2]);
        values.put(WEDNESDAY, days_to_alert[3]);
        values.put(THURSDAY, days_to_alert[4]);
        values.put(FRIDAY, days_to_alert[5]);
        values.put(SATURDAY, days_to_alert[6]);
        values.put(ALERT_TODAY, "");
        db.update(TABLE_NAME, values, KEY_ID+"="+rowID, null);
    }

    //deleting rowID
    public boolean deleteRow(int rowID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, KEY_ID+"="+rowID, null) >0;
    }

    public void updateAlertToday(int rowID, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALERT_TODAY, time);
        db.update(TABLE_NAME, values, KEY_ID+"="+rowID, null);
    }

    public void allTodaysAlertReset(int rowID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALERT_TODAY, "");
        db.update(TABLE_NAME, values, KEY_ID+"="+rowID, null);
    }
}
