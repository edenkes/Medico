package bredesh.medico.Fragments.DataMediGo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.R;
import bredesh.medico.Utils.Utils;

/**
 * Created by edenk on 12/10/2017.
 */

public class MedicineDa extends DataGeneral implements IRemoveLastAlert{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_medicine_data);

//        setUpOnCrate();
        setFindView();

        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource (this, R.array.drugs_dosage, R.layout.spinner_item );
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spType.setAdapter(adapterType);

        ArrayAdapter<CharSequence> adapterSpecial = ArrayAdapter.createFromResource(this, R.array.drugs_dosage_notes, R.layout.spinner_item);
        adapterSpecial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spSpecial.setAdapter(adapterSpecial);

        etNotes.setMovementMethod(new ScrollingMovementMethod());
        etNotes.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                etNotes.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });

        setOnCreate();
    }

/*
    protected void setUpOnCrate(){
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("");
        actionBar.setLogo(R.mipmap.ic_medigo_logo_clock);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        RecyclerView timeViews = findViewById(R.id.time_views);
        timeViews.setLayoutManager(new LinearLayoutManager(this));

        resources = getResources();
        btConfirm = findViewById(R.id.btConfirm);
        btDelete = findViewById(R.id.btDelete);
        tvDataName = findViewById(R.id.etExerciseName);
        tvSelectedDays = findViewById(R.id.lblSelectedDays);
        tvSelectedDays.setMovementMethod(new ScrollingMovementMethod());

        btPlayImage = findViewById(R.id.btPlayImage);

        alertPlanButtons[0] = findViewById(R.id.bt1time);
        alertPlanButtons[1] = findViewById(R.id.bt2times);
        alertPlanButtons[2] = findViewById(R.id.bt3times);
        alertPlanButtons[3] = findViewById(R.id.bt4times);
        alertPlanButtons[4] = findViewById(R.id.bt5times);
        btAddAlert = findViewById(R.id.btAddAlert);
        tvAddMultiAlert = findViewById(R.id.lbAddMultiAlert);

        etAmount = findViewById(R.id.amount_number);

        spType = findViewById(R.id.spinner_type);

        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource (this, R.array.drugs_dosage, R.layout.spinner_item );
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spType.setAdapter(adapterType);

        spSpecial = findViewById(R.id.spinner_special);
        ArrayAdapter<CharSequence> adapterSpecial = ArrayAdapter.createFromResource(this, R.array.drugs_dosage_notes, R.layout.spinner_item);
        adapterSpecial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spSpecial.setAdapter(adapterSpecial);


        etNotes = findViewById(R.id.et_notes);
        etNotes.setMovementMethod(new ScrollingMovementMethod());
        etNotes.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                etNotes.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });



        final AlertDialog reShootConfirm = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onReshootConfirmStill)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.reshootPic)).create();


        askBeforeSave = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onConfirm)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), onCancel)
                .setMessage(resources.getString(R.string.save_changes)).create();

        ImageButton btShootImage = findViewById(R.id.btShootImage);
        btShootImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uriStringImage != null)
                    reShootConfirm.show();
                else
                    ShootImage();
            }
        });


        db = new MedicoDB(getApplicationContext());
        Intent intent = getIntent();
        oldUriStringImage = uriStringImage = intent.getStringExtra("uriImage");

        this.dataId = intent.getIntExtra("medicineId", NewData);
        if (this.dataId != NewData)
        {
            setExistingData(intent);
        }
        else {
            arrayList = new ArrayList<>();
            // arrayList.add(makeTimeString());
            for(int i=0; i<selectedDays.length; i++)
                selectedDays[i] = true;
            oldDosageType = Integer.toString (R.string.medicine_dosage_type_tab);
            oldSpecialNotes = Integer.toString(R.string.medicine_usage_notes_none);
            oldDays = new int[7];
            for (int i=0 ;i < 7; i++)
                oldDays[i] = 1;
        }

        Button [] buttons = new Button[] {btConfirm, btDelete};


        timeAdapter = new TimeAdapterRecyclerMedGo(MedicineDa.this,arrayList, buttons, this);
        timeViews.setAdapter(timeAdapter);
        setDialog();

        tvSelectedDays.setOnClickListener(clickHandler);

        for (int i=0; i< 5; i++)
            alertPlanButtons[i].setOnClickListener(setAlertPlan);

        if (uriStringImage == null)
            btPlayImage.setVisibility(View.INVISIBLE);
        else {
            Glide.with(this).load(uriStringImage).into(btPlayImage);
            btPlayImage.invalidate();
        }
        btPlayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                Uri imageUri = Uri.parse(uriStringImage);
                if (imageUri != null) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(imageUri,"image*//*
*/
/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (RuntimeException e) {
                        Toast.makeText(context.getApplicationContext(),
                                resources.getString(R.string.media_not_found_image), Toast.LENGTH_SHORT).show();
                    }
                } else Toast.makeText(context.getApplicationContext(),
                        resources.getString(R.string.media_not_found_image), Toast.LENGTH_SHORT).show();
            }
        });

        btConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        final AlertDialog deleteDialog = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onDelete)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.delete_medicine_confirm)).create();

        btDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteDialog.show();
            }
        });

        // updating the list + adding another alert
        btAddAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arrayList.add(makeTimeString());
                timeAdapter.notifyItemInserted(arrayList.size() - 1);
            }
        });
    }
