package bredesh.medico.DAL;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import bredesh.medico.Notification.PartialVideoItem;
import bredesh.medico.Utils.Utils;

public class MedicoDB extends SQLiteOpenHelper {

    public enum KIND { Exercise, Medicine }
    private static final String DATABASE_NAME = "Medico";
    private static final String EXERCISE_STR = KIND.Exercise.toString();
    private static final String ALERTS_TABLE_NAME = "ALERTS";
    private static final String PERSONAL_INFO_TABLE_NAME = "PersonalInfo";
    private static final String MEDICINE_TABLE_NAME = "Medicine";
    private static final String LANG_TABLE_NAME = "Language";
    private static final int VERSION = 22;

    public static final int PhysioTherapy = 1;
    public static final int Drugs = 2;
    public static final int PeriodicChecks = 3;
    public static final int Memgraphy = 4;



    /*Alerts Table*/
    public static final String KEY_NAME = "name";
    public static final String KEY_KIND = "kind";
    public static final String KEY_TIME = "time";
    public static final String KEY_REPEATS = "repeats";
    public static final String KEY_REPETITION_TYPE = "repetition_type";
    public static final String URIVIDEO = "urivideo";
    public static final String KEY_ALERT_SOUND_URI = "uri_alert_sound";
    public static final String SUNDAY = "SUNDAY";
    public static final String MONDAY = "MONDAY";
    public static final String TUESDAY = "TUESDAY";
    public static final String WEDNESDAY = "WEDNESDAY";
    public static final String THURSDAY = "THURSDAY";
    public static final String FRIDAY = "FRIDAY";
    public static final String SATURDAY = "SATURDAY";
    public static final String ALERT_TODAY = "alerttoday";

    /*Personal Info Table*/
    public static final String KEY_ID = "id";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_POINTS = "points";

    /* Medicine Table */
    public static final String KEY_TYPE = "type";
    public static final String KEY_SPECIAL = "special";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_AMOUNT = "amount";

    /*Language Table*/
    public static final String LANG="LANG";

    // Point table
    private static final String POINTS_TABLE_NAME = "Points";

    public static final String KEY_POINTS_CONTEXT = "CONTEXT";
    public static final String KEY_POINTS_ITEM_ID = "ITEM_ID";
    public static final String KEY_POINTS_ITEM_NAME = "ITEM_NAME";
    public static final String KEY_POINTS_POINTS = "POINTS";
    public static final String KEY_POINTS_TIME = "TIME";
    public Context dbContext;

    public MedicoDB(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        dbContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createLang(db);
        createAlerts(db);
        createPersonalInfo(db);
        createMedicine(db);
        createPoints(db);
    }

    private void createMedicine(SQLiteDatabase db) {
        String CREATE_ALERTS_TABLE = "CREATE TABLE IF NOT EXISTS " + MEDICINE_TABLE_NAME +"( " +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_TYPE + " TEXT, "+
                KEY_SPECIAL + " TEXT, "+
                KEY_NOTES + " TEXT, "+
                KEY_AMOUNT+ " INTEGER)";
        // create table
        db.execSQL(CREATE_ALERTS_TABLE);

    }

    private void addAlertRepetitionType(SQLiteDatabase db)
    {
        String addRepetitionTypeSql = "ALTER TABLE " + ALERTS_TABLE_NAME + " ADD COLUMN " + KEY_REPETITION_TYPE + " TEXT";
        db.execSQL(addRepetitionTypeSql);
        ContentValues values = new ContentValues();
    }


    private void addAlertSoundUri(SQLiteDatabase db)
    {
        String addAlertSoundUriSql = "ALTER TABLE " + ALERTS_TABLE_NAME + " ADD COLUMN " + KEY_ALERT_SOUND_URI + " TEXT";
        db.execSQL(addAlertSoundUriSql);
    }


    private void addAlertKind(SQLiteDatabase db)
    {
        String addAlertKindSql = "ALTER TABLE " + ALERTS_TABLE_NAME + " ADD COLUMN " + KEY_KIND + " TEXT";
        db.execSQL(addAlertKindSql);
        ContentValues values = new ContentValues();
        values.put(KEY_KIND, EXERCISE_STR);
        db.update(ALERTS_TABLE_NAME, values, null, null);
    }

