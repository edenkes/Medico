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
import android.net.Uri;

import bredesh.medico.Notification.PartialVideoItem;

public class LocalDBManager extends SQLiteOpenHelper{

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

    public static final String DATABASE_NAME = "Medico";
    private static final String ALERTS_TABLE_NAME = "ALERTS";
    private static final int VERSION = 2;
    public static final String KEY_ID = "id";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_POINTS = "points";

    private static final String PERSONAL_INFO_TABLE_NAME = "PersonalInfo";    

    public LocalDBManager(Context context) { super(context, DATABASE_NAME, null, VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAlerts(db);
        createPersonalInfo(db);
    }

    private void createAlerts(SQLiteDatabase db)
    {
        String CREATE_ALERTS_TABLE = "CREATE TABLE " + ALERTS_TABLE_NAME +"( " +
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

    private void createPersonalInfo(SQLiteDatabase db)
    {
        String CREATE_TABLE = "CREATE TABLE " + PERSONAL_INFO_TABLE_NAME +"( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_FIRST_NAME + " TEXT, " +
                KEY_LAST_NAME + " TEXT, " +
                KEY_EMAIL + " TEXT, " +
                KEY_POINTS + " INTEGER DEFAULT 0 )";
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " +ALERTS_TABLE_NAME + "");
        // create fresh table
        db.execSQL("DROP TABLE IF EXISTS " +PERSONAL_INFO_TABLE_NAME + "");
        // create fresh table
        this.onCreate(db);
    }

public void DeleteAllAlerts() {
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL("DROP TABLE IF EXISTS " +ALERTS_TABLE_NAME + "");
    createAlerts(db);
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
    db.insert(ALERTS_TABLE_NAME, // table
            null, //nullColumnHack
            values); // key/value -> keys = column names/ values = column values

    // 4. close
    db.close();
}

public Cursor getAllAlerts() {
    Cursor cursor;
    SQLiteDatabase database = this.getReadableDatabase();
    try {
        String sql = "SELECT * FROM " + ALERTS_TABLE_NAME + " WHERE " + KEY_NAME +" NOT LIKE '_TEMP__%'";
        cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
    } catch (SQLiteException e) { createAlerts(database); return getAllAlerts(); }
    return cursor;
}

public Cursor getAllTempAlerts() {
    Cursor cursor;
    SQLiteDatabase database = this.getReadableDatabase();
    try {
        String sql = "SELECT * FROM " + ALERTS_TABLE_NAME + " WHERE " + KEY_NAME +" LIKE '_TEMP__%'";
        cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
    } catch (SQLiteException e) { createAlerts(database); return getAllAlerts(); }
    return cursor;
}

public Cursor getAllAlertsByDay(String day) {
    SQLiteDatabase db = this.getReadableDatabase();
    String sql = "SELECT " + KEY_ID + ", " + KEY_NAME + ", " +  KEY_TIME +", " + KEY_REPEATS+", "+
            URIVIDEO+", "+ ALERT_TODAY + " FROM " + ALERTS_TABLE_NAME + " WHERE " + day +" = 1";
    Cursor cursor = db.rawQuery(sql, null);
    cursor.moveToFirst();
    return cursor;
}

//same parameters as ADD NEW ALERT (WITHOUT VIDEO/IMAGE URI) BUT WITH ROWID. WE CAN GET IT FROM THE CURSOR OF THE CLICKED ITEM
public void updateRow(int rowID, String alert_name, String alert_time, int repeats, String videoUri, int[] days_to_alert){
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
    if (videoUri != null)
        values.put(URIVIDEO, videoUri);
    db.update(ALERTS_TABLE_NAME, values, KEY_ID+"="+rowID, null);
}

//deleting rowID
public boolean deleteRow(int rowID)
{
    SQLiteDatabase db = this.getWritableDatabase();
    return db.delete(ALERTS_TABLE_NAME, KEY_ID+"="+rowID, null) >0;
}

public void updateAlertToday(int rowID, String time) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(ALERT_TODAY, time);
    db.update(ALERTS_TABLE_NAME, values, KEY_ID+"="+rowID, null);
}

public void allTodaysAlertReset(int rowID) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(ALERT_TODAY, "");
    db.update(ALERTS_TABLE_NAME, values, KEY_ID+"="+rowID, null);
}

    public PartialVideoItem getItemByID(int id)
    {
        PartialVideoItem v;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + ALERTS_TABLE_NAME + " WHERE " + KEY_ID +" = "+id;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if(cursor.getCount() != 1) return null;

        v = new PartialVideoItem(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                            Uri.parse(cursor.getString(cursor.getColumnIndex(URIVIDEO))),
                            cursor.getInt(cursor.getColumnIndex(KEY_REPEATS)));
        db.close();
        return v;
    }

    public void setFirstName(String firstName){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_FIRST_NAME, firstName);

        // 3. insert
        db.insert(PERSONAL_INFO_TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public void setLastName(String lastName){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_LAST_NAME, lastName);

        // 3. insert
        db.insert(PERSONAL_INFO_TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public void setEmail(String email){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, email);

        // 3. insert
        db.insert(PERSONAL_INFO_TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public void setPoints(int points){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_POINTS, points);

        // 3. insert
        db.insert(PERSONAL_INFO_TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public  String getFirstName(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + PERSONAL_INFO_TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();


        if(cursor.getCount() != 1) return null;
        String str = cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME));

        return str;
    }

    public Cursor getLastName(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + KEY_LAST_NAME + " FROM " + PERSONAL_INFO_TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getEmail(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + KEY_EMAIL + " FROM " + PERSONAL_INFO_TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        return cursor;
    }

    public Cursor getPoints(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + KEY_POINTS + " FROM " + PERSONAL_INFO_TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        return cursor;
    }    
}
