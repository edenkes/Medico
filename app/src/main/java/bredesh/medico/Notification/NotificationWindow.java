package bredesh.medico.Notification;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;

import bredesh.medico.Localization;
import bredesh.medico.MainActivity;
import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.Menu.MainMenu;
import bredesh.medico.R;
import bredesh.medico.Utils.Utils;

import static android.view.View.GONE;

public class NotificationWindow extends AppCompatActivity {

    private Intent toMain;
    private MedicoDB db;
    private PartialVideoItem item= null;
    private final int SNOOZE_TIME = 5;
    String type, special, notes;
    int amount;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        db = new MedicoDB(getApplicationContext());
        Localization.init(this, db);
        setContentView(R.layout.activity_receive_notifiction);

        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setLogo(R.mipmap.ic_medigo_logo_clock);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        TextView alertPrefixText = findViewById(R.id.txAlertPrefixText);
        TextView alertName = findViewById(R.id.txAlertName);
        TextView alertRepeats = findViewById(R.id.txRepeats);

        Button accept = findViewById(R.id.button_accept);
        Button decline = findViewById(R.id.button_decline);
        Button snooze = findViewById(R.id.snooze);
        Button snooze30 = findViewById(R.id.snooze30);
        ImageButton ib_video = findViewById(R.id.ib_video);
        ImageButton ib_image = findViewById(R.id.ib_image);

        toMain = new Intent(NotificationWindow.this, MainMenu.class);
        int id = getIntent().getIntExtra("db_id",-1);
        item = db.getItemByID(id);
        final Resources resources = getResources();
        if(item != null) {

            switch (item.kind)
            {
                case Exercise:
                    alertPrefixText.setText(resources.getString(R.string.notification_alert_prefix));
                    alertName.setText(item.name);
                    alertRepeats.setText(resources.getString(R.string.notification_alert_repeats, item.repeats, Utils.stringOrFromResource(resources, item.repetition_type, R.string.repetition_type_repetitions)));
                    break;
                case Medicine:
                    Cursor c = (new MedicoDB(getApplicationContext())).getMedicineByID(item.id);
                    amount = c.getInt(c.getColumnIndex(MedicoDB.KEY_AMOUNT));
                    type = Utils.stringOrFromResource(resources, c.getString(c.getColumnIndex(MedicoDB.KEY_TYPE)));
                    special = Utils.stringOrFromResource(resources, c.getString(c.getColumnIndex(MedicoDB.KEY_SPECIAL)));
                    notes = c.getString(c.getColumnIndex(MedicoDB.KEY_NOTES));
                    alertPrefixText.setText(resources.getString(R.string.notification_alert_prefix_medicine));
                    alertName.setText(item.name);
                    if (type.compareTo(resources.getString(R.string.medicine_dosage_other)) == 0)
                        alertRepeats.setText("");
                    else
                        alertRepeats.setText(""+amount +" "+type);

                    TextView tvSpecial = findViewById(R.id.tv_special);
                    if(special.equals(resources.getString(R.string.medicine_usage_notes_none)))
                        tvSpecial.setVisibility(GONE);
                    else
                        tvSpecial.setText(special);

                    if(!notes.equals(""))
                    {
                        TextView tvNotes = findViewById(R.id.tv_notes);
                        tvNotes.setText("* " +notes);
                        tvNotes.setMovementMethod(new ScrollingMovementMethod());
                    }
                    if(item.uriImage != null && item.kind == MedicoDB.KIND.Medicine) {
                        ViewGroup.LayoutParams layoutParams = ib_image.getLayoutParams();
                        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        layoutParams.height= 200;
                        ib_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        ib_image.setLayoutParams(layoutParams);
                        Glide.with(this).load(item.uriImage).into(ib_image);
                        ib_image.invalidate();
                    }
                    break;
                case Reminders:
                    alertPrefixText.setText(resources.getString(R.string.notification_alert_prefix));
                    alertName.setText(item.name);
                    alertRepeats.setText("");
                    Cursor cReminders = (new MedicoDB(getApplicationContext())).getRemindersByID(item.id);
                    notes = cReminders.getString(cReminders.getColumnIndex(MedicoDB.KEY_NOTES));

                    if(!notes.equals("")){
                        TextView tvNotes = findViewById(R.id.tv_notes);
                        tvNotes.setText("* " +notes);
                        tvNotes.setMovementMethod(new ScrollingMovementMethod());
                    }
                    if(item.uriImage != null && item.kind == MedicoDB.KIND.Medicine) {
                        ViewGroup.LayoutParams layoutParams = ib_image.getLayoutParams();
                        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        layoutParams.height= 200;
                        ib_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        ib_image.setLayoutParams(layoutParams);
                        Glide.with(this).load(item.uriImage).into(ib_image);
                        ib_image.invalidate();
                    }

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
                Bundle params = new Bundle();
                params.putInt("points", 10);
                mFirebaseAnalytics.logEvent("Notification_done", params);
                Toast.makeText(getApplicationContext(), goodJob , Toast.LENGTH_LONG).show();
                moveToMain();
            }
        });

        final String alertCancelled = resources.getString(R.string.alert_cancelled);
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), alertCancelled , Toast.LENGTH_LONG).show();
                Bundle params = new Bundle();
                mFirebaseAnalytics.logEvent("Notification_cancel", params);
                moveToMain();
            }
        });


        View.OnClickListener snoozeListener =  new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String snoozeTimeStr = v.getTag().toString();
                final String alertSnoozed = resources.getString(R.string.alert_snoozed, snoozeTimeStr);

                Toast.makeText(getApplicationContext(), alertSnoozed, Toast.LENGTH_LONG).show();
