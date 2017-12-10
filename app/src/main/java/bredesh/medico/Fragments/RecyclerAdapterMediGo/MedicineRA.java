package bredesh.medico.Fragments.RecyclerAdapterMediGo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import bredesh.medico.Fragments.DataMediGo.MedicineDa;
import bredesh.medico.Fragments.ItemMediGo.ItemGeneral;
import bredesh.medico.Fragments.ItemMediGo.MedicineIt;
import bredesh.medico.R;
import bredesh.medico.Utils.Utils;

/**
 * Created by edenk on 12/10/2017.
 */

public class MedicineRA extends RecyclerAdapterGeneral<MedicineIt> {
    public MedicineRA(Context context, List<MedicineIt> itemGeneral, Activity activity) {
        super(context,itemGeneral, activity);
    }
/*
    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        final MedicineIt item = medicineItems.get(i);
        final int position = i;
        customViewHolder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MedicineDa.class);
                intent.putExtra("medicineId", item.id);
                intent.putExtra("medicine_amount", item.amount);
                intent.putExtra("time", item.allTimes);
                intent.putExtra("medicine_name", item.name);
                intent.putExtra("days", item.days);
                intent.putExtra("medicine_type", item.type);
                intent.putExtra("medicine_special", item.special);
                intent.putExtra("medicine_notes", item.notes);

                Uri uri = item.uriVideo;
                if (uri!=null)
                    intent.putExtra("RecordedUri", item.uriVideo.toString());

                activity.startActivityForResult(intent, 0x1987);
            }
        });

        customViewHolder.amount.setImageResource(R.drawable.ic_pill);

        final Resources resources = context.getResources();

        if (item.uriVideo == null)
            customViewHolder.play.setVisibility(View.INVISIBLE);
        else {
            customViewHolder.play.setScaleType(ImageView.ScaleType.FIT_CENTER);
            customViewHolder.play.setBackground(null);
            Glide.with(activity).load(item.uriVideo).into(customViewHolder.play);
            customViewHolder.play.invalidate();
        }

        customViewHolder.play.setOnClickListener(new View.OnClickListener() {
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

        DialogInterface.OnClickListener onDelete = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                MedicoDB db = new MedicoDB(context);
                db.deleteRow(item.id);
                medicineItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, medicineItems.size());
            }
        };

        final AlertDialog deleteDialog = new AlertDialog.Builder(activity)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onDelete)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.delete_medicine_confirm)).create();


        customViewHolder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteDialog.show();
                return true;
            }
        });


        activateAlerts(customViewHolder, item);


    }*/

    @Override
    protected Intent sendIntetInformation(ItemGeneral item) {Intent intent = new Intent(context, MedicineDa.class);
        intent.putExtra("medicineId", item.id);
        intent.putExtra("medicine_amount", ((MedicineIt) item).amount);
        intent.putExtra("time", item.allTimes);
        intent.putExtra("medicine_name", item.name);
        intent.putExtra("days", item.days);
        intent.putExtra("medicine_type", ((MedicineIt) item).type);
        intent.putExtra("medicine_special", ((MedicineIt) item).special);
        intent.putExtra("medicine_notes", ((MedicineIt) item).notes);

        Uri uri = item.uriVideo;
        if (uri!=null)
            intent.putExtra("RecordedUri", item.uriVideo.toString());
        return intent;
    }
    @Override
    protected void changeViewHolder(CustomViewHolder customViewHolder, ItemGeneral item, Resources resources) {
        String itemType = Utils.stringOrFromResource(resources, ((MedicineIt) item).type);
        customViewHolder.btPlay.setImageResource(R.drawable.ic_pill);
        customViewHolder.ivRepetition.setImageResource(R.drawable.ic_pill);

        if (item.uriVideo == null)
            customViewHolder.btPlay.setVisibility(View.INVISIBLE);
        else {
            customViewHolder.btPlay.setScaleType(ImageView.ScaleType.FIT_CENTER);
            customViewHolder.btPlay.setBackground(null);
            Glide.with(activity).load(item.uriVideo).into(customViewHolder.btPlay);
            customViewHolder.btPlay.invalidate();
        }

        if (itemType.equals(resources.getString(R.string.medicine_dosage_other))) {
            customViewHolder.lbItemNoOfRepeats.setText("");
            customViewHolder.txItemDosageType.setText("");
        }
        else {
            customViewHolder.lbItemNoOfRepeats.setText(String.valueOf(((MedicineIt) item).amount));
            customViewHolder.txItemDosageType.setText(itemType);
        }

        customViewHolder.tvItemName.setText(item.name);

        String times = item.time.replace(resources.getString(R.string.times_splitter), resources.getString(R.string.times_nice_separator));
        customViewHolder.tvItemTime.setText(times);
        if (!item.detailedTimes)
            customViewHolder.tvItemTime.setTextDirection(View.TEXT_DIRECTION_RTL);
    }



  /*  class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMedicineName, tvMedicineTime, tvMedicineAmount, txMedicineDosageType;
        private TextView tvSUN, tvMON, tvTUE, tvWED, tvTHU, tvFRI, tvSAT;
        private TextView[] days;
        private ImageButton play;
        private ImageView amount;
        View v;

        private CustomViewHolder(View convertView) {
            super(convertView);
            this.v = convertView;
            this.tvMedicineName = convertView.findViewById(R.id.tvItemName);
            this.tvMedicineTime = convertView.findViewById(R.id.tvItemTime);
            this.txMedicineDosageType = convertView.findViewById(R.id.txItemDosageType);
            this.tvSUN = convertView.findViewById(R.id.tvSUN);
            this.tvMON = convertView.findViewById(R.id.tvMON);
            this.tvTUE = convertView.findViewById(R.id.tvTUE);
            this.tvWED = convertView.findViewById(R.id.tvWED);
            this.tvTHU = convertView.findViewById(R.id.tvTHU);
            this.tvFRI = convertView.findViewById(R.id.tvFRI);
            this.tvSAT = convertView.findViewById(R.id.tvSAT);
            this.tvMedicineAmount = convertView.findViewById(R.id.lbItemNoOfRepeats);
            this.play = convertView.findViewById(R.id.btPlay);

            this.play.setImageResource(R.drawable.ic_pill);
            this.days = new TextView[]{tvSUN, tvMON, tvTUE, tvWED, tvTHU, tvFRI, tvSAT};
            this.amount = convertView.findViewById(R.id.ivRepetition);
        }
    }
*/

}
