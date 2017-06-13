package bredesh.medico.Fragments.PictureItem;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import bredesh.medico.Camera.VideoData;
import bredesh.medico.MedicoDB;
import bredesh.medico.R;


public class ExerciseRecyclerAdapter extends RecyclerView.Adapter<ExerciseRecyclerAdapter.CustomViewHolder> {
    private List<VideoItem> videoItems;
    private Context context;
    private Activity activity;

    public ExerciseRecyclerAdapter(Context context, List<VideoItem> videoItems, Activity activity) {
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
        final int position = i;
        customViewHolder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoData.class);
                intent.putExtra("exerciseId", item.getId());
                intent.putExtra("repeats", item.getNoOfRepetitions());
                intent.putExtra("time", item.getAllTimes());
                intent.putExtra("exercise_name", item.getName());
                intent.putExtra("days", item.getDays());
                Uri uri = item.getUri();
                if (uri!=null)
                    intent.putExtra("RecordedUri", item.getUri().toString());
                activity.startActivityForResult(intent, 0x1987);
            }
        });

        final Resources resources = context.getResources();

        DialogInterface.OnClickListener onDelete = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                MedicoDB db = new MedicoDB(context);
                db.deleteRow(item.getId());
                videoItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, videoItems.size());
            }
        };

        final AlertDialog deleteDialog = new AlertDialog.Builder(activity)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onDelete)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.delete_exercise_confirm)).create();


        customViewHolder.v.setOnLongClickListener(new View.OnLongClickListener() {
              @Override
              public boolean onLongClick(View v) {
                  deleteDialog.show();
                  return true;
              }
          });



        customViewHolder.lblExerciseNoOfRepeats.setText(String.valueOf(item.getNoOfRepetitions()));
        customViewHolder.tvExercisesName.setText(item.getName());
        if (item.getUri() == null)
            customViewHolder.btPlay.setVisibility(View.INVISIBLE);


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
                               resources.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                    }
                }
                else    Toast.makeText(context.getApplicationContext(),
                        resources.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void activateAlerts(ExerciseRecyclerAdapter.CustomViewHolder viewHolder, VideoItem item) {
        int[] days = item.getDays();
        boolean isActive = false;
        for(int i=0; i<days.length; i++)
        {
            if(days[i] == 1) {
                viewHolder.days[i].setTextColor(ContextCompat.getColor(context, R.color.titleColor));
                isActive = true;
            }
            else viewHolder.days[i].setTextColor(ContextCompat.getColor(context, R.color.colorGreyLite));

        }
        if(isActive)
            viewHolder.tvExerciseTime.setTextColor(ContextCompat.getColor(context, R.color.titleColor));
        else
            viewHolder.tvExerciseTime.setTextColor(ContextCompat.getColor(context, R.color.colorGreyLite));
    }


    @Override
    public int getItemCount() {
        return (null != videoItems ? videoItems.size() : 0);
    }



    class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView tvExercisesName, tvExerciseTime, lblExerciseNoOfRepeats;
        private TextView tvSUN, tvMON, tvTUE, tvWED, tvTHU, tvFRI, tvSAT;
        private ImageButton btPlay;
        private TextView[] days;
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
            this.lblExerciseNoOfRepeats = (TextView) convertView.findViewById(R.id.lblExerciseNoOfRepeats);
            this.btPlay = (ImageButton) convertView.findViewById(R.id.btPlay);
            this.days = new TextView[]{tvSUN,tvMON,tvTUE,tvWED,tvTHU,tvFRI,tvSAT};
        }
    }
}