/*
                Long alertTime = new Long(System.currentTimeMillis() + (5 * 1000));

                Intent alertIntent = new Intent(NotificationWindow.this, AlertReceiver.class);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime,
                        PendingIntent.getBroadcast(NotificationWindow.this, 1, alertIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT));*/
                if (item != null) {
                    String timeSTR;
                    Calendar cal = Calendar.getInstance();
                    int snoozeMinutes = Integer.parseInt(snoozeTimeStr);
                    int minute = (cal.get(Calendar.MINUTE) + snoozeMinutes);
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    if (minute >= 60) {
                        minute = minute % 60;
                        hour++;
                    }
                    if (hour >= 24) hour = hour % 24;
                    String str_minute;
                    if (minute < 10)
                        str_minute = "0" + minute;
                    else
                        str_minute = "" + minute;
                    String strHour = "0" + hour;
                    strHour = strHour.substring(strHour.length() - 2);
                    timeSTR = strHour + " : " + str_minute;
                    //TODO
                    db.addAlert("_TEMP__" + item.name, item.kind, timeSTR, item.repeats, item.repetition_type, (item.uriVideo != null) ? item.uriVideo.toString() : null,
                            (item.uriImage != null) ? item.uriImage.toString() : null, new int[]{1, 1, 1, 1, 1, 1, 1}, item.alertSoundUriString);
                    Bundle params = new Bundle();
                    params.putInt("minutes", snoozeMinutes);
                    mFirebaseAnalytics.logEvent("Notification_snooze", params);
                    if (item.kind == MedicoDB.KIND.Medicine)
                        db.addMedicine(type, special, notes, amount);
                }
                moveToMain();
            }
        };

        snooze.setOnClickListener(snoozeListener);
        snooze30.setOnClickListener(snoozeListener);

        if (item == null || item.uriImage == null)
            ib_image.setVisibility(View.INVISIBLE);

        if (item == null || item.uriVideo == null)
            ib_video.setVisibility(View.INVISIBLE);

        ib_video.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(item!=null)
                {
                    Uri videoUri = item.uriVideo;
                    Intent intent = null;
                    String errMsg = "";
                    try {
                        switch (item.kind)
                        {
                            case Exercise:
                                intent = new Intent(Intent.ACTION_VIEW, videoUri);
                                errMsg =  getResources().getString(R.string.media_not_found);
                                break;
                            case Medicine:
                                intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(videoUri,"image/*");
                                errMsg =  getResources().getString(R.string.media_not_found_image);
                                break;
                            case Reminders:
                                intent = new Intent(Intent.ACTION_VIEW, videoUri);
                                errMsg =  getResources().getString(R.string.media_not_found);
                                break;
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                    catch (RuntimeException e){ Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_SHORT).show(); }
                }
            }
        });
    }


    private void moveToMain() {
        if (MainMenu.active)
            finish();
        else {
            startActivity(toMain);
            finish();
        }
    }
}