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
    private int repetitionType;
    private int numberOfSets;

    public ExerciseIt(int id, String time, String name, String uriVideo, int[] days, int noOfRepetitions, int repetitionType, boolean detailedTimes, String allTimes, MedicoDB.KIND kind, String alertSoundUri, int numberOfSets){
        super(id,time,name,uriVideo, null, days,detailedTimes,allTimes,kind, alertSoundUri);
        this.noOfRepetitions = noOfRepetitions;
        this.repetitionType = repetitionType;
        this.numberOfSets = numberOfSets;
    }

    //Getters
    public int getNoOfRepetitions() {return this.noOfRepetitions;}

    public int getNumberOfSets() {
        return this.numberOfSets;
    }

    public int getRepetitionType() {return this.repetitionType;}
}
