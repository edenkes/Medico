package bredesh.medico.Camera;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import bredesh.medico.MedicoDB;
import bredesh.medico.R;

/**
 * Created by Omri on 12/06/2017.
 */



public class MedicineData extends AppCompatActivity implements IRemoveLastAlert {



    private EditText etMedicineName;
    private ArrayList<String> arrayList;
    private TextView lblSelectedDays;
    private Button btDelete, btConfirm;


    private Button btType, btSpecial, btNotes;
    private TextView tvType, tvSpecial, tvNotes;
    private AlertDialog dialogType, dialogSpecial, dialogNotes;

    private AlertDialog dialogDays;
    // array to keep the selected days
    private final boolean[] selectedDays = new boolean[7];
    private final boolean[] newSelectedDays = new boolean[7];
    private MedicoDB db;
    private final int maxSize = 16;
    private Resources resources;
    private int exerciseId;
    private final int NewExercise = -6;
    private ImageButton btPlay;
    private String videoUriString;
    private final Button[] alertPlanButtons = new Button[5];
    private TimeAdapterRecycler timeAdapter = null;
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
                    for (int j=0; j< i+1; j++)
                        arrayList.add(AlertPlans[i][j]);
                    timeAdapter.notifyItemInserted(arrayList.size() - 1);

                }
            }
            setAddAlertsButtons(false);
        }
    };


    private void setExistingExercise(Intent intent)
    {

        etMedicineName.setText(intent.getStringExtra("exercise_name"));

        String times = intent.getStringExtra("time");
        String[] timeAL = null;
        arrayList = new ArrayList<>();
        if (times != null && !times.contentEquals("")) {
            timeAL = times.split(resources.getString(R.string.times_splitter));
            Collections.addAll(arrayList, timeAL);
        }
        int[] days = intent.getIntArrayExtra("days");
        for (int i=0; i< 7; i++)
            selectedDays[i] = days[i] != 0;
        updateSelectedDays();
        this.setAddAlertsButtons(timeAL == null || timeAL.length == 0);
    }

    private View.OnClickListener clickHandler = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            for (int i=0; i< 7; i++)
                newSelectedDays[i] = selectedDays[i];
            dialogDays.show();
            showButtons(false);

            // alignDialogRTL(dialogDays, getApplicationContext());
        }
    };

    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private void ShootVideo()
    {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(MedicineData.this.getPackageManager()) != null) {
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void initTypeDialog()
    {
        final Resources resources = getResources();
        final CharSequence[] items = {
                resources.getString(R.string.menu1_item1),
                resources.getString(R.string.menu1_item2),
                resources.getString(R.string.menu1_item3),
                resources.getString(R.string.menu1_item4),
                resources.getString(R.string.menu1_item5),
                resources.getString(R.string.menu1_item6)};

        dialogType = new AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.menu1_title))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvType.setText(items[which]);
                    }
                })
                .create();
    }

    private void initSpecialDialog()
    {
        final Resources resources = getResources();
        final CharSequence[] items = {
                resources.getString(R.string.menu2_item1),
                resources.getString(R.string.menu2_item2),
                resources.getString(R.string.menu2_item3),
                resources.getString(R.string.menu2_item4),
                resources.getString(R.string.menu2_item5)};

        dialogSpecial = new AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.menu2_title))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvSpecial.setText(items[which]);
                    }
                })
                .create();
    }

    private void initNotesDialog()
    {
        final Resources resources = getResources();

        final EditText input = new EditText(MedicineData.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        dialogNotes = new AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.medicine_notes))
                .setView(input)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        tvNotes.setText(input.getText().toString());
                    }
                }).setNegativeButton(resources.getString(R.string.alert_dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {}
                })
                .create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_medicine_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setLogo(R.mipmap.ic_medico_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        RecyclerView timeViews = (RecyclerView) findViewById(R.id.time_views);
        timeViews.setLayoutManager(new LinearLayoutManager(this));

        resources = getResources();
        Button btChangeFrequency = (Button) findViewById(R.id.btChangeFrequency);
        btConfirm = (Button) findViewById(R.id.btConfirm);
        btDelete = (Button) findViewById(R.id.btDelete);
        etMedicineName = (EditText) findViewById(R.id.etExerciseName);
        lblSelectedDays = (TextView) findViewById(R.id.lblSelectedDays);
        lblSelectedDays.setMovementMethod(new ScrollingMovementMethod());

        btPlay = (ImageButton) findViewById(R.id.btPlay);
        alertPlanButtons[0] = (Button) findViewById(R.id.bt1time);
        alertPlanButtons[1] = (Button) findViewById(R.id.bt2times);
        alertPlanButtons[2] = (Button) findViewById(R.id.bt3times);
        alertPlanButtons[3] = (Button) findViewById(R.id.bt4times);
        alertPlanButtons[4] = (Button) findViewById(R.id.bt5times);
        btAddAlert = (Button) findViewById(R.id.btAddAlert);
        lbAddMultiAlert = (TextView) findViewById(R.id.lbAddMultiAlert);


        btType = (Button) findViewById(R.id.btType);
        tvType = (TextView) findViewById(R.id.tv_type);
        initTypeDialog();
        btType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogType.show();
            }
        });

        btSpecial = (Button) findViewById(R.id.btSpecial);
        tvSpecial = (TextView) findViewById(R.id.tv_special);
        tvSpecial.setMovementMethod(new ScrollingMovementMethod());
        initSpecialDialog();
        btSpecial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSpecial.show();
            }
        });

        btNotes = (Button) findViewById(R.id.btNotes);
        tvNotes = (TextView) findViewById(R.id.tv_notes);
        tvNotes.setMovementMethod(new ScrollingMovementMethod());
        initNotesDialog();
        btNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNotes.show();
            }
        });


        final AlertDialog reShootConfirm = new AlertDialog.Builder(this)
                .setPositiveButton(resources.getString(R.string.alert_dialog_set), onReshootConfirm)
                .setNegativeButton(resources.getString(R.string.alert_dialog_cancel), null)
                .setMessage(resources.getString(R.string.reshootPic)).create();


        ImageButton video = (ImageButton) findViewById(R.id.btShootVideo);
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
        videoUriString = intent.getStringExtra("RecordedUri");

        this.exerciseId = intent.getIntExtra("exerciseId", NewExercise);
        if (this.exerciseId != NewExercise)
        {
            setExistingExercise(intent);
        }
        else {
            arrayList = new ArrayList<>();
            // arrayList.add(makeTimeString());
            for(int i=0; i<selectedDays.length; i++)
                selectedDays[i] = true;
        }

        Button [] buttons = new Button[] {btConfirm, btDelete};


        timeAdapter = new TimeAdapterRecycler(MedicineData.this,arrayList, buttons, this);
        timeViews.setAdapter(timeAdapter);
        setDialog();

        btChangeFrequency.setOnClickListener(clickHandler);
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

        btConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(etMedicineName.getText().toString().length() == 0)
                    Toast.makeText(getApplicationContext(), resources.getString(R.string.name_too_short), Toast.LENGTH_SHORT).show();
                else {
                    if (etMedicineName.getText().toString().length() <= maxSize) {
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
                        if (exerciseId != NewExercise)
                            db.updateRow(exerciseId, etMedicineName.getText().toString(), times, 0, videoUriString, days_to_alert);
                        else
                            db.addAlert(etMedicineName.getText().toString(), MedicoDB.KIND.Exercise, times, 0, videoUriString, days_to_alert);
                        finish();
                    } else
                        Toast.makeText(getApplicationContext(), resources.getString(R.string.name_too_long), Toast.LENGTH_SHORT).show();
                }
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

                arrayList.add(makeTimeString());
                timeAdapter.notifyItemInserted(arrayList.size() - 1);
            }
        });
    }

    private void updateSelectedDays()
    {
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
                result += (result != "" ? " " : "") + items[i];
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

        dialogDays = new AlertDialog.Builder(this)
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
                        for (int i=0; i< 7; i++)
                            selectedDays[i] = newSelectedDays[i];
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
        if(resultCode == RESULT_OK && intent != null ){
            if(intent.getData() != null){
                videoUriString = intent.getData().toString();
                btPlay.setVisibility(View.VISIBLE);
                Toast.makeText(MedicineData.this.getApplicationContext(), resources.getString(R.string.AttachSuccess), Toast.LENGTH_LONG).show();
                return;
            }
        }
        Toast.makeText(MedicineData.this.getApplicationContext(), resources.getString(R.string.AttachFailed), Toast.LENGTH_LONG).show();
    }
    /*
        return string format of the current time.
        DO NOT CHANGE THIS FORMAT [database and other checks relying on that!!]
     */

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

}