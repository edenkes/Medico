package bredesh.medico.Fragments.PictureItem;

import bredesh.medico.DAL.MedicoDB;

/**
 * Created by Omri on 13/06/2017.
 */

public class MedicineItem extends Item {

    protected String type, special,  notes, amount;

    public MedicineItem(int id, String time, String name, String uriStill, int[] days, boolean detailedTimes, String allTimes,
                        MedicoDB.KIND kind, String type, String special, String notes, String amount) {
        super(id, time, name, uriStill, days, detailedTimes, allTimes, kind, null);
        this.type = type;
        this.special = special;
        this.notes = notes;
        this.amount = amount;
    }
}
