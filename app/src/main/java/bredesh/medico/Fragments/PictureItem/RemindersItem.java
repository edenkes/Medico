package bredesh.medico.Fragments.PictureItem;

import android.media.RingtoneManager;

import bredesh.medico.DAL.MedicoDB;

/**
 * Created by edenk on 11/20/2017.
 */

public class RemindersItem extends Item{
    protected String notes;

    public RemindersItem(int id, String time, String name, String uri, int[] days, boolean detailedTimes, String allTimes,
                        MedicoDB.KIND kind, String notes, String alertSoundUri) {
        super(id, time, name, uri, days, detailedTimes, allTimes, kind, alertSoundUri);
        this.notes = notes;
    }

    public String getAlertSoundUri() {
        if (alertSoundUri == null)
            alertSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
        return alertSoundUri;
    }

}
