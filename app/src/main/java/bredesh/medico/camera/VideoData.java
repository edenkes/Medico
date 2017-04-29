package bredesh.medico.camera;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import bredesh.medico.R;


/**
 * Created by Omri on 07-Mar-17.
 */
public class VideoData extends Activity{
    Button btChangeFrequency, btChangeTime, btConfirm;
    TextView tvTime;
    EditText etExerciseName;

    AlertDialog dialog;
    // array to keep the selected days
    final boolean[] selectedDays = new boolean[7];
    LocalDBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.exercises_data);

        btChangeFrequency = (Button) findViewById(R.id.btChangeFrequency);

        btConfirm = (Button) findViewById(R.id.btConfirm);
        etExerciseName = (EditText) findViewById(R.id.etExerciseName);

        tvTime = (TextView) findViewById(R.id.tvTime);
        btChangeTime = (Button) findViewById(R.id.btChangeTime);

        setDialog();

        db = new LocalDBManager(getApplicationContext());

        btChangeFrequency.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        btChangeTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int hour = 8;
                int minute = 00;
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(VideoData.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        tvTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, DateFormat.is24HourFormat(getApplicationContext()));//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        btConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int[] days_to_alert = new int[selectedDays.length];
                for(int i=0; i<days_to_alert.length; i++)
                {
                    if(selectedDays[i])
                        days_to_alert[i] = 1;
                    else
                        days_to_alert[i] = 0;
                }
                String videoUri = getIntent().getStringExtra("RecordedUri");
                Toast.makeText(getApplicationContext(), "name: " + etExerciseName.getText().toString()+ ", time: " + tvTime.getText().toString(),Toast.LENGTH_SHORT).show();

                db.addAlert(etExerciseName.getText().toString(), tvTime.getText().toString(), videoUri, days_to_alert);
                finish();
//                startActivity(new Intent(VideoData.this,MainLoginActivity.class));
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
