package bredesh.medico.Fragments.RecyclerAdapterMediGo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.View;

import java.util.List;

import bredesh.medico.DAL.ValueConstants;
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
        intent.putExtra("dataId", item.getId());
        intent.putExtra("dataName", item.getName());
        intent.putExtra("dataTime", item.getAllTimes());
        intent.putExtra("dataDays", item.getDays());
        Uri uriVideo = item.getUriVideo();
        if (uriVideo!=null)
            intent.putExtra("dataUriVideo", uriVideo.toString());
        Uri uriImage = item.getUriImage();
        if (uriImage!=null)
            intent.putExtra("dataUriImage", uriImage.toString());
        intent.putExtra("dataAlertSoundUri", item.getAlertSoundUri());

        intent.putExtra("dataRepeats", ((ExerciseIt) item).getNoOfRepetitions());
        intent.putExtra("dataRepetitionType", ((ExerciseIt) item).getRepetitionType());
        return intent;
    }

    @Override
    protected void changeViewHolder(final CustomViewHolder customViewHolder, final ItemGeneral item, final Resources resources) {
        customViewHolder.tvNumberOfRepeats.setText(String.valueOf(((ExerciseIt) item).getNoOfRepetitions()));

        int repetitionType = ((ExerciseIt) item).getRepetitionType();
        if (repetitionType == ValueConstants.ExerciseRepetitionType.defaultValue ) {
            customViewHolder.ivRepetition.setVisibility(View.VISIBLE);
        }
        else {
            customViewHolder.ivRepetition.setVisibility(View.GONE);
            String repetitionTypeS = resources.getString(ValueConstants.ExerciseRepetitionType.getStringCodeFromDBCode(repetitionType));
            customViewHolder.tvDosageType.setText(repetitionTypeS);
        }
    }

}
