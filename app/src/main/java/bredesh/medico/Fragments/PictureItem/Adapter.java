package bredesh.medico.Fragments.PictureItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import bredesh.medico.R;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by edenk on 22-Apr-17.
 */

public class Adapter extends BaseAdapter {

    private Context context;
    private int id;
    private ArrayList<VideoItem> arrayList;

    public Adapter(Context context, @LayoutRes int resource, ArrayList<VideoItem> objects) {
        super();
        this.context = context;
        this.id = resource;
        this.arrayList = objects;
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public VideoItem getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrayList.indexOf(getItem(position));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mainViewHolder = null;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.exercises_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            final VideoItem item = arrayList.get(position);

            setTextView(convertView, viewHolder, item);

            setButtons(convertView, viewHolder, item);

            convertView.setTag(viewHolder);
        } else  mainViewHolder = (ViewHolder) convertView.getTag();

        return convertView;
    }



    private void setButtons(final View convertView, final ViewHolder viewHolder, final VideoItem item) {
        viewHolder.btPlay = (Button) convertView.findViewById(R.id.btPlay);
        viewHolder.btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri videoUri = item.getUri();
                if(videoUri != null ) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }catch (RuntimeException e){

                    }

                }
                else    Toast.makeText(context.getApplicationContext(),
                        "Couldn't find the video/photo", Toast.LENGTH_SHORT).show();

            }
        });

        viewHolder.btAlerts = (Button) convertView.findViewById(R.id.btAlerts);
        viewHolder.btAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.switchAlertsActive();
                activateAlerts(viewHolder, item);
            }
        });
    }


    private void setTextView(View convertView, ViewHolder viewHolder, final VideoItem item) {
        viewHolder.tvExercisesName = (TextView) convertView.findViewById(R.id.tvExercisesName);
        viewHolder.tvExerciseTime = (TextView) convertView.findViewById(R.id.tvExerciseTime);
        viewHolder.tvSUN = (TextView) convertView.findViewById(R.id.tvSUN);
        viewHolder.tvMON = (TextView) convertView.findViewById(R.id.tvMON);
        viewHolder.tvTUE = (TextView) convertView.findViewById(R.id.tvTUE);
        viewHolder.tvWED = (TextView) convertView.findViewById(R.id.tvWED);
        viewHolder.tvTHU = (TextView) convertView.findViewById(R.id.tvTHU);
        viewHolder.tvFRI = (TextView) convertView.findViewById(R.id.tvFRI);
        viewHolder.tvSAT = (TextView) convertView.findViewById(R.id.tvSAT);

        viewHolder.imageSync = (ImageView) convertView.findViewById(R.id.imageSync);

        viewHolder.tvExercisesName.setText(item.getName());
        viewHolder.tvExerciseTime.setText(item.getTime());

        activateAlerts(viewHolder, item);
    }

    private void activateAlerts(ViewHolder viewHolder, VideoItem item) {
        if(item.isAlertsActive()) {
            int[] days = item.getDays();
            if (days[0] == 1) viewHolder.tvSUN.setVisibility(View.VISIBLE);
            if (days[1] == 1) viewHolder.tvMON.setVisibility(View.VISIBLE);
            if (days[2] == 1) viewHolder.tvTUE.setVisibility(View.VISIBLE);
            if (days[3] == 1) viewHolder.tvWED.setVisibility(View.VISIBLE);
            if (days[4] == 1) viewHolder.tvTHU.setVisibility(View.VISIBLE);
            if (days[5] == 1) viewHolder.tvFRI.setVisibility(View.VISIBLE);
            if (days[6] == 1) viewHolder.tvSAT.setVisibility(View.VISIBLE);
            if(days[0] == 1 || days[1] == 1 || days[2] == 1 || days[3] == 1 || days[4] == 1 || days[5] == 1 || days[6] == 1)
            {
                viewHolder.imageSync.setVisibility(View.VISIBLE);
                viewHolder.tvExerciseTime.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.imageSync.setVisibility(View.INVISIBLE);
                viewHolder.tvExerciseTime.setVisibility(View.INVISIBLE);
            }
        }else{
            viewHolder.tvSUN.setVisibility(View.INVISIBLE);
            viewHolder.tvMON.setVisibility(View.INVISIBLE);
            viewHolder.tvTUE.setVisibility(View.INVISIBLE);
            viewHolder.tvWED.setVisibility(View.INVISIBLE);
            viewHolder.tvTHU.setVisibility(View.INVISIBLE);
            viewHolder.tvFRI.setVisibility(View.INVISIBLE);
            viewHolder.tvSAT.setVisibility(View.INVISIBLE);
            viewHolder.imageSync.setVisibility(View.INVISIBLE);
            viewHolder.tvExerciseTime.setVisibility(View.INVISIBLE);
        }
    }

    private class ViewHolder{
        TextView tvExercisesName, tvExerciseTime;
        TextView tvSUN, tvMON, tvTUE, tvWED, tvTHU, tvFRI, tvSAT;
        Button btPlay, btAlerts;
        ImageView imageSync;
    }

}
