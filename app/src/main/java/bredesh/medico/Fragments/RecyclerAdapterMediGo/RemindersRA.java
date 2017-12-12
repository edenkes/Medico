package bredesh.medico.Fragments.RecyclerAdapterMediGo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.View;

import java.util.List;

import bredesh.medico.Fragments.DataMediGo.RemindersDa;
import bredesh.medico.Fragments.ItemMediGo.ItemGeneral;
import bredesh.medico.Fragments.ItemMediGo.RemindersIt;

/**
 * Created by edenK on 12/10/2017.
 */

public class RemindersRA extends RecyclerAdapterGeneral<RemindersIt> {
    public RemindersRA(Context context, List<RemindersIt> itemGeneral, Activity activity) {
        super(context, itemGeneral, activity);
    }

    @Override
    protected Intent sendIntetInformation(ItemGeneral item) {
        Intent intent = new Intent(context, RemindersDa.class);
        intent.putExtra("remindersId", item.id);
        intent.putExtra("time", item.allTimes);
        intent.putExtra("reminders_name", item.name);
        intent.putExtra("days", item.days);
        intent.putExtra("reminders_notes", ((RemindersIt) item).notes);
        Uri uriImage = item.uriImage;
        if (uriImage!=null)
            intent.putExtra("uriImage", uriImage.toString());
        Uri uriVideo = item.uriVideo;
        if (uriVideo!=null)
            intent.putExtra("uriVideo", uriVideo.toString());
        intent.putExtra("AlertSoundUri", item.getAlertSoundUri());
        return intent;
    }

    @Override
    protected void changeViewHolder(final CustomViewHolder customViewHolder, final ItemGeneral item, final Resources resources) {
        customViewHolder.ivRepetition.setVisibility(View.INVISIBLE);

    }

}
