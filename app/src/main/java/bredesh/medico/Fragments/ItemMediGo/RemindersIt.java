package bredesh.medico.Fragments.ItemMediGo;

import bredesh.medico.DAL.MedicoDB;

/**
 * Created by edenk on 12/10/2017.
 */
/*
* This class is sub-class of Item, And it's contain the Reminders fragment characters
*/
public class RemindersIt extends ItemGeneral{
    public String notes;

    public RemindersIt(int id, String time, String name, String uriVideo, String uriImage, int[] days, boolean detailedTimes, String allTimes,
                         MedicoDB.KIND kind, String notes, String alertSoundUri) {
        super(id, time, name, uriVideo, uriImage, days, detailedTimes, allTimes, kind, alertSoundUri);
        this.notes = notes;
    }

}
