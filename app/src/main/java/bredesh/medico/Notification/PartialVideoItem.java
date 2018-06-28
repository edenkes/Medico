package bredesh.medico.Notification;

import android.net.Uri;

import bredesh.medico.DAL.MedicoDB;

public class PartialVideoItem {
    public int id;
    public String name;
    public int repeats;
    public Uri uriVideo;
    public Uri uriImage;
    public String alertSoundUriString;
    public MedicoDB.KIND kind;
    public int repetition_type;
    public boolean temp = false;
    public int number_of_sets;

    public PartialVideoItem(int id, String name, Uri uriVideo, Uri uriImage, int repeats, int repetition_type, MedicoDB.KIND kind, String alertSoundUriString, int number_of_sets)
    {
        this.id = id;
        this.kind = kind;
        if(name.length() >=7 && name.substring(0,7).equals("_TEMP__")) {
            this.name = name.substring(7);
            temp = true;
        }
        else this.name = name;
        this.uriVideo = uriVideo;
        this.uriImage = uriImage;
        this.repeats = repeats;
        this.repetition_type = repetition_type;
        this.alertSoundUriString = alertSoundUriString;
        this.number_of_sets = number_of_sets;
    }
}
