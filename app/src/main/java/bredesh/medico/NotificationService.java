package bredesh.medico;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import java.util.Calendar;
import bredesh.medico.Camera.LocalDBManager;

public class NotificationService extends Service {
    private NotificationManager mNotificationManager;
    private Notification.Builder NotificationBuilder;
    private LocalDBManager local;
    private Cursor cursor;
    private int CURRENT_DAY;
    private boolean shouldStop = false;
    private int notificationID = 1;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("start", "OnStartCommand");



        local = new LocalDBManager(getApplicationContext());

        cursor = getAllTodaysAlerts(); //also updates @param:CURRENT_DAY

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationBuilder = new Notification.Builder(getApplicationContext());
        NotificationBuilder.setVibrate(new long[]{100, 1000});

        //return to the last open activity
        Intent getBack = new Intent(this, MainActivity.class);
        getBack.setAction(Intent.ACTION_MAIN);
        getBack.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, getBack, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationBuilder.setContentIntent(pendingIntent);

        startThreading();
        return Service.START_STICKY;
    }

    private void showNotification(String notiName, int times)
    {
        String msg = "It's time to do "+notiName+". "+times+" repeats";
        NotificationBuilder
                .setContentTitle(notiName)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_medico)
                .setContentText(msg)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis());
        mNotificationManager.notify(notificationID++, NotificationBuilder.build());
    }

    private Cursor getAllTodaysAlerts()
    {
        Calendar calendar = Calendar.getInstance();
        CURRENT_DAY = calendar.get(Calendar.DAY_OF_WEEK);
        switch (CURRENT_DAY) {
            case Calendar.SUNDAY:    return local.getAllAlertsByDay(LocalDBManager.SUNDAY);
            case Calendar.MONDAY:    return local.getAllAlertsByDay(LocalDBManager.MONDAY);
            case Calendar.TUESDAY:   return local.getAllAlertsByDay(LocalDBManager.TUESDAY);
            case Calendar.WEDNESDAY: return local.getAllAlertsByDay(LocalDBManager.WEDNESDAY);
            case Calendar.THURSDAY:  return local.getAllAlertsByDay(LocalDBManager.THURSDAY);
            case Calendar.FRIDAY:    return local.getAllAlertsByDay(LocalDBManager.FRIDAY);
            case Calendar.SATURDAY:  return local.getAllAlertsByDay(LocalDBManager.SATURDAY);
            default:                 return local.getAllAlerts();
        }//now cursor initiated with all the alerts today
    }

    private void startThreading()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar;
                int currentHour;
                int currentMinutes;
                String time;
                while (!shouldStop) {
                    try {
                        synchronized (this) {
                            calendar = Calendar.getInstance();
                            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                            currentMinutes = calendar.get(Calendar.MINUTE);
                            Log.i("OMRI", "min: " + currentMinutes);
                            Log.i("OMRI", "hour: " + currentHour);

                            cursor = getAllTodaysAlerts();

                            cursor.moveToFirst();
                            Log.i("OMRI", "cSize: " + cursor.getCount());
                            while (cursor.moveToNext()) {
                                time = cursor.getString(cursor.getColumnIndex(LocalDBManager.KEY_TIME));
                                int gap = 2;
                                if (currentHour < 10) gap = 1;
                                int notiHour = Integer.parseInt(time.substring(0, gap));
                                int notiMinute = Integer.parseInt(time.substring(gap + 3));
                                Log.i("OMRI", "minNOTI: " + notiMinute);
                                Log.i("OMRI", "hourNOTI: " + notiHour);
                                if (notiHour == currentHour && notiMinute == currentMinutes)
                                //now we need to show notification!!
                                {
                                    Log.i("OMRI", "now im suppose to notify");
                                    String notiName = cursor.getString(cursor.getColumnIndex(LocalDBManager.KEY_NAME));
                                    int repeats = cursor.getInt(cursor.getColumnIndex(LocalDBManager.KEY_REPEATS));
                                    showNotification(notiName, repeats);
                                }
                            }
                            Thread.sleep(60000);//60 sec
                        }
                    } catch (Exception e) {
                        Log.i("OMRI", "Exception: " + e.getMessage());
                        shouldStop = true;
                    }
                }
            }
        }).start();
    }


}
