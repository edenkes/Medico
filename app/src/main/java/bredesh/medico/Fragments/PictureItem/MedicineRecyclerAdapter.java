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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import bredesh.medico.Camera.MedicineData;
import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.R;
import bredesh.medico.Utils.Utils;

/**
 * Created by Omri on 13/06/2017.
 */

public class MedicineRecyclerAdapter extends RecyclerView.Adapter<MedicineRecyclerAdapter.CustomViewHolder> {
    private List<MedicineItem> medicineItems;
    private Context context;
    private Activity activity;

    public MedicineRecyclerAdapter(Context context, List<MedicineItem> medicineItems, Activity activity) {
        this.medicineItems = medicineItems;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public MedicineRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_exercise_item, viewGroup, false);
        return new MedicineRecyclerAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MedicineRecyclerAdapter.CustomViewHolder customViewHolder, int i) {
        final MedicineItem item = medicineItems.get(i);
        final int position = i;
        customViewHolder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MedicineData.class);
                intent.putExtra("medicineId", item.id);
                intent.putExtra("medicine_amount", item.amount);
                intent.putExtra("time", item.allTimes);
                intent.putExtra("medicine_name", item.name);
                intent.putExtra("days", item.days);
                intent.putExtra("medicine_type", item.type);
                intent.putExtra("medicine_special", item.special);
                intent.putExtra("medicine_notes", item.notes);

                Uri uri = item.uri;
                if (uri!=null)
                    intent.putExtra("RecordedUri", item.uri.toString());

                activity.startActivityForResult(intent, 0x1987);
            }
        });

        customViewHolder.amount.setImageResource(R.drawable.ic_pill);

        final Resources resources = context.getResources();

        if (item.uri == null)
            customViewHolder.play.setVisibility(View.INVISIBLE);
        else {
            customViewHolder.play.setScaleType(ImageView.ScaleType.FIT_CENTER);
            customViewHolder.play.setBackground(null);
            Glide.with(activity).load(item.uri).into(customViewHolder.play);
            customViewHolder.play.invalidate();
        }

        customViewHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri imageUri = item.uri;
                if(imageUri != null ) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(imageUri,"image/*");
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

        String itemType = Utils.stringOrFromResource(resources, item.type);

        if (itemType.equals(resources.getString(R.string.medicine_dosage_other))) {
            customViewHolder.tvMedicineAmount.setText("");
            customViewHolder.txMedicinedosageType.setText("");
        }
        else {
            customViewHolder.tvMedicineAmount.setText(String.valueOf(item.amount));
            customViewHolder.txMedicinedosageType.setText(itemType);
        }

        customViewHolder.tvMedicineName.setText(item.name);

        String times = item.time.replace(resources.getString(R.string.times_splitter), resources.getString(R.string.times_nice_separator));
        customViewHolder.tvMedicineTime.setText(times);
        if (!item.detailedTimes)
            customViewHolder.tvMedicineTime.setTextDirection(View.TEXT_DIRECTION_RTL);

        activateAlerts(customViewHolder, item);


    }


    private void activateAlerts(MedicineRecyclerAdapter.CustomViewHolder viewHolder, MedicineItem item) {
        int[] days = item.days;
        boolean isActive = false;
        for (int i = 0; i < days.length; i++) {
            if (days[i] == 1) {
                viewHolder.days[i].setTextColor(ContextCompat.getColor(context, R.color.titleColor));
                isActive = true;
            } else
                viewHolder.days[i].setTextColor(ContextCompat.getColor(context, R.color.colorGreyLite));

        }
        if (isActive)
            viewHolder.tvMedicineTime.setTextColor(ContextCompat.getColor(context, R.color.labelColor));
        else
            viewHolder.tvMedicineTime.setTextColor(ContextCompat.getColor(context, R.color.colorGreyLite));
    }


    @Override
    public int getItemCount() {
        return (null != medicineItems ? medicineItems.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMedicineName, tvMedicineTime, tvMedicineAmount, txMedicinedosageType;
        private TextView tvSUN, tvMON, tvTUE, tvWED, tvTHU, tvFRI, tvSAT;
        private TextView[] days;
        private ImageButton play;
        private ImageView amount;
        View v;

        private CustomViewHolder(View convertView) {
            super(convertView);
            this.v = convertView;
            this.tvMedicineName = (TextView) convertView.findViewById(R.id.tvExercisesName);
            this.tvMedicineTime = (TextView) convertView.findViewById(R.id.tvExerciseTime);
            this.txMedicinedosageType = (TextView) convertView.findViewById(R.id.txMedicinedosageType);
            this.tvSUN = (TextView) convertView.findViewById(R.id.tvSUN);
            this.tvMON = (TextView) convertView.findViewById(R.id.tvMON);
            this.tvTUE = (TextView) convertView.findViewById(R.id.tvTUE);
            this.tvWED = (TextView) convertView.findViewById(R.id.tvWED);
            this.tvTHU = (TextView) convertView.findViewById(R.id.tvTHU);
            this.tvFRI = (TextView) convertView.findViewById(R.id.tvFRI);
            this.tvSAT = (TextView) convertView.findViewById(R.id.tvSAT);
            this.tvMedicineAmount = (TextView) convertView.findViewById(R.id.lblExerciseNoOfRepeats);
            this.play = (ImageButton) convertView.findViewById(R.id.btPlay);

            this.play.setImageResource(R.drawable.ic_pill);
            this.days = new TextView[]{tvSUN, tvMON, tvTUE, tvWED, tvTHU, tvFRI, tvSAT};
            this.amount = (ImageView) convertView.findViewById(R.id.imageRepeat);
        }
    }
}
