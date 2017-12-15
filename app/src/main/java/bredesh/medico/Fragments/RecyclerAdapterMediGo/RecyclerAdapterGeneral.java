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

import com.bumptech.glide.Glide;

import java.util.List;

import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.Fragments.ItemMediGo.ItemGeneral;
import bredesh.medico.R;

/**
 * Created by edenk on 12/10/2017.
 * The Recycler Adapter Class contain Features that common for all fragments in the app,
 * And responsible for the look of each CARD.
 *
 * for extend this abstract class RecyclerAdapterGeneral<T>, needed to implements:
 *
 *     protected abstract void changeViewHolder(final CustomViewHolder customViewHolder, ItemGeneral item,final Resources resources);
 *
 *     protected abstract Intent sendIntetInformation(ItemGeneral item);
 **/

public abstract class RecyclerAdapterGeneral<T> extends RecyclerView.Adapter<RecyclerAdapterGeneral.CustomViewHolder>{
    private List<T> itemGeneral;
    protected Context context;
    protected Activity activity;

    RecyclerAdapterGeneral(Context context, List<T> itemGeneral, Activity activity) {
        this.itemGeneral = itemGeneral;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public RecyclerAdapterGeneral.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_exercise_item, viewGroup, false);
        return new RecyclerAdapterGeneral.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapterGeneral.CustomViewHolder customViewHolder, int i) {
        final ItemGeneral item = (ItemGeneral) itemGeneral.get(i);
        final int position = i;
        customViewHolder.convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = sendIntetInformation(item);
                activity.startActivityForResult(intent, 0x1987);
            }
        });

        DialogInterface.OnClickListener onDelete = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                MedicoDB db = new MedicoDB(context);
                db.deleteRow(item.getId());
                itemGeneral.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, itemGeneral.size());
            }
        };

        final Resources resources = context.getResources();

        final AlertDialog deleteDialog = new AlertDialog.Builder(activity)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onDelete)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.delete_exercise_confirm)).create();


        customViewHolder.convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteDialog.show();
                return true;
            }
        });

        customViewHolder.tvItemName.setText(item.name);

        if (item.getUriVideo() == null) {
            customViewHolder.btPlayVideo.setVisibility(View.INVISIBLE);
        }

        if (item.getUriImage() == null)
            customViewHolder.btPlayImage.setVisibility(View.INVISIBLE);
        else {
            customViewHolder.btPlayImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            customViewHolder.btPlayImage.setBackground(null);
            Glide.with(activity).load(item.uriImage).into(customViewHolder.btPlayImage);
            customViewHolder.btPlayImage.invalidate();
        }

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

        customViewHolder.btPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri videoUri = item.getUriVideo();
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
        changeViewHolder(customViewHolder, item, resources);

        activateAlerts(customViewHolder, item);
    }

    protected abstract void changeViewHolder(final CustomViewHolder customViewHolder, ItemGeneral item,final Resources resources);

    protected abstract Intent sendIntetInformation(ItemGeneral item);

    private void activateAlerts(RecyclerAdapterGeneral.CustomViewHolder viewHolder, ItemGeneral item) {
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
            viewHolder.tvItemTime.setTextColor(ContextCompat.getColor(context, R.color.labelColor));
        else
            viewHolder.tvItemTime.setTextColor(ContextCompat.getColor(context, R.color.colorGreyLite));
    }

    @Override
    public int getItemCount() {
        return (null != itemGeneral ? itemGeneral.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView[] days;
        TextView tvItemName, tvItemTime, tvItemTimeMulti,  tvNumberOfRepeats, tvDosageType;
        TextView tvSUN, tvMON, tvTUE, tvWED, tvTHU, tvFRI, tvSAT;
        ImageButton btPlayImage, btPlayVideo;
        ImageView ivRepetition;
        View convertView;

        protected CustomViewHolder(View convertView) {
            super(convertView);
            this.convertView = convertView;
            this.tvItemName = convertView.findViewById(R.id.tvDataName);
            this.tvItemTime = convertView.findViewById(R.id.tvDataTime);
            this.tvItemTimeMulti = convertView.findViewById(R.id.tvDataTimeMulti);

            this.tvDosageType = convertView.findViewById(R.id.tvDosageType);
            this.tvNumberOfRepeats = convertView.findViewById(R.id.tvNumberOfRepeats);
            this.btPlayImage = convertView.findViewById(R.id.btPlayImage);
            this.btPlayVideo = convertView.findViewById(R.id.btPlayVideo);
            this.ivRepetition = convertView.findViewById(R.id.ivRepetition);

            this.tvSUN = convertView.findViewById(R.id.tvSUN);
            this.tvMON = convertView.findViewById(R.id.tvMON);
            this.tvTUE = convertView.findViewById(R.id.tvTUE);
            this.tvWED = convertView.findViewById(R.id.tvWED);
            this.tvTHU = convertView.findViewById(R.id.tvTHU);
            this.tvFRI = convertView.findViewById(R.id.tvFRI);
            this.tvSAT = convertView.findViewById(R.id.tvSAT);
            this.days = new TextView[]{tvSUN,tvMON,tvTUE,tvWED,tvTHU,tvFRI,tvSAT};
        }
    }
}
