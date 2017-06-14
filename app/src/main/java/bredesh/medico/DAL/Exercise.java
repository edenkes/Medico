package bredesh.medico.DAL;

import android.database.Cursor;

import bredesh.medico.R;

/**
 * Created by ophir on 6/9/2017.
 */

public class Exercise {
    public int id;
    public String name;
    public boolean[] daysToPerform;
    public String[] timesToPerform;
    public int noOfRepetitions;
    public String videoUri;
    
    public Exercise(Cursor c, MedicoDB db)
    {
        name = c.getString(c.getColumnIndex(db.KEY_NAME));
        videoUri = c.getString(c.getColumnIndex(db.URIVIDEO));
        daysToPerform = new boolean[7];
        daysToPerform[0] = c.getInt(c.getColumnIndex(db.SUNDAY)) != 0;
        daysToPerform[1] = c.getInt(c.getColumnIndex(db.MONDAY)) != 0;
        daysToPerform[2] = c.getInt(c.getColumnIndex(db.TUESDAY)) != 0;
        daysToPerform[3] = c.getInt(c.getColumnIndex(db.WEDNESDAY)) != 0;
        daysToPerform[4] = c.getInt(c.getColumnIndex(db.THURSDAY)) != 0;
        daysToPerform[5] = c.getInt(c.getColumnIndex(db.FRIDAY)) != 0;
        daysToPerform[6] = c.getInt(c.getColumnIndex(db.SATURDAY)) != 0;
        noOfRepetitions = c.getInt(c.getColumnIndex(db.KEY_REPEATS));
        timesToPerform = c.getString(c.getColumnIndex(db.KEY_TIME)).split(db.dbContext.getResources().getString(R.string.times_splitter));
    }
}
