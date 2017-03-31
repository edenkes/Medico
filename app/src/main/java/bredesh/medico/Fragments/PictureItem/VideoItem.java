package bredesh.medico.Fragments.PictureItem;

/**
 * Created by Omri on 01-Apr-17.
 */

public class VideoItem {

    private String name;
    private String time;
    private int[] days;
    private final String[] daysNames = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

    public VideoItem(String time, String name, int[] days)
    {
        this.time = time;
        this.name = name;
        this.days = days;
    }

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
}