    private void createPoints(SQLiteDatabase db) {
        String CREATE_POINTS_TABLE = "CREATE TABLE IF NOT EXISTS " + POINTS_TABLE_NAME +"( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_POINTS_CONTEXT + " INTEGER, "+
                KEY_POINTS_ITEM_ID + " INTEGER, "+
                KEY_POINTS_ITEM_NAME + " TEXT, "+
                KEY_POINTS_TIME + " TEXT, "+
                KEY_POINTS_POINTS + " INTEGER) ";
        db.execSQL(CREATE_POINTS_TABLE);

    }


    private void createAlerts(SQLiteDatabase db) {
        String CREATE_ALERTS_TABLE = "CREATE TABLE " + ALERTS_TABLE_NAME +"( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_NAME + " TEXT, "+
                KEY_KIND + " TEXT, "+
                KEY_TIME + " TEXT, "+
                KEY_REPEATS+ " INTEGER, "+
                KEY_REPETITION_TYPE+ " TEXT, "+
                URIVIDEO + " TEXT, "+
                KEY_ALERT_SOUND_URI + " TEXT, "+
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
                KEY_POINTS + " INTEGER DEFAULT 0)";
        db.execSQL(CREATE_TABLE);
        ContentValues values = new ContentValues();
        values.put(KEY_FIRST_NAME,"");
        db.insert(PERSONAL_INFO_TABLE_NAME, null, values);

    }

