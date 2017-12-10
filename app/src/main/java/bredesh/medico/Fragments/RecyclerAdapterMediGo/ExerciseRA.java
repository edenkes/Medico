package bredesh.medico.Fragments.RecyclerAdapterMediGo;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.Fragments.DataMediGo.ExerciseDa;
import bredesh.medico.Fragments.ItemMediGo.ExerciseIt;
import bredesh.medico.R;
import bredesh.medico.Utils.Utils;

/**
 * Created by edenk on 12/10/2017.
 */

public class ExerciseRA extends RecyclerView.Adapter<ExerciseRA.CustomViewHolder> {
    private List<ExerciseIt> videoItems;
    private Context context;
    private Activity activity;

    public ExerciseRA(Context context, List<ExerciseIt> videoItems, Activity activity) {
        this.videoItems = videoItems;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public ExerciseRA.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_exercise_item, viewGroup, false);
        return new ExerciseRA.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ExerciseRA.CustomViewHolder customViewHolder, int i) {
        final ExerciseIt  item = videoItems.get(i);
        final int position = i;
        customViewHolder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ExerciseDa.class);
                intent.putExtra("exerciseId", item.getId());
                intent.putExtra("repeats", item.getNoOfRepetitions());
                intent.putExtra("repetition_type", item.getRepetitionType());
                intent.putExtra("time", item.getAllTimes());
                intent.putExtra("exercise_name", item.getName());
                intent.putExtra("days", item.getDays());
                Uri uri = item.getUri();
                if (uri!=null)
                    intent.putExtra("RecordedUri", item.getUri().toString());
                intent.putExtra("AlertSoundUri", item.getAlertSoundUri());

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
        if (!item.getDetailedTimes()) {
            customViewHolder.tvExerciseTimeMulti.setText(times);
            customViewHolder.tvExerciseTimeMulti.setVisibility(View.VISIBLE);
            customViewHolder.tvExerciseTime.setVisibility(View.GONE);
        }
        else
        {
            customViewHolder.tvExerciseTime.setText(times);
            customViewHolder.tvExerciseTime.setVisibility(View.VISIBLE);
            customViewHolder.tvExerciseTimeMulti.setVisibility(View.GONE);

        }

        String repetitionTypeInDB = item.getRepetitionType();
        if (repetitionTypeInDB != null) {
            int repetitionType = Integer.parseInt(item.getRepetitionType());
            if (repetitionType == R.string.repetition_type_repetitions ) {
                customViewHolder.imageRepeat.setVisibility(View.VISIBLE);
            }
            else {
                customViewHolder.imageRepeat.setVisibility(View.GONE);
                String repetitionTypeS = Utils.stringOrFromResource(resources,repetitionTypeInDB );
                customViewHolder.tvRepetitionType.setText(repetitionTypeS);
            }

        }

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



    private void activateAlerts(ExerciseRA.CustomViewHolder viewHolder, ExerciseIt item) {
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
            viewHolder.tvExerciseTime.setTextColor(ContextCompat.getColor(context, R.color.labelColor));
        else
            viewHolder.tvExerciseTime.setTextColor(ContextCompat.getColor(context, R.color.colorGreyLite));
    }


    @Override
    public int getItemCount() {
        return (null != videoItems ? videoItems.size() : 0);
    }



    class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView tvExercisesName, tvExerciseTime, tvExerciseTimeMulti,  lblExerciseNoOfRepeats, tvRepetitionType;
        private TextView tvSUN, tvMON, tvTUE, tvWED, tvTHU, tvFRI, tvSAT;
        private ImageButton btPlay;
        private TextView[] days;
        private ImageView imageRepeat;
        View v;

        private CustomViewHolder(View convertView) {
            super(convertView);
            this.v = convertView;
            this.tvExercisesName = (TextView) convertView.findViewById(R.id.tvExercisesName);
            this.tvExerciseTime = (TextView) convertView.findViewById(R.id.tvExerciseTime);
            this.tvExerciseTimeMulti = (TextView) convertView.findViewById(R.id.tvExerciseTimeMulti);
            this.tvSUN = (TextView) convertView.findViewById(R.id.tvSUN);
            this.tvMON = (TextView) convertView.findViewById(R.id.tvMON);
            this.tvTUE = (TextView) convertView.findViewById(R.id.tvTUE);
            this.tvWED = (TextView) convertView.findViewById(R.id.tvWED);
            this.tvTHU = (TextView) convertView.findViewById(R.id.tvTHU);
            this.tvFRI = (TextView) convertView.findViewById(R.id.tvFRI);
            this.tvSAT = (TextView) convertView.findViewById(R.id.tvSAT);
            this.lblExerciseNoOfRepeats = (TextView) convertView.findViewById(R.id.lblExerciseNoOfRepeats);
            this.tvRepetitionType = (TextView) convertView.findViewById(R.id.txMedicinedosageType);
            this.btPlay = (ImageButton) convertView.findViewById(R.id.btPlay);
            this.days = new TextView[]{tvSUN,tvMON,tvTUE,tvWED,tvTHU,tvFRI,tvSAT};
            this.imageRepeat = (ImageView) convertView.findViewById(R.id.imageRepeat);
        }
    }
}
