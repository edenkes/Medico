package bredesh.medico.Notification;

import android.net.Uri;

import bredesh.medico.MedicoDB;

/**
 * Created by Omri on 22/05/2017.
 */

public class PartialVideoItem {

    public int id;
    public String name;
    public int repeats;
    public Uri uri;
    public MedicoDB.KIND kind;
    public boolean temp = false;

    public PartialVideoItem(int id, String name, Uri uri, int repeats, MedicoDB.KIND kind)
    {
        this.id = id;
        this.kind = kind;
        if(name.length() >=7 && name.substring(0,7).equals("_TEMP__")) {
            this.name = name.substring(7);
            temp = true;
        }
        else this.name = name;
        this.uri = uri;
        this.repeats = repeats;
    }
}