*/

    @Override
    protected void setExistingData(Intent intent) {
        oldDataName = intent.getStringExtra("dataName");
        oldTimes = intent.getStringExtra("dataTime");
        oldDays = intent.getIntArrayExtra("dataDays");
        oldUriStringVideo = intent.getStringExtra("dataUriVideo");
        oldUriStringImage = intent.getStringExtra("dataUriImage");

        String type = intent.getStringExtra("medicine_type");
        String special = intent.getStringExtra("medicine_special");
        oldNotes = intent.getStringExtra("medicine_notes");
        oldAmount = intent.getStringExtra("medicine_amount");

        etDataName.setText(oldDataName);
        oldDosageType = type;
        int index;
        index = Utils.findIndexInResourcesArray(resources, R.array.drugs_dosage, type);
        spType.setSelection(index);

        oldSpecialNotes = special;
        index = Utils.findIndexInResourcesArray(resources, R.array.drugs_dosage_notes, special);
        spSpecial.setSelection(index);

        etNotes.setText(oldNotes);
        etAmount.setText(oldAmount);

//        oldTimes = times;
        String[] timeAL = null;
        arrayList = new ArrayList<>();
        if (oldTimes != null && !oldTimes.contentEquals("")) {
            timeAL = oldTimes.split(resources.getString(R.string.times_splitter));
            Collections.addAll(arrayList, timeAL);
        }
//        oldDays = days;
        for (int i=0; i< 7; i++)
            selectedDays[i] = oldDays[i] != 0;
        updateSelectedDays();
        this.setAddAlertsButtons(timeAL == null || timeAL.length == 0);

    }

    @Override
    protected String getDeletedMessage() {
        return  getResources().getString(R.string.medicine_deleted);
    }

    @Override
    protected void setNewData() {
        arrayList = new ArrayList<>();
        for(int i=0; i<selectedDays.length; i++)
            selectedDays[i] = true;
        oldDosageType = Integer.toString (R.string.medicine_dosage_type_tab);
        oldSpecialNotes = Integer.toString(R.string.medicine_usage_notes_none);
        oldDays = new int[7];
        for (int i=0 ;i < 7; i++)
            oldDays[i] = 1;
    }

    @Override
    protected CharSequence getDeletedMessageConfirm() {
        return resources.getString(R.string.delete_medicine_confirm);
    }

    @Override
    protected TimeAdapterRecyclerMedGo getNewTimeAdapter(Button[] buttons) {
        return new TimeAdapterRecyclerMedGo(MedicineDa.this,arrayList, buttons, this);
    }

    @Override
    protected void confirm(boolean askBeforeSave) {
        if(etDataName.getText().toString().length() == 0 && !askBeforeSave) {
            Toast.makeText(getApplicationContext(), resources.getString(R.string.name_too_short_medicine), Toast.LENGTH_SHORT).show();
            return;
        }
        if(etAmount.getText().toString().length() == 0 && !askBeforeSave)
        {
            Toast.makeText(getApplicationContext(), resources.getString(R.string.amount_too_short_medicine), Toast.LENGTH_SHORT).show();
        }
        else {
            if (etDataName.getText().toString().length() <= Max_Size) {
                int[] days_to_alert = new int[selectedDays.length];
                for (int i = 0; i < days_to_alert.length; i++) {
                    if (selectedDays[i])
                        days_to_alert[i] = 1;
                    else
                        days_to_alert[i] = 0;
                }
                String times = "";
                Collections.sort(arrayList);
                for (int i = 0; i < arrayList.size(); i++)
                    times = times + (i > 0 ? getResources().getString(R.string.times_splitter) : "") + arrayList.get(i);

                String typeToWrite = Utils.findResourceIdInResourcesArray(resources, R.array.drugs_dosage, spType.getSelectedItem().toString());
                String specialNotesToWrite =Utils.findResourceIdInResourcesArray(resources, R.array.drugs_dosage_notes, spSpecial.getSelectedItem().toString());
                String medicineName = etDataName.getText().toString();
                String newNotes = etNotes.getText().toString();
                String amountText = etAmount.getText().toString();
                int amount = Integer.parseInt(amountText);

                if (askBeforeSave)
                {
                    boolean dataNotChanged = (
                            oldDataName.equals(medicineName) &&
                                    oldTimes.equals(times) &&
                                    oldSpecialNotes.equals(specialNotesToWrite) &&
                                    oldNotes.equals(newNotes) &&
                                    oldAmount.equals(amountText) &&
                                    (oldUriStringImage == null ? uriStringImage == null : oldUriStringImage.equals(uriStringImage)) &&
                                    Arrays.equals(oldDays, days_to_alert) &&
                                    oldDosageType.equals(typeToWrite)
                    );
                    if (dataNotChanged)
                        finish();
                    else
                        askUserBeforeSave();
                    return;

                }

                Bundle bundle = new Bundle();
                if (dataId != NewData) {
                    db.updateRow(dataId, medicineName, times, amount, "" , null, uriStringImage, days_to_alert, null);
                    db.updateMedicine(dataId,
                            typeToWrite,
                            specialNotesToWrite,
                            newNotes,
                            amount);
                    mFirebaseAnalytics.logEvent("Medicine_updated", bundle);
                }
                else
                {
                    long alertId = db.addAlert(etDataName.getText().toString(), MedicoDB.KIND.Medicine, times, Integer.parseInt(etAmount.getText().toString()), "",null, uriStringImage, days_to_alert, null);
                    db.addMedicine(alertId, typeToWrite,
                            specialNotesToWrite,
                            etNotes.getText().toString(),
                            Integer.parseInt(etAmount.getText().toString()));
                    mFirebaseAnalytics.logEvent("Medicine_added", bundle);
                }
                finish();
            } else
                Toast.makeText(getApplicationContext(), resources.getString(R.string.name_too_long), Toast.LENGTH_SHORT).show();
        }

    }
}
