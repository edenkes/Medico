package bredesh.medico.Camera;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import bredesh.medico.R;

public class VideoData extends Activity{

    private Button btChangeFrequency, btConfirm, addAlert;
    private EditText etExerciseName;
    private NumberPicker numberPicker;
    private ListView timeList;
    private TimeAdapter adapter;
    private ArrayList<String> arrayList;


    private AlertDialog dialog;
    // array to keep the selected days
    private final boolean[] selectedDays = new boolean[7];
    private LocalDBManager db;
    private final int maxSize = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.exercises_data);
        btChangeFrequency = (Button) findViewById(R.id.btChangeFrequency);
        btConfirm = (Button) findViewById(R.id.btConfirm);
        addAlert = (Button) findViewById(R.id.btAddAlert);
        etExerciseName = (EditText) findViewById(R.id.etExerciseName);
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        timeList = (ListView) findViewById(R.id.listChangeTime);

        arrayList = new ArrayList<>();
        arrayList.add(makeTimeString());
        adapter = new TimeAdapter(VideoData.this, R.layout.time_item, arrayList);
        timeList.setAdapter(adapter);


        setDialog();

        db = new LocalDBManager(getApplicationContext());

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(50);
        numberPicker.setWrapSelectorWheel(false);

        btChangeFrequency.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
                    for(int i=0; i<arrayList.size(); i++)
                        db.addAlert(etExerciseName.getText().toString(), arrayList.get(i), repeats ,videoUri, days_to_alert);
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

    private void setDialog() {
        Resources rscs = getResources();
        final CharSequence[] items = {
                rscs.getString(R.string.Sunday),
                rscs.getString(R.string.Monday),
                rscs.getString(R.string.Tuesday),
                rscs.getString(R.string.Wednesday),
                rscs.getString(R.string.Thursday),
                rscs.getString(R.string.Friday),
                rscs.getString(R.string.Saturday)};
        for(int i=0; i<selectedDays.length; i++)
            selectedDays[i] = true;

        dialog = new AlertDialog.Builder(this)
                .setTitle(rscs.getString(R.string.alert_dialog_select_days))
                .setMultiChoiceItems(items, selectedDays, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selectedDays[indexSelected] = true;
                        } else if (selectedDays[indexSelected]) {
                            // Else, if the item is already in the array, remove it
                            selectedDays[indexSelected] = false;
                        }
                    }
                }).setPositiveButton(rscs.getString(R.string.alert_dialog_set), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here
                    }
                }).setNegativeButton(rscs.getString(R.string.alert_dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
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