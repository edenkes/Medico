package bredesh.medico;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;

import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.DAL.ValueConstants;
import bredesh.medico.Notification.NotificationWindow;
import bredesh.medico.Utils.Utils;

public class NotificationService extends Service {

    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private MedicoDB local;
    private Cursor cursor;
    private int CURRENT_DAY;
    private boolean shouldStop = false;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String CHANNEL_ID = "my_channel_01";

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "Medigo Notifications";
            String Description = "Medigo Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);

        Calendar calendar = Calendar.getInstance();
        local = new MedicoDB(getApplicationContext());

        CURRENT_DAY = calendar.get(Calendar.DAY_OF_WEEK);
        cursor = getAllTodayAlerts();

        startThreading();
        return Service.START_STICKY;
    }

    private void showNotification(String notificationName, int times, int notiID, String soundUri, int numberOfSets) {
        int notification_id = (int) System.currentTimeMillis();

        if(notificationName.length() >=7 && notificationName.substring(0,7).equals("_TEMP__"))
            notificationName = notificationName.substring(7);

        Intent button_intent = new Intent(getApplicationContext(),NotificationWindow.class);
        button_intent.putExtra("id", notification_id);
        button_intent.putExtra("db_id",notiID);
        button_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Intent getBack = new Intent(getApplicationContext(),NotificationWindow.class);
        getBack.putExtra("db_id",notiID);
        getBack.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), notiID,getBack, PendingIntent.FLAG_UPDATE_CURRENT);

        String notiString = "";
        switch (local.getKindByID(notiID)){
            case Exercise:
                String repetitionType = getResources().getString(ValueConstants.ExerciseRepetitionType.getStringCodeFromDBCode(cursor.getInt(cursor.getColumnIndex(MedicoDB.KEY_REPETITION_TYPE))));
                String timesStr = Integer.toString(times);
                notiString = (getResources().getString(R.string.alert_text, notificationName ,timesStr, repetitionType));
                if (numberOfSets != 1) {
                    notiString += " " + Integer.toString(numberOfSets) + " " + getResources().getString(R.string.exercise_sets_label);
                }
                break;
            case Medicine:
                Cursor cMedicine = local.getMedicineByID(notiID);
                int dosageType = cMedicine.getInt(cMedicine.getColumnIndex(MedicoDB.KEY_TYPE));

                if (dosageType != ValueConstants.MedicineDosageOther) {
                    String dosageTypeMedicine = getResources().getString(ValueConstants.DrugDosage.getStringCodeFromDBCode(dosageType));
                    notiString = (getResources().getString(R.string.alert_text_medicine,
                            times,
                            dosageTypeMedicine));
                }
                else {
                    notiString = (getResources().getString(R.string.notification_alert_prefix_medicine));
                    if (notiString.endsWith(":"))
                        notiString = notiString.substring(0, notiString.length()-1);
                }
                break;
            case Reminders:
                Cursor cReminders = local.getRemindersByID(notiID);
                String notesReminders = Utils.stringOrFromResource(getResources(), cReminders.getString(cReminders.getColumnIndex(MedicoDB.KEY_NOTES)));
                notiString = (getResources().getString(R.string.alert_text_reminders,
                        notificationName,
                        notesReminders));
                break;
        }


        builder.setSmallIcon(R.mipmap.ic_medigo_logo_clock)
                .setSmallIcon(R.mipmap.ic_alarm_black_48dp)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(notificationName)
                .setWhen(System.currentTimeMillis())
                .setContentText(notiString)
                .setPriority(Notification.PRIORITY_HIGH);

        Uri soundActualUri;
        if (soundUri != null) {
            soundActualUri = Uri.parse(soundUri);
        }
        else
        {
            soundActualUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        builder.setSound(soundActualUri);

        notificationManager.notify(notification_id,builder.build());
    }

    private Cursor getAllTodayAlerts()
    {
        switch (CURRENT_DAY) {
            case Calendar.SUNDAY:    return local.getAllAlertsByDay(MedicoDB.SUNDAY);
            case Calendar.MONDAY:    return local.getAllAlertsByDay(MedicoDB.MONDAY);
            case Calendar.TUESDAY:   return local.getAllAlertsByDay(MedicoDB.TUESDAY);
            case Calendar.WEDNESDAY: return local.getAllAlertsByDay(MedicoDB.WEDNESDAY);
            case Calendar.THURSDAY:  return local.getAllAlertsByDay(MedicoDB.THURSDAY);
            case Calendar.FRIDAY:    return local.getAllAlertsByDay(MedicoDB.FRIDAY);
            case Calendar.SATURDAY:  return local.getAllAlertsByDay(MedicoDB.SATURDAY);
            default:                 return local.getAllAlerts();
        }//now cursor initiated with all the alerts today
    }

    private void startThreading()
    {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        final ContextWrapper _this = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar;
                int currentHour;
                int currentMinutes;
                String time;
                while (!shouldStop) {  try { synchronized (this) {
                    cursor = getAllTodayAlerts();

                    if(cursor.getCount() > 0) {

                        calendar = Calendar.getInstance();
                        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                        currentMinutes = calendar.get(Calendar.MINUTE);
                        if(CURRENT_DAY != calendar.get(Calendar.DAY_OF_WEEK))
                        {
                            cursor.moveToFirst();
                            do {
                                local.allTodaysAlertReset(cursor.getInt(cursor.getColumnIndex(MedicoDB.KEY_ID)));
                            } while (cursor.moveToNext());
                            CURRENT_DAY = calendar.get(Calendar.DAY_OF_WEEK);
                            cursor = getAllTodayAlerts();
                        }
                        cursor.moveToFirst();
                        do {
                            try {
                                String allTimes = cursor.getString(cursor.getColumnIndex(MedicoDB.KEY_TIME));
                                String[] times = allTimes.split(getResources().getString(R.string.times_splitter));

                                for (int i = 0; i < times.length; i++) {
                                    time = times[i];
                                    int gap = 2;
                                    //if (currentHour < 10) gap = 1;
                                    int notificationHour = Integer.parseInt(time.substring(0, gap));
                                    int notificationMinute = Integer.parseInt(time.substring(gap + 3));
                                    String todayAlert = cursor.getString(cursor.getColumnIndex(MedicoDB.ALERT_TODAY));
                                    if (notificationHour == currentHour && notificationMinute == currentMinutes && todayAlert.compareTo(time) < 0) {
                                        //now we need to show notification!!
                                        String notificationName = cursor.getString(cursor.getColumnIndex(MedicoDB.KEY_NAME));
                                        int repeats = cursor.getInt(cursor.getColumnIndex(MedicoDB.KEY_REPEATS));
                                        Localization.init(_this, local);

                                        Bundle bundle=new Bundle();
                                        mFirebaseAnalytics.logEvent("Notification_show", bundle);

                                        showNotification(notificationName, repeats, cursor.getInt(cursor.getColumnIndex(MedicoDB.KEY_ID)), cursor.getString(cursor.getColumnIndex(MedicoDB.KEY_ALERT_SOUND_URI)),
                                                cursor.getInt(cursor.getColumnIndex(MedicoDB.KEY_NUMBER_OF_SETS)));
                                        local.updateAlertToday(cursor.getInt(cursor.getColumnIndex(MedicoDB.KEY_ID)), time);
                                    }
                                }
                            }
                            catch (Exception alertEx)
                            {
                                Log.i("Alert Exception", " Alert Exception: " + alertEx.getMessage());
                            }
                        } while (cursor.moveToNext());
                    }
                    Thread.sleep(10000);//60 sec
                } /*sync*/ }/* try*/ catch (Exception e) {
                    Log.i("Exception", "Exception: " + e.getMessage());
                }
                }
            }
        }).start();
    }
}
