package bredesh.medico.Fragments.PictureItem;

import android.media.RingtoneManager;
import android.net.Uri;

import bredesh.medico.DAL.MedicoDB;

public class VideoItem extends Item{

    private boolean isAlertsActive;
    private int noOfRepetitions;
    private String repetitionType;

    public VideoItem(int id, String time, String name, String uri, int[] days, int noOfRepetitions, String repetitionType,  boolean detailedTimes, String allTimes, MedicoDB.KIND kind, String alertSoundUri){
        super(id,time,name,uri,days,detailedTimes,allTimes,kind, alertSoundUri);
        isAlertsActive = true;
        this.noOfRepetitions = noOfRepetitions;
        this.repetitionType = repetitionType;
    }

    public String getAlertSoundUri() {
        if (alertSoundUri == null)
            alertSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString();
        return alertSoundUri;
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

    public String getRepetitionType() {return this.repetitionType;}

    public boolean getDetailedTimes() {return this.detailedTimes;}

    public String getAllTimes() {return this.allTimes;}

    public String getDaysString(){
        String ans = "";
        for(int i=0; i<days.length; i++)
            if(days[i] == 1)
                ans+= daysNames[i]+", ";
        return ans;
    }

    public int getId() {return this.id;}
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
