package bredesh.medico.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import bredesh.medico.Camera.LocalDBManager;
import bredesh.medico.MainActivity;
import bredesh.medico.R;

public class NotificationWindow extends AppCompatActivity {

    private Intent toMain;
    private LocalDBManager db;
    private PartialVideoItem item= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiction_window);

        TextView title = (TextView) findViewById(R.id.textView);
        Button accept = (Button) findViewById(R.id.button_accept);
        Button decline = (Button) findViewById(R.id.button_decline);
        Button snooze = (Button) findViewById(R.id.snooze);
        ImageButton play = (ImageButton) findViewById(R.id.imageButton);
        toMain = new Intent(NotificationWindow.this, MainActivity.class);
        int id = getIntent().getIntExtra("db_id",-1);
        db = new LocalDBManager(getApplicationContext());
        item = db.getItemByID(id);
        if(item != null) {
            String finalText = getResources().getString(R.string.part1_timeToDO) + " " + item.name + "\n" +getResources().getString(R.string.part2_repeats) + item.repeats;
            title.setText(finalText);
            db.deleteRow(id);
        }
        else title.setText("FATAL ERROR!");

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item!=null)
                {
                    Uri videoUri = item.uri;
                    if(videoUri != null ) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(intent);
                        }catch (RuntimeException e){
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't open the video/photo", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else    Toast.makeText(getApplicationContext(),
                            "Couldn't find the video/photo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Good job", Toast.LENGTH_LONG).show();
                moveToMain();


            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Alert been cancel, Try again later", Toast.LENGTH_LONG).show();
                moveToMain();
            }
        });

        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Alert been delay in five mints", Toast.LENGTH_LONG).show();
/*
                Long alertTime = new Long(System.currentTimeMillis() + (5 * 1000));

                Intent alertIntent = new Intent(NotificationWindow.this, AlertReceiver.class);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime,
                        PendingIntent.getBroadcast(NotificationWindow.this, 1, alertIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT));*/
                if(item!=null) {
                    String timeSTR="";
                    Calendar cal = Calendar.getInstance();
                    int minute =  (cal.get(Calendar.MINUTE) +5);
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    if(minute >= 60)
                    {
                        minute =  minute%60;
                        hour++;
                    }
                    if(hour>=24) hour = hour % 24;
                    String str_minute;
                    if(minute < 10)
                        str_minute = "0" + minute;
                    else
                        str_minute = "" + minute;
                    timeSTR = hour + " : " + str_minute;
                    db.addAlert("_TEMP__"+item.name, timeSTR, item.repeats, item.uri.toString(), new int[]{1, 1, 1, 1, 1, 1, 1});
                }
                moveToMain();
            }
        });
    }


    private void moveToMain() {
        if (MainActivity.active)
            finish();
        else {
            startActivity(toMain);
            finish();
        }

    }
}