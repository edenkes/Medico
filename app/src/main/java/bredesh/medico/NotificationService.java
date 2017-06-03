package bredesh.medico;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

import bredesh.medico.Camera.LocalDBManager;
import bredesh.medico.Notification.NotificationWindow;

public class NotificationService extends Service {
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id;
     //private RemoteViews remoteViews;


    private LocalDBManager local;
    private Cursor cursor;
    private int CURRENT_DAY;
    private boolean shouldStop = false;

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(getApplicationContext());
/*
        remoteViews = new RemoteViews(getApplicationContext().getPackageName(),R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.notif_icon, R.mipmap.ic_medico_logo);
        remoteViews.setTextViewText(R.id.notif_title,"TEXT");
        remoteViews.setProgressBar(R.id.progressBar,100,40,true);
        //End of Notifications
*/

        Calendar calendar = Calendar.getInstance();
        local = new LocalDBManager(getApplicationContext());

        CURRENT_DAY = calendar.get(Calendar.DAY_OF_WEEK);
        cursor = getAllTodayAlerts();

        /*
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationBuilder = new Notification.Builder(getApplicationContext());

        //return to the last open activity
        Intent getBack = new Intent(this, MainActivity.class);
        getBack.setAction(Intent.ACTION_MAIN);
        getBack.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, getBack, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationBuilder.setContentIntent(pendingIntent);
*/
        startThreading();
        return Service.START_STICKY;
    }

    private void showNotification(String notificationName, int times, int notiID) {
        notification_id = (int) System.currentTimeMillis();

        if(notificationName.length() >=7 && notificationName.substring(0,7).equals("_TEMP__"))
            notificationName = notificationName.substring(7);

        Intent button_intent = new Intent(getApplicationContext(),NotificationWindow.class);
        button_intent.putExtra("id",notification_id);
        button_intent.putExtra("db_id",notiID);
        button_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
/*
        PendingIntent button_pending_event = PendingIntent.getActivity(getApplicationContext(),notification_id,
                button_intent,PendingIntent.FLAG_UPDATE_CURRENT);


        remoteViews.setOnClickPendingIntent(R.id.btDone, button_pending_event);
        remoteViews.setOnClickPendingIntent(R.id.btCancel, button_pending_event);
        remoteViews.setOnClickPendingIntent(R.id.btSnoozed, button_pending_event);
*/
        Intent getBack = new Intent(getApplicationContext(),NotificationWindow.class);
        getBack.putExtra("db_id",notiID);
        getBack.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,getBack, PendingIntent.FLAG_UPDATE_CURRENT);
    //    remoteViews.setTextViewText(R.id.notif_title,notiName);

        String msg = getResources().getString(R.string.alert_text, notificationName ,times);

        builder.setSmallIcon(R.mipmap.ic_medico_logo)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX)
           //     .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntent)
                .setContentTitle(notificationName)
                .setWhen(System.currentTimeMillis())
                .setContentText(msg)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH);

        notificationManager.notify(notification_id,builder.build());
    }

    private Cursor getAllTodayAlerts()
    {
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
            while (!shouldStop) {  try { synchronized (this) {
                cursor = getAllTodayAlerts();

                if(cursor.getCount() > 0) {

                    calendar = Calendar.getInstance();
                    currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                    currentMinutes = calendar.get(Calendar.MINUTE);
                    if(CURRENT_DAY != calendar.get(Calendar.DAY_OF_WEEK))
                    {
                        cursor.moveToFirst();
                        while (cursor.moveToNext())
                            local.allTodaysAlertReset(cursor.getInt(cursor.getColumnIndex(LocalDBManager.KEY_ID)));
                        CURRENT_DAY = calendar.get(Calendar.DAY_OF_WEEK);
                        cursor = getAllTodayAlerts();
                    }
                    cursor.moveToFirst();
                    Log.i("break","break2");

                    do {
                        String allTimes=cursor.getString(cursor.getColumnIndex(LocalDBManager.KEY_TIME));
                        String[] times = allTimes.split(getResources().getString(R.string.times_splitter));

                        for (int i=0; i< times.length; i++) {
                            time = times[i];
                            int gap = 2;
                            //if (currentHour < 10) gap = 1;
                            int notificationHour = Integer.parseInt(time.substring(0, gap));
                            int notificationMinute = Integer.parseInt(time.substring(gap + 3));
                            String todayAlert = cursor.getString(cursor.getColumnIndex(LocalDBManager.ALERT_TODAY));
                            Log.i("omri","notificationHour: "+notificationHour);
                            Log.i("omri","currentHour: "+currentHour);
                            Log.i("omri","notificationMinute: "+notificationMinute);
                            Log.i("omri","currentMinutes: "+currentMinutes);
                            Log.i("omri","todayAlert.compareTo(time): "+todayAlert.compareTo(time));
                            if (notificationHour == currentHour && notificationMinute == currentMinutes && todayAlert.compareTo(time) < 0) {
                                //now we need to show notification!!
                                String notificationName = cursor.getString(cursor.getColumnIndex(LocalDBManager.KEY_NAME));
                                int repeats = cursor.getInt(cursor.getColumnIndex(LocalDBManager.KEY_REPEATS));
                                showNotification(notificationName, repeats, cursor.getInt(cursor.getColumnIndex(LocalDBManager.KEY_ID)));
                                local.updateAlertToday(cursor.getInt(cursor.getColumnIndex(LocalDBManager.KEY_ID)), time);
                            }
                        }
                    } while (cursor.moveToNext());
                }
                Thread.sleep(10000);//60 sec
                } /*sync*/ }/* try*/ catch (Exception e) {
                    Log.i("Exception", "Exception: " + e.getMessage());
                    //shouldStop = true;
                }
            }
            }
        }).start();
    }
}
