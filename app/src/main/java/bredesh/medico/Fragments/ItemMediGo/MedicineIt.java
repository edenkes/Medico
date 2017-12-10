package bredesh.medico.Fragments.ItemMediGo;

import bredesh.medico.DAL.MedicoDB;

/**
 * Created by edenk on 12/10/2017.
 */

public class MedicineIt extends ItemGeneral{
    public String type;
    public String special;
    public String notes;
    public String amount;

    public MedicineIt(int id, String time, String name, String uriStill, int[] days, boolean detailedTimes, String allTimes,
                        MedicoDB.KIND kind, String type, String special, String notes, String amount) {
        super(id, time, name, uriStill, days, detailedTimes, allTimes, kind, null);
        this.type = type;
        this.special = special;
        this.notes = notes;
        this.amount = amount;
    }
}
