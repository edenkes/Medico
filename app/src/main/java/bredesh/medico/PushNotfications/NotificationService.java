package bredesh.medico.PushNotfications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import java.util.Calendar;
import bredesh.medico.Camera.LocalDBManager;
import bredesh.medico.MainActivity;

public class NotificationService extends Service {

    private NotificationManager mNotificationManager;
    private Notification.Builder NotificationBuilder;
    private LocalDBManager localdb;
    private Cursor cursor;
    private int CURRENT_DAY;
    private boolean shouldStop = false;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        localdb = new LocalDBManager(getApplicationContext());

        cursor = getAllTodaysAlerts(); //also updates @param:CURRENT_DAY



        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationBuilder = new Notification.Builder(getApplicationContext());
        NotificationBuilder.setVibrate(new long[] {100,1000});

        //return to the last open activity
        Intent getBack = new Intent(this, MainActivity.class);
        getBack.setAction(Intent.ACTION_MAIN);
        getBack.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, getBack, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationBuilder.setContentIntent(pendingIntent);


        new Thread(new Runnable() {
            @Override
            public void run() {

                Calendar calendar;
                int currentHour;
                int currentMinutes;
                String time;
                while(!shouldStop)
                {
                    try {
                        Thread.sleep(30000);//30 sec
                        calendar = Calendar.getInstance();
                        currentHour = calendar.get(Calendar.HOUR);
                        currentMinutes = calendar.get(Calendar.MINUTE);

                        if(calendar.get(Calendar.DAY_OF_WEEK) != CURRENT_DAY)
                            getAllTodaysAlerts();

                        cursor.moveToFirst();
                        while(cursor.moveToNext())
                        {
                            time = cursor.getString(cursor.getColumnIndex(LocalDBManager.KEY_TIME));
                            if(Integer.parseInt(time.substring(0,2)) == currentHour &&
                                Integer.parseInt(time.substring(3)) == currentMinutes)
                                //now we need to show notification!!
                            {
                                String notiName = cursor.getString(cursor.getColumnIndex(LocalDBManager.KEY_NAME));
                                showNotification(notiName);
                            }
                        }
                    } catch (Exception e) {
                        stopSelf();
                    }
                }


                //Stop service once it finishes its task
                stopSelf();
            }
        }).start();


        return START_STICKY;
    }

    private void showNotification(String notiName)
    {
        NotificationBuilder
                .setContentTitle(notiName)
                .setWhen(System.currentTimeMillis());
      //TODO: change api to 16 and then write this :  mNotificationManager.notify(1, NotificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shouldStop = true;
        mNotificationManager.cancelAll();
    }

    private Cursor getAllTodaysAlerts()
    {
        Calendar calendar = Calendar.getInstance();
        CURRENT_DAY = calendar.get(Calendar.DAY_OF_WEEK);
        switch (CURRENT_DAY) {
            case Calendar.SUNDAY:    return localdb.getAllAlertsByDay(LocalDBManager.SUNDAY);
            case Calendar.MONDAY:    return localdb.getAllAlertsByDay(LocalDBManager.MONDAY);
            case Calendar.TUESDAY:   return localdb.getAllAlertsByDay(LocalDBManager.TUESDAY);
            case Calendar.WEDNESDAY: return localdb.getAllAlertsByDay(LocalDBManager.WEDNESDAY);
            case Calendar.THURSDAY:  return localdb.getAllAlertsByDay(LocalDBManager.THURSDAY);
            case Calendar.FRIDAY:    return localdb.getAllAlertsByDay(LocalDBManager.FRIDAY);
            case Calendar.SATURDAY:  return localdb.getAllAlertsByDay(LocalDBManager.SATURDAY);
            default:                 return localdb.getAllAlerts();
        }//now cursor initiated with all the alerts today
    }


}