    private void createLang(SQLiteDatabase db)
    {
        String CREATE_TABLE = "CREATE TABLE " + LANG_TABLE_NAME +"( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LANG+ " TEXT )";
        db.execSQL(CREATE_TABLE);
        ContentValues values = new ContentValues();
        values.put(LANG, "");
        db.insert(LANG_TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + ALERTS_TABLE_NAME + "");
            createAlerts(db);
        }
        // create fresh table
        if (oldVersion < 7) {
            db.execSQL("DROP TABLE IF EXISTS " + PERSONAL_INFO_TABLE_NAME + "");
            createPersonalInfo(db);
            db.execSQL("DROP TABLE IF EXISTS " + LANG_TABLE_NAME + "");
            createLang(db);
        }
        createPoints(db);
        createMedicine(db);
        if (oldVersion < 20)
            addAlertKind(db);
        if (oldVersion < 21)
            addAlertRepetitionType(db);
        if (oldVersion < 22)
            addAlertSoundUri(db);

    }

    public void DeleteAllAlerts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " +ALERTS_TABLE_NAME + "");
        createAlerts(db);
        db.close();
    }

    public void addAlert(String alert_name,KIND kind, String alert_time, int repeats, String repetition_type, String alert_uri, int[] days_to_alert, String alertSoundUriString){

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, alert_name);
        values.put(KEY_KIND, kind.toString());
        values.put(KEY_TIME, alert_time);
        values.put(KEY_REPEATS, repeats);
        values.put(KEY_REPETITION_TYPE, repetition_type);
        values.put(URIVIDEO, alert_uri);
        values.put(KEY_ALERT_SOUND_URI, alertSoundUriString);
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

    public int getLastID() {
        Cursor cursor;
        SQLiteDatabase database = this.getReadableDatabase();
        try {
            String sql = "SELECT MAX("+KEY_ID+") FROM " + ALERTS_TABLE_NAME + " WHERE " + KEY_NAME +" NOT LIKE '_TEMP__%'";
            cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
        } catch (SQLiteException e) { return -1; }
        return cursor.getInt(0);
    }



    public Cursor getAllAlertsByKind(KIND kind) {
        Cursor cursor;
        SQLiteDatabase database = this.getReadableDatabase();
        try {
            String sql = "SELECT * FROM " + ALERTS_TABLE_NAME + " WHERE " + KEY_NAME +" NOT LIKE '_TEMP__%'" +
                    " AND " + KEY_KIND + " LIKE '"+kind.toString()+"'";
            cursor = database.rawQuery(sql, null);
            cursor.moveToFirst();
        } catch (SQLiteException e) { createAlerts(database); return getAllAlertsByKind(kind); }
        return cursor;
    }

    public Exercise[] getAlerts()
    {
        Cursor alerts = getAllAlerts();
        ArrayList<Exercise> result = new ArrayList<Exercise>();
        int len = alerts.getCount();
        if (alerts != null && len > 0)
        {
            for (int i=0; i< len; i++)
            {
                Exercise newEx = new Exercise(alerts, this);
                result.add(newEx);
                alerts.moveToNext();
            }
            return (Exercise[]) result.toArray(new Exercise[result.size()]);
        }
        return null;
    }

    private String getDateString(Calendar calendar)
    {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return Integer.toString(year) + '-' + Utils.noToString(month,2) + '-' + Utils.noToString(day,2);
    }

    private String getStartOfdateString(Calendar calendar)
    {
        return "datetime('" + getDateString(calendar) + "')";
    }

    private String getEndOfdateString(Calendar calendar)
    {
        return "datetime('" + getDateString(calendar) + "','+1 day','-0.001 seconds')";
    }


    public int getTotalPointsByDates(Calendar startDate, Calendar endDate) {
        Cursor cursor;
        SQLiteDatabase database = this.getReadableDatabase();
        try {
            String sql = "SELECT SUM(" + KEY_POINTS_POINTS + ") FROM " + POINTS_TABLE_NAME + " WHERE " + KEY_POINTS_TIME +" between ";
            sql += getStartOfdateString(startDate);
            sql += " and ";
            sql += getEndOfdateString(endDate);
            cursor = database.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() == 1) {
                cursor.moveToFirst();
                return cursor.getInt(0);
            }
            return 0;
        } catch (SQLiteException e) { createPoints(database); return getTotalPointsByDates(startDate, endDate); }
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
        String sql = "SELECT " + KEY_ID + ", " + KEY_NAME + ", " +  KEY_TIME +", " + KEY_REPEATS+", "+ KEY_REPETITION_TYPE + ", "+
                URIVIDEO+", "+ ALERT_TODAY +", "+ KEY_ALERT_SOUND_URI + " FROM " + ALERTS_TABLE_NAME + " WHERE " + day +" = 1";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        return cursor;
    }

    //same parameters as ADD NEW ALERT (WITHOUT VIDEO/IMAGE URI) BUT WITH ROWID. WE CAN GET IT FROM THE CURSOR OF THE CLICKED ITEM
    public void updateRow(int rowID, String alert_name, String alert_time, int repeats, String repetition_type, String videoUri, int[] days_to_alert, String alertSoundUriString){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, alert_name);
        values.put(KEY_TIME, alert_time);
        values.put(KEY_REPEATS, repeats);
        values.put(KEY_REPETITION_TYPE, repetition_type);
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
        values.put(KEY_ALERT_SOUND_URI, alertSoundUriString);
        db.update(ALERTS_TABLE_NAME, values, KEY_ID+"="+rowID, null);
    }

    //deleting rowID
    public boolean deleteRow(int rowID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return (db.delete(ALERTS_TABLE_NAME, KEY_ID+"="+rowID, null) >0) &&
                (db.delete(MEDICINE_TABLE_NAME, KEY_ID+"="+rowID, null) >0);
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
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if(cursor.getCount() != 1) return null;

        String kind = cursor.getString(cursor.getColumnIndex(KEY_KIND));
        String videoUri = cursor.getString(cursor.getColumnIndex(URIVIDEO));
        Uri uri = videoUri != null? Uri.parse(videoUri) : null;

        v = new PartialVideoItem(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                uri,
                cursor.getInt(cursor.getColumnIndex(KEY_REPEATS)),
                cursor.getString(cursor.getColumnIndex(KEY_REPETITION_TYPE)),
                (kind.equals(KIND.Exercise.toString())? KIND.Exercise : KIND.Medicine),
                cursor.getString(cursor.getColumnIndex(KEY_ALERT_SOUND_URI)));
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
        db.update(PERSONAL_INFO_TABLE_NAME, // table
                values, null, null); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public void setLang(String language){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(LANG, language);

        // 3. insert
        db.update(LANG_TABLE_NAME, // table
                values, null, null); // key/value -> keys = column names/ values = column values

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
        db.update(PERSONAL_INFO_TABLE_NAME, // table
                values, null, null); // key/value -> keys = column names/ values = column values

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
        db.update(PERSONAL_INFO_TABLE_NAME, // table
                values, null, null); // key/value -> keys = column names/ values = column values

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
        db.update(PERSONAL_INFO_TABLE_NAME, // table
                values, null, null); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public  String getFirstName(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + PERSONAL_INFO_TABLE_NAME;
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        if(cursor.getCount() != 1) return null;

        return cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME));
    }

    public  String getLang(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + LANG_TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        if(cursor.getCount() != 1) return null;

        return cursor.getString(cursor.getColumnIndex(LANG));
    }


    public String getLastName(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + KEY_LAST_NAME + " FROM " + PERSONAL_INFO_TABLE_NAME;
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        if(cursor.getCount() != 1) return null;

        return cursor.getString(cursor.getColumnIndex(KEY_LAST_NAME));
    }

    public String getEmail(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + KEY_EMAIL + " FROM " + PERSONAL_INFO_TABLE_NAME;
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        if(cursor.getCount() != 1) return null;

        return cursor.getString(cursor.getColumnIndex(KEY_EMAIL));
    }

    public int getPoints(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT " + KEY_POINTS + " FROM " + PERSONAL_INFO_TABLE_NAME;
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        if(cursor.getCount() != 1) return -1;

        return cursor.getInt(cursor.getColumnIndex(KEY_POINTS));
    }

    public void addPoints(int context, int itemId, String itemName, int points_to_add){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_POINTS_CONTEXT, context);
        values.put(KEY_POINTS_ITEM_ID, itemId);
        values.put(KEY_POINTS_ITEM_NAME, itemName);
        values.put(KEY_POINTS_POINTS, points_to_add);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        values.put(KEY_POINTS_TIME, dateFormat.format(date));

        db.insert(POINTS_TABLE_NAME, null, values);
    }





