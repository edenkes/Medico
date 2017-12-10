package bredesh.medico.Fragments.PictureItem;

import bredesh.medico.DAL.MedicoDB;

/**
 * Created by edenk on 11/20/2017.
 */

public class RemindersItem extends Item{
    protected String notes;

    /*public RemindersItem(int id, String time, String name, String uriStill, String uriVideo, int[] days, boolean detailedTimes, String allTimes,
                        MedicoDB.KIND kind, String type, String special, String notes, String amount) {
        super(id, time, name, uriStill, uriVideo, days, detailedTimes, allTimes, kind, null);
        this.type = type;
        this.special = special;
        this.notes = notes;
        this.amount = amount;
    }*/

    public RemindersItem(int id, String time, String name, String uri, int[] days, boolean detailedTimes, String allTimes,
                        MedicoDB.KIND kind, String notes, String alertSoundUri) {
        super(id, time, name, uri, days, detailedTimes, allTimes, kind, alertSoundUri);
        this.notes = notes;
    }
}
