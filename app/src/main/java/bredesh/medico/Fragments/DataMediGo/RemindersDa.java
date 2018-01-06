package bredesh.medico.Fragments.DataMediGo;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.R;

/**
 * Created by edenk on 12/10/2017.
 */
public class RemindersDa extends DataGeneral implements IRemoveLastAlert{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_reminders_data);

        setFindView();

        etNotes.setMovementMethod(new ScrollingMovementMethod());
        etNotes.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                etNotes.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });

        setOnCreate();

/*        setUpOnCrate();

        RecyclerView timeViews = findViewById(R.id.time_views);
        timeViews.setLayoutManager(new LinearLayoutManager(this));

        Button [] buttons = new Button[] {btConfirm, btDelete};

        timeAdapter = new TimeAdapterRecyclerMedGo(RemindersDa.this,arrayList, buttons, this);
        timeViews.setAdapter(timeAdapter);
        setDialog();*/
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

        resources = getResources();
        btConfirm = findViewById(R.id.btConfirm);
        btDelete = findViewById(R.id.btDelete);
        etDataName = findViewById(R.id.etDataName);
        tvSelectedDays = findViewById(R.id.lblSelectedDays);
        tvSelectedDays.setMovementMethod(new ScrollingMovementMethod());

        btPlayImage = findViewById(R.id.btPlayImage);
        btPlayVideo = findViewById(R.id.btPlayVideo);

        btChooseSound = findViewById(R.id.btChooseSound);

        alertPlanButtons[0] = findViewById(R.id.bt1time);
        alertPlanButtons[1] = findViewById(R.id.bt2times);
        alertPlanButtons[2] = findViewById(R.id.bt3times);
        alertPlanButtons[3] = findViewById(R.id.bt4times);
        alertPlanButtons[4] = findViewById(R.id.bt5times);
        btAddAlert = findViewById(R.id.btAddAlert);
        tvAddMultiAlert = findViewById(R.id.lbAddMultiAlert);

        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource (this, R.array.drugs_dosage, R.layout.spinner_item );
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        etNotes = findViewById(R.id.et_notes);
        etNotes.setMovementMethod(new ScrollingMovementMethod());
        etNotes.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                etNotes.getParent().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });



        final AlertDialog reShootConfirmStill = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onReshootConfirmStill)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.reshootPic)).create();

        final AlertDialog reShootConfirmVideo = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onReshootConfirmVideo)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.reshootPic)).create();


        askBeforeSave = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onConfirm)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), onCancel)
                .setMessage(resources.getString(R.string.save_changes)).create();

        ImageButton still = findViewById(R.id.btShootStill);
        still.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uriStringImage != null)
                    reShootConfirmStill.show();
                else
                    ShootImage();
            }
        });

        ImageButton video = findViewById(R.id.btShootVideo);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uriStringVideo != null)
                    reShootConfirmVideo.show();
                else
                    ShootVideo();
            }
        });


        db = new MedicoDB(getApplicationContext());
        Intent intent = getIntent();
        oldUriStringImage = uriStringImage = intent.getStringExtra("uriImage");
        oldUriStringVideo = uriStringVideo = intent.getStringExtra("uriVideo");
        oldAlertSoundUriString = alertSoundUriString = intent.getStringExtra("AlertSoundUri");

        this.dataId = intent.getIntExtra("remindersId", NewData);
        if (this.dataId != NewData) setExistingData(intent);
        else {
            arrayList = new ArrayList<>();
            for(int i=0; i<selectedDays.length; i++)
                selectedDays[i] = true;
            oldDays = new int[7];
            for (int i=0 ;i < 7; i++)
                oldDays[i] = 1;
        }

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

        if (uriStringVideo == null)
            btPlayVideo.setVisibility(View.INVISIBLE);

        btPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                Uri videoUri = Uri.parse(uriStringVideo);
                if (videoUri != null) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (RuntimeException e) {
                        Toast.makeText(context.getApplicationContext(),
                                resources.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                    }
                } else Toast.makeText(context.getApplicationContext(),
                        resources.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
            }
        });

        btPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                Uri videoUri = Uri.parse(uriStringVideo);
                if (videoUri != null) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (RuntimeException e) {
                        Toast.makeText(context.getApplicationContext(),
                                resources.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                    }
                } else Toast.makeText(context.getApplicationContext(),
                        resources.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
            }
        });

        btChooseSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseSound();
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
                .setMessage(resources.getString(R.string.delete_reminders_confirm)).create();

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

    protected void setExistingData(Intent intent){
        oldDataName = intent.getStringExtra("dataName");
        oldTimes = intent.getStringExtra("dataTime");
        oldDays = intent.getIntArrayExtra("dataDays");
        oldUriStringVideo = intent.getStringExtra("dataUriVideo");
        oldUriStringImage = intent.getStringExtra("dataUriImage");
        oldNotes = intent.getStringExtra("reminders_notes");

        etDataName.setText(oldDataName);
        String times = oldTimes;
        String[] timeAL = null;
        arrayList = new ArrayList<>();
        if (times != null && !times.contentEquals("")) {
            timeAL = times.split(resources.getString(R.string.times_splitter));
            Collections.addAll(arrayList, timeAL);
        }
        for (int i=0; i< 7; i++)
            selectedDays[i] = oldDays[i] != 0;
        updateSelectedDays();
        etNotes.setText(oldNotes);
        this.setAddAlertsButtons(timeAL == null || timeAL.length == 0);

    }

    @Override
    protected String getDeletedMessage() {
        return  getResources().getString(R.string.reminders_deleted);
    }

    @Override
    protected void setNewData() {
        arrayList = new ArrayList<>();
        for(int i=0; i<selectedDays.length; i++)
            selectedDays[i] = true;
        oldDays = new int[7];
        for (int i=0 ;i < 7; i++)
            oldDays[i] = 1;
    }

    @Override
    protected CharSequence getDeletedMessageConfirm() {
        return resources.getString(R.string.delete_reminders_confirm);
    }

    @Override
    protected TimeAdapterRecyclerMedGo getNewTimeAdapter(Button[] buttons) {
        return new TimeAdapterRecyclerMedGo(RemindersDa.this,arrayList, buttons, this);
    }

    protected void confirm(boolean askBeforeSave) {
        {
            if(etDataName.getText().toString().length() == 0 && !askBeforeSave) {
                Toast.makeText(getApplicationContext(), resources.getString(R.string.name_too_short_reminders), Toast.LENGTH_SHORT).show();
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
                    String remindersName = etDataName.getText().toString();
                    String newNotes = etNotes.getText().toString();

                    if (askBeforeSave)
                    {
                        boolean dataNotChanged = (
                                oldDataName.equals(remindersName) &&
                                        oldTimes.equals(times) &&
                                        oldNotes.equals(newNotes) &&
                                        (oldUriStringVideo == null ? uriStringVideo == null : oldUriStringVideo.equals(uriStringVideo)) &&
                                        (oldUriStringImage == null ? uriStringImage == null : oldUriStringImage.equals(uriStringImage)) &&
                                        (oldAlertSoundUriString == null ? alertSoundUriString == null : oldAlertSoundUriString.equals(alertSoundUriString)) &&
                                        Arrays.equals(oldDays, days_to_alert)
                        );
                        if (dataNotChanged)
                            finish();
                        else
                            askUserBeforeSave();
                        return;

                    }

                    Bundle bundle = new Bundle();
                    if (dataId != NewData) {
                        db.updateRow(dataId, remindersName, times, 0, "" ,  uriStringVideo, uriStringImage, days_to_alert, alertSoundUriString);
                        db.updateReminders(dataId, newNotes);
                        mFirebaseAnalytics.logEvent("Reminders_updated", bundle);
                    }
                    else
                    {
                        long alertId = db.addAlert(etDataName.getText().toString(), MedicoDB.KIND.Reminders, times, 0, "", uriStringVideo, uriStringImage, days_to_alert, alertSoundUriString);
                        db.addReminders(alertId, etNotes.getText().toString());
                        mFirebaseAnalytics.logEvent("Reminders_added", bundle);
                    }
                    finish();
                } else
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.name_too_long), Toast.LENGTH_SHORT).show();
            }
        }
    }
}