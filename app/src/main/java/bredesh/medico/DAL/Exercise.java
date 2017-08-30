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
    public String repetitionType;
    public String videoUri;
    
    public Exercise(Cursor c, MedicoDB db)
    {
        name = c.getString(c.getColumnIndex(MedicoDB.KEY_NAME));
        videoUri = c.getString(c.getColumnIndex(MedicoDB.URIVIDEO));
        daysToPerform = new boolean[7];
        daysToPerform[0] = c.getInt(c.getColumnIndex(MedicoDB.SUNDAY)) != 0;
        daysToPerform[1] = c.getInt(c.getColumnIndex(MedicoDB.MONDAY)) != 0;
        daysToPerform[2] = c.getInt(c.getColumnIndex(MedicoDB.TUESDAY)) != 0;
        daysToPerform[3] = c.getInt(c.getColumnIndex(MedicoDB.WEDNESDAY)) != 0;
        daysToPerform[4] = c.getInt(c.getColumnIndex(MedicoDB.THURSDAY)) != 0;
        daysToPerform[5] = c.getInt(c.getColumnIndex(MedicoDB.FRIDAY)) != 0;
        daysToPerform[6] = c.getInt(c.getColumnIndex(MedicoDB.SATURDAY)) != 0;
        noOfRepetitions = c.getInt(c.getColumnIndex(MedicoDB.KEY_REPEATS));
        repetitionType = c.getString(c.getColumnIndex(MedicoDB.KEY_REPETITION_TYPE));
        timesToPerform = c.getString(c.getColumnIndex(MedicoDB.KEY_TIME)).split(db.dbContext.getResources().getString(R.string.times_splitter));
    }
}
