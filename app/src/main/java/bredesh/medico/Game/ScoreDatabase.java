package bredesh.medico.Game;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import bredesh.medico.Camera.LocalDBManager;

/**
 * Created by Omri on 22/05/2017.
 */

public class ScoreDatabase extends SQLiteOpenHelper {

    public static final String KEY_ID = "id";
    public static final String KEY_SCORE = "current_score";
    public static final String KEY_SCORE_MAX = "max_score";


    private static final String TABLE_NAME = "SCORES";

    private static final String DATABASE_NAME = "Medico";
    private static final int VERSION = 1;




    public ScoreDatabase(Context context) { super(context, DATABASE_NAME, null, VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALERTS_TABLE = "CREATE TABLE " + TABLE_NAME +"( " +
                KEY_ID + " INTEGER PRIMARY, " +
                KEY_SCORE + " INTEGER DEFAULT 0," +
                KEY_SCORE_MAX + " INTEGER DEFAULT 0,"+
                " FOREIGN KEY ("+KEY_ID+") REFERENCES "+ LocalDBManager.DATABASE_NAME+"("+LocalDBManager.KEY_ID+")"+
                "ON DELETE CASCADE );";

        db.execSQL(CREATE_ALERTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addScore(int id, int gotScore, int outOf)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_SCORE, gotScore);
        values.put(KEY_SCORE_MAX, outOf);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void updateScore(int id, int gotScore, int outOf)
    {
        try{
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_ID , id);
            values.put(KEY_SCORE, gotScore);
            values.put(KEY_SCORE_MAX, outOf);

            db.update(TABLE_NAME, values, KEY_ID+"="+id, null);
            db.close();
        } catch (Exception e)
        {
            addScore(id, gotScore, outOf);
        }
    }
}
