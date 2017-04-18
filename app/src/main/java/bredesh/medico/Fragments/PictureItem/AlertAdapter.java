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
            gridView = inflater.inflate(R.layout.exercises_item, null);

            TextView tv_name = (TextView) gridView.findViewById(R.id.textExercisesName);
            TextView sunday = (TextView) gridView.findViewById(R.id.textSUN);
            TextView monday = (TextView) gridView.findViewById(R.id.textMON);
            TextView tuesday = (TextView) gridView.findViewById(R.id.textTUE);
            TextView wednesday = (TextView) gridView.findViewById(R.id.textWED);
            TextView thursday = (TextView) gridView.findViewById(R.id.textTHU);
            TextView friday = (TextView) gridView.findViewById(R.id.textFRI);
            TextView saturday = (TextView) gridView.findViewById(R.id.textSAT);
            TextView time = (TextView) gridView.findViewById(R.id.textTimeExercise);

            tv_name.setText(videoItems[position].getName());
            int[] days = videoItems[position].getDays();
            if(days[0] == 1) sunday.setVisibility(View.VISIBLE);
            if(days[1] == 1) monday.setVisibility(View.VISIBLE);
            if(days[2] == 1) tuesday.setVisibility(View.VISIBLE);
            if(days[3] == 1) wednesday.setVisibility(View.VISIBLE);
            if(days[4] == 1) thursday.setVisibility(View.VISIBLE);
            if(days[5] == 1) friday.setVisibility(View.VISIBLE);
            if(days[6] == 1) saturday.setVisibility(View.VISIBLE);

            time.setText(videoItems[position].getTime());

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

}
