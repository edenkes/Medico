package bredesh.medico.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.w3c.dom.Text;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setLogo(R.mipmap.ic_medico_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        TextView title = (TextView) findViewById(R.id.name);
        TextView repeats = (TextView) findViewById(R.id.tvrepeats);
        Button accept = (Button) findViewById(R.id.button_accept);
        Button decline = (Button) findViewById(R.id.button_decline);
        Button snooze = (Button) findViewById(R.id.snooze);
        ImageButton playButton = (ImageButton) findViewById(R.id.play_button);

        toMain = new Intent(NotificationWindow.this, MainActivity.class);
        int id = getIntent().getIntExtra("db_id",-1);
        db = new LocalDBManager(getApplicationContext());
        item = db.getItemByID(id);
        Resources resources = getResources();
        if(item != null) {
            title.setText(item.name);
            repeats.setText(""+item.repeats);

            if(item.temp) {
                db.deleteRow(id);
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "FATAL ERROR" , Toast.LENGTH_LONG).show();
            moveToMain();
        }
        final String goodJob = resources.getString(R.string.good_job);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), goodJob , Toast.LENGTH_LONG).show();
                moveToMain();

            }
        });

        final String alertCancelled = resources.getString(R.string.alert_cancelled);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), alertCancelled , Toast.LENGTH_LONG).show();
                moveToMain();
            }
        });

        final String alertSnoozed = resources.getString(R.string.alert_snoozed);
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
                    String timeSTR;
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
                    db.addAlert("_TEMP__"+item.name, timeSTR, item.repeats, item.uri.toString(), new int[]{1, 1, 1, 1, 1, 1, 1});
                }
                moveToMain();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
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