package bredesh.medico.Fragments.PictureItem;

import bredesh.medico.MedicoDB;

/**
 * Created by Omri on 13/06/2017.
 */

public class MedicineItem extends Item {

    protected String type, special,  notes, amount;

    public MedicineItem(int id, String time, String name, String uri, int[] days, boolean detailedTimes, String allTimes,
                        MedicoDB.KIND kind, String type, String special, String notes, String amount) {
        super(id, time, name, uri, days, detailedTimes, allTimes, kind);
        this.type = type;
        this.special = special;
        this.notes = notes;
        this.amount = amount;
    }
}
