package bredesh.medico.Camera;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

    private Button btChangeFrequency, btConfirm;
    private EditText etExerciseName, etRepeats;
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
        final Calendar calendar = Calendar.getInstance();
        btChangeFrequency = (Button) findViewById(R.id.btChangeFrequency);
        btConfirm = (Button) findViewById(R.id.btConfirm);
        etExerciseName = (EditText) findViewById(R.id.etExerciseName);
        etRepeats = (EditText) findViewById(R.id.etRepeats);
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        timeList = (ListView) findViewById(R.id.listChangeTime);

        int minute =  calendar.get(Calendar.MINUTE);
        String str_minute;
        if(minute < 10)
            str_minute = "0" + minute;
        else
            str_minute = "" + minute;

        final String startString = calendar.get(Calendar.HOUR_OF_DAY) + " : " + str_minute;

        arrayList = new ArrayList<>();
        arrayList.add(startString);
        adapter = new TimeAdapter(getApplicationContext(), R.layout.time_item, arrayList);

        timeList.setAdapter(adapter);


        setDialog();

        db = new LocalDBManager(getApplicationContext());

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(15);
        numberPicker.setWrapSelectorWheel(false);

        btChangeFrequency.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.show();
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
                    int repeats = Integer.parseInt((etRepeats.getText().toString().equals("")) ? "1" : etRepeats.getText().toString());
//                    int repeats = numberPicker.getValue();
                    for(int i=0; i<arrayList.size(); i++)
                        db.addAlert(etExerciseName.getText().toString(), arrayList.get(i), repeats ,videoUri, days_to_alert);
                    finish();
                }
                else Toast.makeText(getApplicationContext(), "The name of the exercise is too long, please shorten it", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setDialog() {
        final CharSequence[] items = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        for(int i=0; i<selectedDays.length; i++)
            selectedDays[i] = true;

        dialog = new AlertDialog.Builder(this)
                .setTitle("Select Days to Alert")
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
                }).setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on OK
                        //  You can write the code  to save the selected item here
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();
    }

}