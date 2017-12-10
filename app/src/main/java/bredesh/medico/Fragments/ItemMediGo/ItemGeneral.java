package bredesh.medico.Fragments.ItemMediGo;

import android.media.RingtoneManager;
import android.net.Uri;

import bredesh.medico.DAL.MedicoDB;

/**
 * Created by edenk on 12/10/2017.
 */

public abstract class ItemGeneral {
    public int[] days;
    public int id;
    public String name, time, alertSoundUri;
    public MedicoDB.KIND kind;
    public Uri uriVideo;
    public Uri uriStill;

    protected final String[] daysNames = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    public boolean detailedTimes;
    public String allTimes;

    ItemGeneral(int id, String time, String name/*, String uriStill*/, String uriVideo, int[] days, boolean detailedTimes, String allTimes, MedicoDB.KIND kind, String alertSoundUri)
    {
        this.id = id;
        this.time = time;
        this.name = name;
        this.days = days;
        /*if (uriStill != null && !uriStill.equals("null")) {
            this.uriStill = Uri.parse(uriStill);
        }else this.uriStill = null;*/
        if (uriVideo != null && !uriVideo.equals("null")) {
            this.uriVideo = Uri.parse(uriVideo);
        }else this.uriVideo = null;
        this.detailedTimes = detailedTimes;
        this.allTimes = allTimes;
        this.kind = kind;
        this.alertSoundUri = alertSoundUri;
    }

    public String getAlertSoundUri() {
        if (alertSoundUri == null)
            alertSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
        return alertSoundUri;
    }

    public Uri getUri() { return  uriVideo;}

    public String getTime() { return time; }

    public String getName() {
        return name;
    }

    public int[] getDays() {
        return days;
    }

    public boolean getDetailedTimes() {return this.detailedTimes;}

    public String getAllTimes() {return this.allTimes;}

    public int getId() {return this.id;}

    public void setName(String name){
        this.name = name;
    }
}
