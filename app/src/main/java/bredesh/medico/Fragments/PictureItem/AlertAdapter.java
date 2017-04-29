package bredesh.medico.Fragments.PictureItem;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import bredesh.medico.camera.LocalDBManager;
import bredesh.medico.R;

/**
 * Created by Omri on 01-Apr-17.
 */

public class AlertAdapter extends BaseAdapter {

    VideoItem[] videoItems;
    Context con;
    private TextView tvExercisesName, tvExerciseTime;
    private TextView tvSUN, tvMON, tvTUE, tvWED, tvTHU, tvFRI, tvSAT;

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

            setupTestView(gridView);

            tvExercisesName.setText(videoItems[position].getName());
            int[] days = videoItems[position].getDays();
            if(days[0] == 1) tvSUN.setVisibility(View.VISIBLE);
            if(days[1] == 1) tvMON.setVisibility(View.VISIBLE);
            if(days[2] == 1) tvTUE.setVisibility(View.VISIBLE);
            if(days[3] == 1) tvWED.setVisibility(View.VISIBLE);
            if(days[4] == 1) tvTHU.setVisibility(View.VISIBLE);
            if(days[5] == 1) tvFRI.setVisibility(View.VISIBLE);
            if(days[6] == 1) tvSAT.setVisibility(View.VISIBLE);

            tvExerciseTime.setText(videoItems[position].getTime());

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    private void setupTestView(View gridView) {
        tvExercisesName = (TextView) gridView.findViewById(R.id.tvExercisesName);
        tvExerciseTime = (TextView) gridView.findViewById(R.id.tvExerciseTime);
        tvSUN = (TextView) gridView.findViewById(R.id.tvSUN);
        tvMON = (TextView) gridView.findViewById(R.id.tvMON);
        tvTUE = (TextView) gridView.findViewById(R.id.tvTUE);
        tvWED = (TextView) gridView.findViewById(R.id.tvWED);
        tvTHU = (TextView) gridView.findViewById(R.id.tvTHU);
        tvFRI = (TextView) gridView.findViewById(R.id.tvFRI);
        tvSAT = (TextView) gridView.findViewById(R.id.tvSAT);
    }

}
