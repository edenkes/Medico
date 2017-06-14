package bredesh.medico.Notification;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import bredesh.medico.MainActivity;
import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.R;

public class NotificationWindow extends AppCompatActivity {

    private Intent toMain;
    private MedicoDB db;
    private PartialVideoItem item= null;
    private final int SNOOZE_TIME = 5;
    String type, special, notes;
    int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_notifiction);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setLogo(R.mipmap.ic_medico_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        TextView alertPrefixText = (TextView) findViewById(R.id.txAlertPrefixText);
        TextView alertName = (TextView) findViewById(R.id.txAlertName);
        TextView alertRepeats = (TextView) findViewById(R.id.txRepeats);

        Button accept = (Button) findViewById(R.id.button_accept);
        Button decline = (Button) findViewById(R.id.button_decline);
        Button snooze = (Button) findViewById(R.id.snooze);
        ImageButton playButton = (ImageButton) findViewById(R.id.play_button);

        toMain = new Intent(NotificationWindow.this, MainActivity.class);
        int id = getIntent().getIntExtra("db_id",-1);
        db = new MedicoDB(getApplicationContext());
        item = db.getItemByID(id);
        Resources resources = getResources();
        if(item != null) {

            switch (item.kind)
            {
                case Exercise:
                    alertPrefixText.setText(resources.getString(R.string.notification_alert_prefix));
                    alertName.setText(item.name);
                    alertRepeats.setText(resources.getString(R.string.notification_alert_repeats, item.repeats));
                    break;
                case Medicine:
                    Cursor c = (new MedicoDB(getApplicationContext())).getMedicineByID(item.id);
                    amount = c.getInt(c.getColumnIndex(MedicoDB.KEY_AMOUNT));
                    type = c.getString(c.getColumnIndex(MedicoDB.KEY_TYPE));
                    special = c.getString(c.getColumnIndex(MedicoDB.KEY_SPECIAL));
                    notes = c.getString(c.getColumnIndex(MedicoDB.KEY_NOTES));
                    alertPrefixText.setText(resources.getString(R.string.notification_alert_prefix_medicine));
                    alertName.setText(item.name);
                    alertRepeats.setText(""+amount +" "+type);

                    TextView tvSpecial = (TextView) findViewById(R.id.tv_special);
                    tvSpecial.setText(resources.getString(R.string.menu2_title) + ": " +special);
                    if(!notes.equals(""))
                    {
                        TextView tvNotes = (TextView) findViewById(R.id.tv_notes);
                        tvNotes.setText("* " +notes);
                        tvNotes.setMovementMethod(new ScrollingMovementMethod());
                    }
                    playButton.setVisibility(View.GONE);

                    break;

            }

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
                db.addPoints(MedicoDB.PhysioTherapy, item.id, item.name, 10);
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
                    String strHour = "0" + hour;
                    strHour = strHour.substring(strHour.length()-2);
                    timeSTR = strHour + " : " + str_minute;
                    db.addAlert("_TEMP__"+item.name, item.kind, timeSTR, item.repeats, (item.uri !=null)? item.uri.toString(): null, new int[]{1, 1, 1, 1, 1, 1, 1});
                    if(item.kind == MedicoDB.KIND.Medicine) db.addMedicine(type, special,notes,amount);
                }
                moveToMain();
            }
        });

        if (item == null || item.uri == null)
            playButton.setVisibility(View.INVISIBLE);
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