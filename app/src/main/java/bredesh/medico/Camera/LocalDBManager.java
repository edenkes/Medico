package bredesh.medico.Camera;

/**
 * Created by Omri on 07-Mar-17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDBManager extends SQLiteOpenHelper{

    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_TIME = "time";
    private static final String SUNDAY = "SUNDAY";
    private static final String MONDAY = "MONDAY";
    private static final String TUESDAY = "TUESDAY";
    private static final String WEDNESDAY = "WEDNESDAY";
    private static final String THURSDAY = "THURSDAY";
    private static final String FRIDAY = "FRIDAY";
    private static final String SATURDAY = "SATURDAY";

    private static final String DATABASE_NAME = "USER_ALERTS";
    private static final String TABLE_NAME = "ALERTS";
    private static final int VERSTION = 1;

    public LocalDBManager(Context context) { super(context, DATABASE_NAME, null, VERSTION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALERTS_TABLE = "CREATE TABLE " + TABLE_NAME +"( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_NAME + " TEXT, "+
                KEY_TIME + " TEXT, "+
                SUNDAY + " INTEGER, "+
                MONDAY + " INTEGER, "+
                TUESDAY + " INTEGER, "+
                WEDNESDAY + " INTEGER, "+
                THURSDAY + " INTEGER, "+
                FRIDAY + " INTEGER, "+
                SATURDAY + " INTEGER )";

        //days: 1 - should alert; 0 - otherwise

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

    public void addAlert(String alert_name, String alert_time, int[] days_to_alert){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, alert_name);
        values.put(KEY_TIME, alert_time);
        values.put(SUNDAY, days_to_alert[0]);
        values.put(MONDAY, days_to_alert[1]);
        values.put(TUESDAY, days_to_alert[2]);
        values.put(WEDNESDAY, days_to_alert[3]);
        values.put(THURSDAY, days_to_alert[4]);
        values.put(FRIDAY, days_to_alert[5]);
        values.put(SATURDAY, days_to_alert[6]);

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
        cursor=database.query(TABLE_NAME, new String[]{KEY_ID, KEY_NAME, KEY_TIME, SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY}, null, null, null, null, null);
        return cursor;
    }

    public Cursor getAllAlertsByDay(String day) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + KEY_ID + ", " + KEY_NAME + ", " +  KEY_TIME +" FROM " + TABLE_NAME + " WHERE " + day +" = 1";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        return cursor;
    }
}
