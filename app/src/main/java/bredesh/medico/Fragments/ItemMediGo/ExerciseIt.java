package bredesh.medico.Fragments.ItemMediGo;

import bredesh.medico.DAL.MedicoDB;

/**
 * Created by edenk on 12/10/2017.
 */
/*
* This class is sub-class of Item, And it's contain the Exercise fragment characters
* */
public class ExerciseIt extends ItemGeneral{
    private int noOfRepetitions;
    private String repetitionType;

    public ExerciseIt(int id, String time, String name, String uriVideo, int[] days, int noOfRepetitions, String repetitionType, boolean detailedTimes, String allTimes, MedicoDB.KIND kind, String alertSoundUri){
        super(id,time,name,uriVideo, null, days,detailedTimes,allTimes,kind, alertSoundUri);
        this.noOfRepetitions = noOfRepetitions;
        this.repetitionType = repetitionType;
    }

    //Getters
    public int getNoOfRepetitions() {return this.noOfRepetitions;}

    public String getRepetitionType() {return this.repetitionType;}
}
