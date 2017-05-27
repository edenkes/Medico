package bredesh.medico.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import bredesh.medico.Game.ScoreDatabase;
import bredesh.medico.MainActivity;
import bredesh.medico.R;

public class NotificationWindow extends AppCompatActivity {

    private Intent toMain;
    private LocalDBManager db;
    private ScoreDatabase scoreDatabase;
    private PartialVideoItem item= null;
    private final int SNOOZE_TIME = 5;

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
        Resources rscs = getResources();
        if(item != null) {
            String finalText = String.format(rscs.getString(R.string.alert_page_text), item.name, item.repeats);
            title.setText(finalText);
            if(item.temp) {
                db.deleteRow(id);

            }
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
                                    getResources().getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                }
            }
        });

        final String goodJob = rscs.getString(R.string.good_job);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), goodJob , Toast.LENGTH_LONG).show();
                moveToMain();


            }
        });
        final String alertCancelled = rscs.getString(R.string.alert_cancelled);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), alertCancelled , Toast.LENGTH_LONG).show();
                moveToMain();
            }
        });

        final String alertSnoozed = rscs.getString(R.string.alert_snoozed);
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), alertSnoozed , Toast.LENGTH_LONG).show();
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
                    int minute =  (cal.get(Calendar.MINUTE) + SNOOZE_TIME);
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
                    String uri =  item.uri != null ? item.uri.toString() : null;
                    db.addAlert("_TEMP__"+item.name, timeSTR, item.repeats, uri, new int[]{1, 1, 1, 1, 1, 1, 1});
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