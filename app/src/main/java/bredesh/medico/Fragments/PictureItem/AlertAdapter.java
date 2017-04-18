package bredesh.medico.Fragments.PictureItem;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import bredesh.medico.Camera.LocalDBManager;
import bredesh.medico.R;

/**
 * Created by Omri on 01-Apr-17.
 */

public class AlertAdapter extends BaseAdapter {

    VideoItem[] videoItems;
    Context con;

    public AlertAdapter(Context con)
    {
        this.con = con;
        LocalDBManager db = new LocalDBManager(con);
        Cursor c = db.getAllAlerts();
        videoItems = new VideoItem[c.getCount()];
        int i=0;
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
            String time = c.getString(c.getColumnIndex(db.KEY_TIME));
            String name = c.getString(c.getColumnIndex(db.KEY_NAME));
            String uri = c.getString(c.getColumnIndex(db.URIVIDEO));
            int[] days = new int[7];
            days[0] = c.getInt(c.getColumnIndex(db.SUNDAY));
            days[1] = c.getInt(c.getColumnIndex(db.MONDAY));
            days[2] = c.getInt(c.getColumnIndex(db.TUESDAY));
            days[3] = c.getInt(c.getColumnIndex(db.WEDNESDAY));
            days[4] = c.getInt(c.getColumnIndex(db.THURSDAY));
            days[5] = c.getInt(c.getColumnIndex(db.FRIDAY));
            days[6] = c.getInt(c.getColumnIndex(db.SATURDAY));

            videoItems[i] = new VideoItem(time, name, uri, days);
            i++;
        }
        c.close();
    }

    @Override
    public int getCount() {
        return videoItems.length;
    }

    @Override
    public Object getItem(int position) {
        return videoItems[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(con);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.video_item, null);

            TextView tv_name = (TextView) gridView.findViewById(R.id.tv_name);
            TextView sunday = (TextView) gridView.findViewById(R.id.sunday);
            TextView monday = (TextView) gridView.findViewById(R.id.monday);
            TextView tuesday = (TextView) gridView.findViewById(R.id.tuesday);
            TextView wednesday = (TextView) gridView.findViewById(R.id.wednesday);
            TextView thursday = (TextView) gridView.findViewById(R.id.thursday);
            TextView friday = (TextView) gridView.findViewById(R.id.friday);
            TextView saturday = (TextView) gridView.findViewById(R.id.saturday);

            tv_name.setText(videoItems[position].getName());
            int[] days =videoItems[position].getDays();
            if(days[0] == 1) sunday.setBackgroundColor(Color.parseColor("#00FF33"));
            if(days[1] == 1) monday.setBackgroundColor(Color.parseColor("#00FF33"));
            if(days[2] == 1) tuesday.setBackgroundColor(Color.parseColor("#00FF33"));
            if(days[3] == 1) wednesday.setBackgroundColor(Color.parseColor("#00FF33"));
            if(days[4] == 1) thursday.setBackgroundColor(Color.parseColor("#00FF33"));
            if(days[5] == 1) friday.setBackgroundColor(Color.parseColor("#00FF33"));
            if(days[6] == 1) saturday.setBackgroundColor(Color.parseColor("#00FF33"));

            TextView time = (TextView) gridView.findViewById(R.id.tv_time);
            time.setText(videoItems[position].getTime());

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

}
