package bredesh.medico.Fragments.PictureItem;

import android.net.Uri;
import android.widget.Switch;

/**
 * Created by Omri on 01-Apr-17.
 */

public class VideoItem {

    private String name;
    private String time;
    private Uri uri;
    private int[] days;
    private final String[] daysNames = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    private boolean isChecked;

    public VideoItem(String time, String name, String uri, int[] days)
    {
        this.time = time;
        this.name = name;
        this.days = days;
        if (!uri.equals("null")) {
            this.uri = Uri.parse(uri);
        }else this.uri = null;
        isChecked = false;
    }

    public Uri getUri() { return  uri;}

    public String getTime() { return time; }

    public String getName() {
        return name;
    }

    public int[] getDays() {
        return days;
    }

    public String getDaysString()
    {
        String ans = "";
        for(int i=0; i<days.length; i++)
            if(days[i] == 1)
                ans+= daysNames[i]+", ";
        return ans;
    }
    public boolean isChecked(){
        return isChecked;
    }


    public void setIsChecked(boolean isChecked){
        this.isChecked = isChecked;
    }

    public void setName(String name){
        this.name = name;
    }
}
