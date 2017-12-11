package bredesh.medico.Fragments.ItemMediGo;

import android.media.RingtoneManager;
import android.net.Uri;

import bredesh.medico.DAL.MedicoDB;

/**
 * Created by edenk on 12/10/2017.
 */

public class ExerciseIt extends ItemGeneral{
    private int noOfRepetitions;
    private String repetitionType;

    public ExerciseIt(int id, String time, String name, String uriVideo, int[] days, int noOfRepetitions, String repetitionType, boolean detailedTimes, String allTimes, MedicoDB.KIND kind, String alertSoundUri){
        super(id,time,name,uriVideo, null, days,detailedTimes,allTimes,kind, alertSoundUri);
        this.noOfRepetitions = noOfRepetitions;
        this.repetitionType = repetitionType;
    }

    public int getNoOfRepetitions() {return this.noOfRepetitions;}

    public String getRepetitionType() {return this.repetitionType;}
}
