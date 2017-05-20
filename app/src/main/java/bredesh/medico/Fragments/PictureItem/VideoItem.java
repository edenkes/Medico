package bredesh.medico.Fragments.PictureItem;

import android.net.Uri;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class VideoItem {
    int id;
    private String name;
    private String time;
    private Uri uri;
    private int[] days;
    private final String[] daysNames = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    private boolean isAlertsActive;
    private int noOfRepetitions;

    public VideoItem(int id, String time, String name, String uri, int[] days, int noOfRepetitions){
        this.id = id;
        this.time = time;
        this.name = name;
        this.days = days;
        if (uri != null && !uri.equals("null")) {
            this.uri = Uri.parse(uri);
        }else this.uri = null;
        isAlertsActive = true;
        this.noOfRepetitions = noOfRepetitions;
    }

    public Uri getUri() { return  uri;}

    public String getTime() { return time; }

    public String getName() {
        return name;
    }

    public int[] getDays() {
        return days;
    }

    public int getNoOfRepetitions() {return this.noOfRepetitions;}

    public String getDaysString(){
        String ans = "";
        for(int i=0; i<days.length; i++)
            if(days[i] == 1)
                ans+= daysNames[i]+", ";
        return ans;
    }

    public boolean isAlertsActive(){
        return isAlertsActive;
    }

    public void setIsAlertsActive(boolean isAlertsActive){
        this.isAlertsActive = isAlertsActive;
    }

    void switchAlertsActive(){
        if(isAlertsActive)
            isAlertsActive = false;
        else isAlertsActive = true;
    }

    public void setName(String name){
        this.name = name;
    }
}
