package bredesh.medico.Fragments.ItemMediGo;

import android.net.Uri;

import bredesh.medico.DAL.MedicoDB;

/**
 * Created by edenk on 12/10/2017.
 */

public abstract class ItemGeneral {
    public int id;
    public String name;
    public MedicoDB.KIND kind;
    public String time;
    public Uri uriVideo;
    public String alertSoundUri;
    public int[] days;

    protected final String[] daysNames = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    public boolean detailedTimes;
    public String allTimes;

    public ItemGeneral(int id, String time, String name/*, String uriStill*/, String uriVideo, int[] days, boolean detailedTimes, String allTimes, MedicoDB.KIND kind, String alertSoundUri)
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
}
