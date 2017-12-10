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
import bredesh.medico.Fragments.ItemMediGo.ItemGeneral;
import bredesh.medico.R;

/**
 * Created by edenk on 12/10/2017.
 */

abstract class RecyclerAdapterGeneral<T> extends RecyclerView.Adapter<RecyclerAdapterGeneral.CustomViewHolder>{
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
//    public abstract void onBindViewHolder(final RecyclerAdapterGeneral.CustomViewHolder customViewHolder, int i);

    @Override
    public void onBindViewHolder(final RecyclerAdapterGeneral.CustomViewHolder customViewHolder, int i) {
        final ItemGeneral item = (ItemGeneral) itemGeneral.get(i);
        final int position = i;
        customViewHolder.v.setOnClickListener(new View.OnClickListener() {
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


        customViewHolder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteDialog.show();
                return true;
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
        TextView tvItemName, tvItemTime, tvItemTimeMulti,  lbItemNoOfRepeats, txItemDosageType;
        TextView tvSUN, tvMON, tvTUE, tvWED, tvTHU, tvFRI, tvSAT;
        protected ImageButton btPlay;
        protected TextView[] days;
        ImageView ivRepetition;
        View v;

        protected CustomViewHolder(View convertView) {
            super(convertView);
            this.v = convertView;
            this.tvItemName = convertView.findViewById(R.id.tvItemName);
            this.tvItemTime = convertView.findViewById(R.id.tvItemTime);
            this.tvItemTimeMulti = convertView.findViewById(R.id.tvItemTimeMulti);

            this.txItemDosageType = convertView.findViewById(R.id.txItemDosageType);
            this.lbItemNoOfRepeats = convertView.findViewById(R.id.lbItemNoOfRepeats);
            this.btPlay = convertView.findViewById(R.id.btPlay);
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
