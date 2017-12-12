package bredesh.medico.Fragments.RecyclerAdapterMediGo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.view.View;

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

        Uri uriImage = item.uriImage;
        if (uriImage!=null)
            intent.putExtra("uriImage", item.uriImage.toString());
        return intent;
    }

    @Override
    protected void changeViewHolder(CustomViewHolder customViewHolder, ItemGeneral item, Resources resources) {
        String itemType = Utils.stringOrFromResource(resources, ((MedicineIt) item).type);
        customViewHolder.ivRepetition.setImageResource(R.drawable.ic_pill);

        if (itemType.equals(resources.getString(R.string.medicine_dosage_other))) {
            customViewHolder.tvNumberOfRepeats.setText("");
            customViewHolder.tvDosageType.setText("");
        }
        else {
            customViewHolder.tvNumberOfRepeats.setText(String.valueOf(((MedicineIt) item).amount));
            customViewHolder.tvDosageType.setText(itemType);
        }

        String times = item.time.replace(resources.getString(R.string.times_splitter), resources.getString(R.string.times_nice_separator));
        customViewHolder.tvItemTime.setText(times);
        if (!item.detailedTimes)
            customViewHolder.tvItemTime.setTextDirection(View.TEXT_DIRECTION_RTL);
    }
}
