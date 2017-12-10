package bredesh.medico.Fragments.RecyclerAdapterMediGo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import bredesh.medico.Fragments.DataMediGo.ExerciseDa;
import bredesh.medico.Fragments.ItemMediGo.ExerciseIt;
import bredesh.medico.Fragments.ItemMediGo.ItemGeneral;
import bredesh.medico.R;
import bredesh.medico.Utils.Utils;

/**
 * Created by edenk on 12/10/2017.
 */

public class ExerciseRA extends RecyclerAdapterGeneral<ExerciseIt> {
    public ExerciseRA(Context context, List<ExerciseIt> itemGeneral, Activity activity) {
        super(context,itemGeneral, activity);
    }

    @Override
    protected Intent sendIntetInformation(ItemGeneral item) {
        Intent intent = new Intent(context, ExerciseDa.class);
        intent.putExtra("exerciseId", item.getId());
        intent.putExtra("repeats", ((ExerciseIt) item).getNoOfRepetitions());
        intent.putExtra("repetition_type", ((ExerciseIt) item).getRepetitionType());
        intent.putExtra("time", item.getAllTimes());
        intent.putExtra("exercise_name", item.getName());
        intent.putExtra("days", item.getDays());
        Uri uri = item.getUri();
        if (uri!=null)
            intent.putExtra("RecordedUri", item.getUri().toString());
        intent.putExtra("AlertSoundUri", item.getAlertSoundUri());
        return intent;
    }

    @Override
    protected void changeViewHolder(final CustomViewHolder customViewHolder, final ItemGeneral item, final Resources resources) {
        customViewHolder.lbItemNoOfRepeats.setText(String.valueOf(((ExerciseIt) item).getNoOfRepetitions()));
        customViewHolder.tvItemName.setText(item.getName());
        if (item.getUri() == null)
            customViewHolder.btPlay.setVisibility(View.INVISIBLE);

        String times = item.getTime().replace(resources.getString(R.string.times_splitter), resources.getString(R.string.times_nice_separator));
        if (!item.getDetailedTimes()) {
            customViewHolder.tvItemTimeMulti.setText(times);
            customViewHolder.tvItemTimeMulti.setVisibility(View.VISIBLE);
            customViewHolder.tvItemTime.setVisibility(View.GONE);
        } else {
            customViewHolder.tvItemTime.setText(times);
            customViewHolder.tvItemTime.setVisibility(View.VISIBLE);
            customViewHolder.tvItemTimeMulti.setVisibility(View.GONE);
        }

        String repetitionTypeInDB = ((ExerciseIt) item).getRepetitionType();
        if (repetitionTypeInDB != null) {
            int repetitionType = Integer.parseInt(((ExerciseIt) item).getRepetitionType());
            if (repetitionType == R.string.repetition_type_repetitions ) {
                customViewHolder.ivRepetition.setVisibility(View.VISIBLE);
            }
            else {
                customViewHolder.ivRepetition.setVisibility(View.GONE);
                String repetitionTypeS = Utils.stringOrFromResource(resources,repetitionTypeInDB );
                customViewHolder.txItemDosageType.setText(repetitionTypeS);
            }
        }

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

}