/*
                  _   _       _                                   _
                 | | (_)     (_)                             _   (_)
   ____   ____ _ | |_  ____ _ ____   ____     ___  ____ ____| |_  _  ___  ____
  |    \ / _  ) || | |/ ___) |  _ \ / _  )   /___)/ _  ) ___)  _)| |/ _ \|  _ \
  | | | ( (/ ( (_| | ( (___| | | | ( (/ /   |___ ( (/ ( (___| |__| | |_| | | | |
  |_|_|_|\____)____|_|\____)_|_| |_|\____)  (___/ \____)____)\___)_|\___/|_| |_|

*/
    public void addMedicine(String type, String special, String notes, int amount)
    {
        int id = getLastID();
        if(id == -1) return;

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_TYPE, type);
        values.put(KEY_SPECIAL, special);
        values.put(KEY_NOTES, notes);
        values.put(KEY_AMOUNT, amount);
        // 3. insert
        db.insert(MEDICINE_TABLE_NAME, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public void updateMedicine(int rowID, String type, String special, String notes, int amount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, type);
        values.put(KEY_SPECIAL, special);
        values.put(KEY_NOTES, notes);
        values.put(KEY_AMOUNT, amount);
        db.update(MEDICINE_TABLE_NAME, values, KEY_ID+"="+rowID, null);
        db.close();
    }

    public Cursor getMedicineByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + MEDICINE_TABLE_NAME + " WHERE " + KEY_ID +" = "+id;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        return cursor;
    }

    public KIND getKindByID(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT "+KEY_KIND+" FROM " + ALERTS_TABLE_NAME + " WHERE " + KEY_ID +" = "+id;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String kind = cursor.getString(0);
        cursor.close();
        db.close();
        if(kind.equals(KIND.Exercise.toString())) return KIND.Exercise;
        else if(kind.equals(KIND.Medicine.toString())) return KIND.Medicine;
        return null;
    }

    public void clearInfo() {
        setFirstName("");
        setLastName("");
        setLastName("");
    }
}
