package bredesh.medico.Fragments.DataMediGo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

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
        Button addAlert = findViewById(R.id.btAddAlert);

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
        if (this.dataId != NewData)
        {
            setExistingData(intent);
        }
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
        addAlert.setOnClickListener(new View.OnClickListener() {
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CHOOSE_ALERT_SOUND) {
            if (resultCode == RESULT_OK && intent != null) {
                Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                if (uri != null) {
                    this.alertSoundUriString = uri.toString();
                }
            }
        }
        else {
            if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK && intent != null) {
                if (intent.getData() != null) {
                    uriStringVideo = intent.getData().toString();
                    btPlayVideo.setVisibility(View.VISIBLE);
                    Toast.makeText(ExerciseDa.this.getApplicationContext(), resources.getString(R.string.AttachSuccess), Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Toast.makeText(ExerciseDa.this.getApplicationContext(), resources.getString(R.string.AttachFailed), Toast.LENGTH_LONG).show();
        }
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






    /*
    private EditText etExerciseName;
    private EditText etRepeats;
    private Spinner spRepetitionType;
    private ArrayList<String> arrayList;
    private TextView lblSelectedDays;
    private Button btDelete, btConfirm;

    private AlertDialog dialog;
    // array to keep the selected days
    private final boolean[] selectedDays = new boolean[7];
    private final boolean[] newSelectedDays = new boolean[7];
    private MedicoDB db;
    private final int Max_Size = 16;
    private Resources resources;
    private int exerciseId;
    private final int NewExercise = -6;
    private ImageButton btPlay;
    private ImageButton btChooseSound;
    private String videoUriString;
    private String alertSoundUriString;
    private final Button[] alertPlanButtons = new Button[5];
    private TimeAdapterRecyclerMedGo timeAdapter = null;
    private Button btAddAlert;
    private TextView lbAddMultiAlert;

    private final String[][] AlertPlans =
            {
                    {"07 : 00"},
                    {"07 : 00", "19 : 00"},
                    {"07 : 00", "12 : 00", "19 : 00"},
                    {"07 : 00", "12 : 00", "17 : 00", "21 : 00"},
                    {"07 : 00", "11 : 00", "15 : 00", "17 : 00", "21 : 00"}
            };


    private DialogInterface.OnClickListener onDelete = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            MedicoDB db = new MedicoDB(getApplicationContext());
            if (exerciseId != NewExercise)
                db.deleteRow(exerciseId);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.exercise_deleted) , Toast.LENGTH_LONG).show();
            finish();
        }
    };

    private DialogInterface.OnClickListener onReshootConfirm = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            ShootVideo();
        }
    };

    public void OnRemoveLastAlert()
    {
        this.setAddAlertsButtons(true);
    }

    private void setAddAlertsButtons(boolean alertPlan)
    {
        int visibleAlertPlan = alertPlan? View.VISIBLE : View.GONE;
        int invisibleAlertPlan = alertPlan? View.GONE : View.VISIBLE;
        btAddAlert.setVisibility(invisibleAlertPlan);
        lbAddMultiAlert.setVisibility(visibleAlertPlan);
        for (int i=0; i< 5; i++)
            alertPlanButtons[i].setVisibility(visibleAlertPlan);
    }

    private View.OnClickListener setAlertPlan = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            for (int i=0; i< 5; i++)
            {
                if (alertPlanButtons[i].getId() == view.getId())
                {
                    hideKeyboard(view);
                    arrayList.addAll(Arrays.asList(AlertPlans[i]).subList(0, i + 1));
                    timeAdapter.notifyItemInserted(arrayList.size() - 1);

                }
            }
            setAddAlertsButtons(false);
        }
    };

    private String oldExerciseName = "";
    private String oldTimes = "";
    private int oldRepeats = 1;
    private String oldRepetitionType = "";

    private int[] oldDays = new int[7];
    private String oldVideoUriString = null;
    private String oldAlertSoundUriString = null;

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setAutoCloseKeyboard(View v) {
        v.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    private void setExistingExercise(Intent intent)
    {

        oldExerciseName = intent.getStringExtra("exercise_name");
        etExerciseName.setText(oldExerciseName);

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
    }

    private View.OnClickListener clickHandler = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            System.arraycopy(selectedDays, 0, newSelectedDays, 0, 7);
            dialog.show();
            showButtons(false);
        }
    };

    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int CHOOSE_ALERT_SOUND = 5;

    private void ShootVideo()
    {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(ExerciseDa.this.getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void ChooseSound()
    {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
        Uri existingAlertSoundUri = alertSoundUriString == null ? null : Uri.parse(alertSoundUriString);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, existingAlertSoundUri);
        this.startActivityForResult(intent, CHOOSE_ALERT_SOUND);
    }


    private DialogInterface.OnClickListener onConfirm = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            confirm(false);
        }
    };

    private DialogInterface.OnClickListener onCancel = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            finish();
        }
    };

    private AlertDialog askBeforeSave = null;
    private FirebaseAnalytics mFirebaseAnalytics;
    private View mainView;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_exercises_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
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

        RecyclerView timeViews = (RecyclerView) findViewById(R.id.time_views);
        timeViews.setLayoutManager(new LinearLayoutManager(this));

        resources = getResources();
        btConfirm = (Button) findViewById(R.id.btConfirm);
        btDelete = (Button) findViewById(R.id.btDelete);
        Button addAlert = (Button) findViewById(R.id.btAddAlert);

        InputMethodManager imm = (InputMethodManager) getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        etExerciseName = (EditText) findViewById(R.id.etExerciseName);
        //this.setAutoCloseKeyboard(etExerciseName);


        etRepeats = (EditText) findViewById(R.id.etRepeats);
        spRepetitionType = (Spinner) findViewById(R.id.spinner_repetition_type);
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource (this, R.array.repetition_types, R.layout.spinner_item );
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spRepetitionType.setAdapter(adapterType);


        //this.setAutoCloseKeyboard(etRepeats);
        lblSelectedDays = (TextView) findViewById(R.id.lblSelectedDays);
        lblSelectedDays.setMovementMethod(new ScrollingMovementMethod());

        btPlay = (ImageButton) findViewById(R.id.btPlay);
        btChooseSound = (ImageButton) findViewById(R.id.btChooseSound);
        alertPlanButtons[0] = (Button) findViewById(R.id.bt1time);
        alertPlanButtons[1] = (Button) findViewById(R.id.bt2times);
        alertPlanButtons[2] = (Button) findViewById(R.id.bt3times);
        alertPlanButtons[3] = (Button) findViewById(R.id.bt4times);
        alertPlanButtons[4] = (Button) findViewById(R.id.bt5times);
        btAddAlert = (Button) findViewById(R.id.btAddAlert);
        lbAddMultiAlert = (TextView) findViewById(R.id.lbAddMultiAlert);


        askBeforeSave = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onConfirm)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), onCancel)
                .setMessage(resources.getString(R.string.save_changes)).create();

        ImageButton video;

        final AlertDialog reShootConfirm = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onReshootConfirm)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.reshootVideo)).create();


        video = (ImageButton) findViewById(R.id.btShootVideo);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoUriString != null)
                    reShootConfirm.show();
                else
                    ShootVideo();
            }
        });


        db = new MedicoDB(getApplicationContext());
        Intent intent = getIntent();
        oldVideoUriString = videoUriString = intent.getStringExtra("RecordedUri");
        oldAlertSoundUriString = alertSoundUriString = intent.getStringExtra("AlertSoundUri");
        etRepeats.setText(Integer.toString(1));
        this.exerciseId = intent.getIntExtra("exerciseId", NewExercise);
        if (this.exerciseId != NewExercise)
        {
            setExistingExercise(intent);
        }
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

        if (videoUriString == null)
            btPlay.setVisibility(View.INVISIBLE);
        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                Uri videoUri = Uri.parse(videoUriString);
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
        addAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                arrayList.add(makeTimeString());
                timeAdapter.notifyItemInserted(arrayList.size() - 1);
            }
        });
    }

    private void updateSelectedDays()
    {
        hideKeyboard(etExerciseName);
        String result = "";
        Resources rscs = getResources();
        final CharSequence[] items = {
                rscs.getString(R.string.Sunday_short),
                rscs.getString(R.string.Monday_Short),
                rscs.getString(R.string.Tuesday_Short),
                rscs.getString(R.string.Wednesday_Short),
                rscs.getString(R.string.Thursday_Short),
                rscs.getString(R.string.Friday_Short),
                rscs.getString(R.string.Saturday_Short)};
        Boolean anyDayAbsent = false;
        for (int i=0; i< selectedDays.length; i++)
        {
            if (selectedDays[i])
                result += (!Objects.equals(result, "") ? " " : "") + items[i];
            else
                anyDayAbsent = true;
        }
        if (!anyDayAbsent)
            result = rscs.getString(R.string.all_days);
        lblSelectedDays.setText(result);
    }

    private void showButtons(boolean show)
    {
        int state = show ? View.VISIBLE : View.INVISIBLE;
        btDelete.setVisibility(state);
        btConfirm.setVisibility(state);
    }

    private void setDialog() {
        final Resources rscs = getResources();
        final CharSequence[] items = {
                rscs.getString(R.string.Sunday),
                rscs.getString(R.string.Monday),
                rscs.getString(R.string.Tuesday),
                rscs.getString(R.string.Wednesday),
                rscs.getString(R.string.Thursday),
                rscs.getString(R.string.Friday),
                rscs.getString(R.string.Saturday)};

        dialog = new AlertDialog.Builder(this)
                .setTitle(rscs.getString(R.string.alert_dialog_select_days))
                .setMultiChoiceItems(items, newSelectedDays, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (!isChecked) {
                            Boolean anyOtherDaySelected = false;
                            for (int i=0; i< 7; i++)
                                if (i != indexSelected && newSelectedDays[i]) {
                                    anyOtherDaySelected = true;
                                    break;
                                }
                            if (!anyOtherDaySelected) {
                                newSelectedDays[indexSelected] = true;
                                ((AlertDialog) dialog).getListView().setItemChecked(indexSelected, true);
                                Toast.makeText(getApplicationContext(), rscs.getString(R.string.error_must_select_one_day),Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                }).setPositiveButton(rscs.getString(R.string.alert_dialog_set), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        System.arraycopy(newSelectedDays, 0, selectedDays, 0, 7);
                        updateSelectedDays();
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here
                    }
                }).setNegativeButton(rscs.getString(R.string.alert_dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        updateSelectedDays();
                        //  Your code when user clicked on Cancel
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        showButtons(true);
                    }
                }).create();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CHOOSE_ALERT_SOUND) {
            if (resultCode == RESULT_OK && intent != null) {
                Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                if (uri != null) {
                    this.alertSoundUriString = uri.toString();
                }
            }
        }
        else {
            if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK && intent != null) {
                if (intent.getData() != null) {
                    videoUriString = intent.getData().toString();
                    btPlay.setVisibility(View.VISIBLE);
                    Toast.makeText(ExerciseDa.this.getApplicationContext(), resources.getString(R.string.AttachSuccess), Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Toast.makeText(ExerciseDa.this.getApplicationContext(), resources.getString(R.string.AttachFailed), Toast.LENGTH_LONG).show();
        }
    }
    *//*
        return string format of the current time.
        DO NOT CHANGE THIS FORMAT [database and other checks relying on that!!]
     *//*

    private String Right(String s)
    {
        return s.substring(s.length() - 2);
    }

    private String makeTimeString()
    {
        Calendar cal = Calendar.getInstance();
        int minute =  cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        String str_hour = Right ("0" + hour);
        String str_minute = Right("0" + minute);
        return  str_hour + " : " + str_minute;
    }

    private void askUserBeforeSave()
    {
        askBeforeSave.show();
    }


    private void confirm()
    {
        confirm(false);
    }

    private void confirm(boolean askBeforeSave) {
        if(etExerciseName.getText().toString().length() == 0 && !askBeforeSave)
            Toast.makeText(getApplicationContext(), resources.getString(R.string.name_too_short), Toast.LENGTH_SHORT).show();
        else {
            if (etRepeats.getText().toString().length() == 0 && !askBeforeSave)
                Toast.makeText(getApplicationContext(), R.string.exercise_repeats_mandatory, Toast.LENGTH_LONG).show();
            else {
                if (etExerciseName.getText().toString().length() <= Max_Size) {
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

                    String exerciseName = etExerciseName.getText().toString();
                    String repetitionTypeToWrite = Utils.findResourceIdInResourcesArray(resources, R.array.repetition_types, spRepetitionType.getSelectedItem().toString());

                    if (askBeforeSave)
                    {
                        boolean dataNotChanged =
                                oldExerciseName.equals(exerciseName) &&
                                        oldRepeats == repeats &&
                                        oldTimes.equals(times) &&
                                        oldRepetitionType.equals(repetitionTypeToWrite) &&
                                        Arrays.equals(oldDays, days_to_alert) &&
                                        (oldVideoUriString == null ? videoUriString == null : oldVideoUriString.equals(videoUriString)) &&
                                        (oldAlertSoundUriString == null ? alertSoundUriString == null : oldAlertSoundUriString.equals(alertSoundUriString));
                        if (dataNotChanged)
                            finish();
                        else
                            askUserBeforeSave();
                        return;
                    }

                    Bundle params = new Bundle();
                    if (exerciseId != NewExercise) {
                        db.updateRow(exerciseId, exerciseName, times, repeats, repetitionTypeToWrite, videoUriString, days_to_alert, alertSoundUriString);
                        mFirebaseAnalytics.logEvent("Excercise_updated", params);
                    }
                    else {
                        db.addAlert(exerciseName, MedicoDB.KIND.Exercise, times, repeats, repetitionTypeToWrite, videoUriString, days_to_alert, alertSoundUriString);
                        mFirebaseAnalytics.logEvent("Excercise_added", params);
                    }
                    finish();
                } else
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.name_too_long), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        confirm(true);
    }
*/
}
