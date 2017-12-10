package bredesh.medico.Fragments.PictureItem;

import android.net.Uri;

import bredesh.medico.DAL.MedicoDB;

/**
 * Created by Omri on 13/06/2017.
 */

public class Item {

    public int id;
    public String name;
    public MedicoDB.KIND kind;
    public String time;
    public Uri uriVideo;
    public String alertSoundUri;
    public int[] days;

    protected final String[] daysNames = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    protected boolean detailedTimes;
    protected String allTimes;
    
    public Item(int id, String time, String name/*, String uriStill*/, String uriVideo, int[] days, boolean detailedTimes, String allTimes, MedicoDB.KIND kind, String alertSoundUri)
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
