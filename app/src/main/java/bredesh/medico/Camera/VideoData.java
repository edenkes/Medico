package bredesh.medico.Camera;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import bredesh.medico.R;

public class VideoData extends Activity{

    private Button btChangeFrequency, btConfirm, addAlert;
    private EditText etExerciseName;
    private NumberPicker numberPicker;
    private ListView timeList;
    private TimeAdapter adapter;
    private ArrayList<String> arrayList;
    private TextView lblSelectedDays;

    private AlertDialog dialog;
    // array to keep the selected days
    private final boolean[] selectedDays = new boolean[7];
    private final boolean[] newSelectedDays = new boolean[7];
    private LocalDBManager db;
    private final int maxSize = 20;
    private Intent intent;
    private Resources rscs;
    private int exerciseId;
    private final int NewExercise = -6;

    private void setExistingExercise(Intent intent)
    {

        etExerciseName.setText(intent.getStringExtra("exercise_name"));

        String times = intent.getStringExtra("time");
        String[] timeAL = times.split(rscs.getString(R.string.times_splitter));
        arrayList = new ArrayList<>();
        Collections.addAll(arrayList, timeAL);
        int repeats = intent.getIntExtra("repeats",1);
        numberPicker.setValue(repeats);
        int[] days = intent.getIntArrayExtra("days");
        for (int i=0; i< 7; i++)
            selectedDays[i] = days[i] != 0;
        updateSelectedDays();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.exercises_data);
        rscs = getResources();
        btChangeFrequency = (Button) findViewById(R.id.btChangeFrequency);
        btConfirm = (Button) findViewById(R.id.btConfirm);
        addAlert = (Button) findViewById(R.id.btAddAlert);
        etExerciseName = (EditText) findViewById(R.id.etExerciseName);
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        lblSelectedDays = (TextView) findViewById(R.id.lblSelectedDays);

        timeList = (ListView) findViewById(R.id.listChangeTime);
        db = new LocalDBManager(getApplicationContext());
        this.intent = getIntent();
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(50);
        numberPicker.setWrapSelectorWheel(false);

        this.exerciseId = intent.getIntExtra("exerciseId", NewExercise);
        if (this.exerciseId != NewExercise)
        {
            setExistingExercise(intent);
        }
        else {
            arrayList = new ArrayList<>();
            arrayList.add(makeTimeString());
            for(int i=0; i<selectedDays.length; i++)
                selectedDays[i] = true;
        }

        adapter = new TimeAdapter(VideoData.this, R.layout.time_item, arrayList);
        timeList.setAdapter(adapter);
        setDialog();

        btChangeFrequency.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (int i=0; i< 7; i++)
                    newSelectedDays[i] = selectedDays[i];
                dialog.show();
                // alignDialogRTL(dialog, getApplicationContext());
            }
        });

        btConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(etExerciseName.getText().toString().length() < maxSize) {
                    int[] days_to_alert = new int[selectedDays.length];
                    for (int i = 0; i < days_to_alert.length; i++) {
                        if (selectedDays[i])
                            days_to_alert[i] = 1;
                        else
                            days_to_alert[i] = 0;
                    }
                    String videoUri = getIntent().getStringExtra("RecordedUri");
                    int repeats = numberPicker.getValue();
                    String times = "";
                    for(int i=0; i<arrayList.size(); i++)
                        times = times + (i > 0? getResources().getString(R.string.times_splitter) : "") + arrayList.get(i);
                    if (exerciseId != NewExercise)
                        db.updateRow(exerciseId, etExerciseName.getText().toString(), times, repeats, days_to_alert);
                    else
                        db.addAlert(etExerciseName.getText().toString(), times, repeats ,videoUri, days_to_alert);
                    finish();
                }
                else Toast.makeText(getApplicationContext(), "The name of the exercise is too long, please shorten it", Toast.LENGTH_SHORT).show();

            }
        });


        // updating the list + adding another alert
        addAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arrayList.add(makeTimeString());
                ((BaseAdapter) timeList.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    public static void alignDialogRTL(Dialog dialog, Context context) {
        // Get message text view
        TextView message = (TextView)dialog.findViewById(android.R.id.message);

        // Defy gravity
        if (message != null) message.setGravity(Gravity.RIGHT);

        // Get title text view
        TextView title = (TextView)dialog.findViewById(context.getResources().getIdentifier("alertTitle", "id", "android"));

        // Defy gravity (again)
        if (title != null) {
            title.setGravity(Gravity.RIGHT);
            // Get title's parent layout
            LinearLayout parent = ((LinearLayout) title.getParent());
            if (parent != null) {

                // Get layout params
                LinearLayout.LayoutParams originalParams = (LinearLayout.LayoutParams) parent.getLayoutParams();

                // Set width to WRAP_CONTENT
                originalParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;

                // Defy gravity (last time)
                originalParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;

                // Set updated layout params
                parent.setLayoutParams(originalParams);
            }
        }
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
                }).create();

    }


    /*
        return string format of the current time.
        DO NOT CHANGE THIS FORMAT [database and other checks relying on that!!]
     */
    private String makeTimeString()
    {
        Calendar cal = Calendar.getInstance();
        int minute =  cal.get(Calendar.MINUTE);
        String str_minute;
        if(minute < 10)
            str_minute = "0" + minute;
        else
            str_minute = "" + minute;
        return cal.get(Calendar.HOUR_OF_DAY) + " : " + str_minute;
    }

}