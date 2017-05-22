package bredesh.medico.Notification;

import android.net.Uri;

/**
 * Created by Omri on 22/05/2017.
 */

public class PartialVideoItem {

    public int id;
    public String name;
    public int repeats;
    public Uri uri;

    public PartialVideoItem(int id, String name, Uri uri, int repeats)
    {
        this.id = id;
        if(name.length() >=7 && name.substring(0,7).equals("_TEMP__"))
            this.name = name.substring(7);
        else this.name = name;
        this.uri = uri;
        this.repeats = repeats;
    }
}
