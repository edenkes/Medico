package bredesh.medico.Fragments.DataMediGo;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

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

public class ExerciseDa extends DataGeneral implements IRemoveLastAlert{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_exercises_data);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setLogo(R.mipmap.ic_medigo_logo_clock);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mainView = findViewById(R.id.vExerciseRoot);

        mainView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(mainView);
                return false;
            }
        });

        RecyclerView timeViews = findViewById(R.id.time_views);
        timeViews.setLayoutManager(new LinearLayoutManager(this));

        resources = getResources();
        btConfirm = findViewById(R.id.btConfirm);
        btDelete = findViewById(R.id.btDelete);

        InputMethodManager imm = (InputMethodManager) getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        etDataName = findViewById(R.id.etExerciseName);
        //this.setAutoCloseKeyboard(etExerciseName);

        etRepeats = findViewById(R.id.etRepeats);
        spRepetitionType = findViewById(R.id.spinner_repetition_type);
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource (this, R.array.repetition_types, R.layout.spinner_item );
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spRepetitionType.setAdapter(adapterType);

        //this.setAutoCloseKeyboard(etRepeats);
        lblSelectedDays = findViewById(R.id.lblSelectedDays);
        lblSelectedDays.setMovementMethod(new ScrollingMovementMethod());

        btPlayVideo = findViewById(R.id.btPlayVideo);
        btChooseSound = findViewById(R.id.btChooseSound);
        alertPlanButtons[0] = findViewById(R.id.bt1time);
        alertPlanButtons[1] = findViewById(R.id.bt2times);
        alertPlanButtons[2] = findViewById(R.id.bt3times);
        alertPlanButtons[3] = findViewById(R.id.bt4times);
        alertPlanButtons[4] = findViewById(R.id.bt5times);
        btAddAlert = findViewById(R.id.btAddAlert);
        lbAddMultiAlert = findViewById(R.id.lbAddMultiAlert);

        askBeforeSave = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onConfirm)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), onCancel)
                .setMessage(resources.getString(R.string.save_changes)).create();

        ImageButton video;

        final AlertDialog reShootConfirm = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onReshootConfirmVideo)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.reshootVideo)).create();

        video = findViewById(R.id.btShootVideo);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uriStringVideo != null)
                    reShootConfirm.show();
                else
                    ShootVideo();
            }
        });

        db = new MedicoDB(getApplicationContext());
        Intent intent = getIntent();
        oldUriStringImage = uriStringImage = intent.getStringExtra("uriImage");
        oldUriStringVideo = uriStringVideo = intent.getStringExtra("uriVideo");
        oldAlertSoundUriString = alertSoundUriString = intent.getStringExtra("AlertSoundUri");

        etRepeats.setText(Integer.toString(1));
        this.dataId = intent.getIntExtra("exerciseId", NewData);
        if (this.dataId != NewData) {setExistingData(intent);}
        else {
            arrayList = new ArrayList<>();
            // arrayList.add(makeTimeString());
            for(int i=0; i<selectedDays.length; i++) {
                selectedDays[i] = true;
                oldDays[i] = 1;
            }
        }

        Button [] buttons = new Button[] {btConfirm, btDelete};

        timeAdapter = new TimeAdapterRecyclerMedGo(ExerciseDa.this,arrayList, buttons, this);
        timeViews.setAdapter(timeAdapter);
        setDialog();

        lblSelectedDays.setOnClickListener(clickHandler);

        for (int i=0; i< 5; i++)
            alertPlanButtons[i].setOnClickListener(setAlertPlan);

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
                .setMessage(resources.getString(R.string.delete_exercise_confirm)).create();

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
                hideKeyboard(v);
                arrayList.add(makeTimeString());
                timeAdapter.notifyItemInserted(arrayList.size() - 1);
            }
        });
    }

    @Override
    protected void setExistingData(Intent intent) {
        oldDataName = intent.getStringExtra("exercise_name");
        etDataName.setText(oldDataName);

        oldTimes = intent.getStringExtra("time");
        String times = oldTimes;
        String[] timeAL = null;
        arrayList = new ArrayList<>();
        if (times != null && !times.contentEquals("")) {
            timeAL = times.split(resources.getString(R.string.times_splitter));
            Collections.addAll(arrayList, timeAL);
        }

        oldRepeats = intent.getIntExtra("repeats",1);
        int repeats = oldRepeats;
        etRepeats.setText(Integer.toString(repeats));

        String repetitionType = intent.getStringExtra("repetition_type");
        if (repetitionType == null)
            repetitionType = Integer.toString(R.string.repetition_type_repetitions);
        oldRepetitionType = repetitionType;
        int index;
        index = Utils.findIndexInResourcesArray(resources, R.array.repetition_types, repetitionType);
        spRepetitionType.setSelection(index);

        oldDays = intent.getIntArrayExtra("days");
        int[] days = oldDays;
        for (int i=0; i< 7; i++)
            selectedDays[i] = days[i] != 0;
        updateSelectedDays();
        this.setAddAlertsButtons(timeAL == null || timeAL.length == 0);

        oldUriStringVideo = intent.getStringExtra("uriVideo");
    }

    @Override
    protected String getDeletedMessage() {
        return  getResources().getString(R.string.exercise_deleted);
    }

    @Override
    protected void confirm(boolean askBeforeSave) {
        if(etDataName.getText().toString().length() == 0 && !askBeforeSave)
            Toast.makeText(getApplicationContext(), resources.getString(R.string.name_too_short), Toast.LENGTH_SHORT).show();
        else {
            if (etRepeats.getText().toString().length() == 0 && !askBeforeSave)
                Toast.makeText(getApplicationContext(), R.string.exercise_repeats_mandatory, Toast.LENGTH_LONG).show();
            else {
                if (etDataName.getText().toString().length() <= Max_Size) {
                    int[] days_to_alert = new int[selectedDays.length];
                    for (int i = 0; i < days_to_alert.length; i++) {
                        if (selectedDays[i])
                            days_to_alert[i] = 1;
                        else
                            days_to_alert[i] = 0;
                    }
                    int repeats = Integer.parseInt(etRepeats.getText().toString());
                    String times = "";
                    Collections.sort(arrayList);
                    for (int i = 0; i < arrayList.size(); i++)
                        times = times + (i > 0 ? getResources().getString(R.string.times_splitter) : "") + arrayList.get(i);

                    String exerciseName = etDataName.getText().toString();
                    String repetitionTypeToWrite = Utils.findResourceIdInResourcesArray(resources, R.array.repetition_types, spRepetitionType.getSelectedItem().toString());

                    if (askBeforeSave)
                    {
                        boolean dataNotChanged =
                                oldDataName.equals(exerciseName) &&
                                        oldRepeats == repeats &&
                                        oldTimes.equals(times) &&
                                        oldRepetitionType.equals(repetitionTypeToWrite) &&
                                        Arrays.equals(oldDays, days_to_alert) &&
                                        (oldUriStringVideo == null ? uriStringVideo == null : oldUriStringVideo.equals(uriStringVideo)) &&
                                        (oldAlertSoundUriString == null ? alertSoundUriString == null : oldAlertSoundUriString.equals(alertSoundUriString));
                        if (dataNotChanged)
                            finish();
                        else
                            askUserBeforeSave();
                        return;
                    }

                    Bundle params = new Bundle();
                    if (dataId != NewData) {
                        db.updateRow(dataId, exerciseName, times, repeats, repetitionTypeToWrite, uriStringVideo,uriStringImage, days_to_alert, alertSoundUriString);
                        mFirebaseAnalytics.logEvent("Excercise_updated", params);
                    }
                    else {
                        db.addAlert(exerciseName, MedicoDB.KIND.Exercise, times, repeats, repetitionTypeToWrite, uriStringVideo, uriStringImage, days_to_alert, alertSoundUriString);
                        mFirebaseAnalytics.logEvent("Excercise_added", params);
                    }
                    finish();
                } else
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.name_too_long), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
