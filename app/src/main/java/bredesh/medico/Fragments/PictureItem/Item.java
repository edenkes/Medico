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
    public Uri uri;
    public int[] days;

    protected final String[] daysNames = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    protected boolean detailedTimes;
    protected String allTimes;
    
    public Item(int id, String time, String name, String uri, int[] days, boolean detailedTimes, String allTimes, MedicoDB.KIND kind)
    {
        this.id = id;
        this.time = time;
        this.name = name;
        this.days = days;
        if (uri != null && !uri.equals("null")) {
            this.uri = Uri.parse(uri);
        }else this.uri = null;
        this.detailedTimes = detailedTimes;
        this.allTimes = allTimes;
        this.kind = kind;
    }
}
