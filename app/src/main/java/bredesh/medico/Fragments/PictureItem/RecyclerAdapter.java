package bredesh.medico.Fragments.PictureItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import bredesh.medico.Camera.VideoData;
import bredesh.medico.R;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CustomViewHolder> {
    private List<VideoItem> videoItems;
    private Context context;
    private Activity activity;

    public RecyclerAdapter(Context context, List<VideoItem> videoItems, Activity activity) {
        this.videoItems = videoItems;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_exercise_item, viewGroup, false);
        return new CustomViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        final VideoItem  item = videoItems.get(i);

        customViewHolder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoData.class);
                intent.putExtra("exerciseId", item.getId());
                intent.putExtra("repeats", item.getNoOfRepetitions());
                intent.putExtra("time", item.getAllTimes());
                intent.putExtra("exercise_name", item.getName());
                intent.putExtra("days", item.getDays());
                activity.startActivityForResult(intent, 0x1987);
            }
        });

        customViewHolder.lblExerciseNoOfRepeats.setText(String.valueOf(item.getNoOfRepetitions()));
        customViewHolder.tvExercisesName.setText(item.getName());
        final Resources resources = context.getResources();
        String times = item.getTime().replace(resources.getString(R.string.times_splitter), resources.getString(R.string.times_nice_separator));
        customViewHolder.tvExerciseTime.setText(times);
        if (!item.getDetailedTimes())
            customViewHolder.tvExerciseTime.setTextDirection(View.TEXT_DIRECTION_RTL);

        activateAlerts(customViewHolder, item);

        customViewHolder.btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri videoUri = item.getUri();
                if(videoUri != null ) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }catch (RuntimeException e){
                        Toast.makeText(context.getApplicationContext(),
                               resources.getString(R.string.unaviable_media), Toast.LENGTH_SHORT).show();
                    }
                }
                else    Toast.makeText(context.getApplicationContext(),
                        resources.getString(R.string.unaviable_media), Toast.LENGTH_SHORT).show();
            }
        });

        customViewHolder.btAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.switchAlertsActive();
                activateAlerts(customViewHolder, item);
            }
        });
    }


    private void activateAlerts(RecyclerAdapter.CustomViewHolder viewHolder, VideoItem item) {
        if(item.isAlertsActive()) {
            int[] days = item.getDays();
            if (days[0] == 1) viewHolder.tvSUN.setVisibility(View.VISIBLE);
            if (days[1] == 1) viewHolder.tvMON.setVisibility(View.VISIBLE);
            if (days[2] == 1) viewHolder.tvTUE.setVisibility(View.VISIBLE);
            if (days[3] == 1) viewHolder.tvWED.setVisibility(View.VISIBLE);
            if (days[4] == 1) viewHolder.tvTHU.setVisibility(View.VISIBLE);
            if (days[5] == 1) viewHolder.tvFRI.setVisibility(View.VISIBLE);
            if (days[6] == 1) viewHolder.tvSAT.setVisibility(View.VISIBLE);
            if(days[0] == 1 || days[1] == 1 || days[2] == 1 || days[3] == 1 || days[4] == 1 || days[5] == 1 || days[6] == 1) {
                viewHolder.tvExerciseTime.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.tvExerciseTime.setVisibility(View.INVISIBLE);
            }
        }else {
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


    @Override
    public int getItemCount() {
        return (null != videoItems ? videoItems.size() : 0);
    }



    class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView tvExercisesName, tvExerciseTime, lblExerciseNoOfRepeats;
        private TextView tvSUN, tvMON, tvTUE, tvWED, tvTHU, tvFRI, tvSAT;
        private Button btAlerts;
        private ImageButton btPlay;
        private ImageView imageSync;
        View v;

        private CustomViewHolder(View convertView) {
            super(convertView);
            this.v = convertView;
            this.tvExercisesName = (TextView) convertView.findViewById(R.id.tvExercisesName);
            this.tvExerciseTime = (TextView) convertView.findViewById(R.id.tvExerciseTime);
            this.tvSUN = (TextView) convertView.findViewById(R.id.tvSUN);
            this.tvMON = (TextView) convertView.findViewById(R.id.tvMON);
            this.tvTUE = (TextView) convertView.findViewById(R.id.tvTUE);
            this.tvWED = (TextView) convertView.findViewById(R.id.tvWED);
            this.tvTHU = (TextView) convertView.findViewById(R.id.tvTHU);
            this.tvFRI = (TextView) convertView.findViewById(R.id.tvFRI);
            this.tvSAT = (TextView) convertView.findViewById(R.id.tvSAT);

            this.imageSync = (ImageView) convertView.findViewById(R.id.imageRepeat);
            this.lblExerciseNoOfRepeats = (TextView) convertView.findViewById(R.id.lblExerciseNoOfRepeats);

            this.btPlay = (ImageButton) convertView.findViewById(R.id.btPlay);
            this.btAlerts = (Button) convertView.findViewById(R.id.btAlerts);
        }
    }
}