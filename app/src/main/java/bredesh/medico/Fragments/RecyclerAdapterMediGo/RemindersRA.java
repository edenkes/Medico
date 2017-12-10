package bredesh.medico.Fragments.RecyclerAdapterMediGo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import bredesh.medico.Fragments.DataMediGo.RemindersDa;
import bredesh.medico.Fragments.ItemMediGo.ItemGeneral;
import bredesh.medico.Fragments.ItemMediGo.RemindersIt;
import bredesh.medico.R;

/**
 * Created by edenk on 12/10/2017.
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
        Uri uriStill = item.uriVideo;
        if (uriStill!=null)
            intent.putExtra("RecordedUri", item.uriVideo.toString());
        intent.putExtra("AlertSoundUri", item.getAlertSoundUri());
        return intent;
    }

    @Override
    protected void changeViewHolder(final CustomViewHolder customViewHolder, final ItemGeneral item, final Resources resources) {
        customViewHolder.ivRepetition.setVisibility(View.INVISIBLE);

        if (item.uriVideo == null)
            customViewHolder.btPlay.setVisibility(View.INVISIBLE);
        else {
            customViewHolder.btPlay.setScaleType(ImageView.ScaleType.FIT_CENTER);
            customViewHolder.btPlay.setBackground(null);
            Glide.with(activity).load(item.uriVideo).into(customViewHolder.btPlay);
            customViewHolder.btPlay.invalidate();
        }

        customViewHolder.btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri imageUri = item.uriVideo;
                if(imageUri != null ) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(imageUri,"image*//*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }catch (RuntimeException e){
                        Toast.makeText(context.getApplicationContext(),
                                resources.getString(R.string.media_not_found_image), Toast.LENGTH_SHORT).show();
                    }
                }
                else    Toast.makeText(context.getApplicationContext(),
                        resources.getString(R.string.media_not_found_image), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
