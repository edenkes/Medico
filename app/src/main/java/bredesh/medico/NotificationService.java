package bredesh.medico;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

import bredesh.medico.Camera.ChangeVideoData;
import bredesh.medico.Camera.LocalDBManager;

public class NotificationService extends Service {
    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id;
    private RemoteViews remoteViews;


    private NotificationManager mNotificationManager;
    private Notification.Builder NotificationBuilder;
    private LocalDBManager local;
    private Cursor cursor;
    private int CURRENT_DAY;
    private boolean shouldStop = false;
    private int notificationID = 1;
    private final String MyOnClick = "button_click";

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("start", "OnStartCommand");

        //Notifications
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(getApplicationContext());

        remoteViews = new RemoteViews(getApplicationContext().getPackageName(),R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.notif_icon, R.mipmap.ic_medico_logo);
        remoteViews.setTextViewText(R.id.notif_title,"TEXT");
        remoteViews.setProgressBar(R.id.progressBar,100,40,true);
        //End of Notifications


        Calendar calendar = Calendar.getInstance();
        local = new LocalDBManager(getApplicationContext());

        CURRENT_DAY = calendar.get(Calendar.DAY_OF_WEEK);
        cursor = getAllTodayAlerts();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationBuilder = new Notification.Builder(getApplicationContext());

        //return to the last open activity
        Intent getBack = new Intent(this, MainActivity.class);
        getBack.setAction(Intent.ACTION_MAIN);
        getBack.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, getBack, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationBuilder.setContentIntent(pendingIntent);

//        startThreading();
        return Service.START_STICKY;
    }

    private void showNotification(String notiName, int times)
    {
    /*    Intent dismissIntent = new Intent(this, NotificationService.class);
        dismissIntent.setAction();
        PendingIntent piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(null, "Previous", prevPendingIntent).build();
*/
    /*
        Intent acceptIntent = new Intent(this, MainActivity.class);
        PendingIntent piAccept = PendingIntent.getActivity(this,0,acceptIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        String msg = "It's time to do "+notiName+".\n"+times+" repeats";
        NotificationBuilder
                .setContentTitle(notiName)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_medico_logo)
                .setAutoCancel(true)
                .setContentText(msg)
                .addAction(android.R.drawable.checkbox_on_background,"Accept", piAccept)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis());

        mNotificationManager.notify(notificationID++, NotificationBuilder.build());
    */
        notification_id = (int) System.currentTimeMillis();

        Intent button_intent = new Intent(getApplicationContext(),NotifictionWindow.class);
//        Intent button_intent = new Intent(MyOnClick);
        button_intent.putExtra("id",notification_id);
        PendingIntent button_pending_event = PendingIntent.getActivity(getApplicationContext(),notification_id,
                button_intent,PendingIntent.FLAG_UPDATE_CURRENT);


        remoteViews.setOnClickPendingIntent(R.id.btDone, button_pending_event);
        remoteViews.setOnClickPendingIntent(R.id.btCancel, button_pending_event);
        remoteViews.setOnClickPendingIntent(R.id.btSnoozed, button_pending_event);

        Intent notification_intent = new Intent(getApplicationContext(),NotifictionWindow.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,notification_intent,0);
        remoteViews.setTextViewText(R.id.notif_title,notiName);

        Intent acceptIntent = new Intent(this, NotifictionWindow.class);
        PendingIntent piAccept = PendingIntent.getActivity(this,0,acceptIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        String msg = "Select to show alarm screen. "+notiName;
//        String msg = "It's time to do "+notiName+".\n"+times+" repeats";

        builder.setSmallIcon(R.mipmap.ic_medico_logo)
                .setAutoCancel(true)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntent)
                .setContentTitle(notiName)
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
/*
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
                            cursor = getAllTodayAlerts();
                            if(cursor.getCount() > 0) {
                                calendar = Calendar.getInstance();
                                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                                currentMinutes = calendar.get(Calendar.MINUTE);
                                Log.i("normal", "currentHour:    "+currentHour);
                                Log.i("normal", "currentMinutes: "+currentMinutes);
                                if(CURRENT_DAY != calendar.get(Calendar.DAY_OF_WEEK))
                                {
                                    cursor.moveToFirst();
                                    while (cursor.moveToNext()) {
                                        local.allTodaysAlertReset(cursor.getInt(cursor.getColumnIndex(LocalDBManager.KEY_ID)));
                                    }
                                    CURRENT_DAY = calendar.get(Calendar.DAY_OF_WEEK);
                                    cursor = getAllTodayAlerts();
                                }
                                Log.i("normal", "cursoe size: " + cursor.getCount());
                                cursor.moveToFirst();
                                do {
                                    time = cursor.getString(cursor.getColumnIndex(LocalDBManager.KEY_TIME));
                                    int gap = 2;
                                    if (currentHour < 10) gap = 1;
                                    int notificationHour = Integer.parseInt(time.substring(0, gap));
                                    int notificationMinute = Integer.parseInt(time.substring(gap + 3));
                                    int todayAlert = cursor.getInt(cursor.getColumnIndex(LocalDBManager.ALERT_TODAY));
                                    Log.i("normal", "notificationHour: " + notificationHour);
                                    Log.i("normal", "notificationMinute: " + notificationMinute);
                                    if (notificationHour == currentHour && notificationMinute == currentMinutes && todayAlert == 0) {
                                        Log.i("normal", "enter alert");
                                        //now we need to show notification!!
                                        String notificationName = cursor.getString(cursor.getColumnIndex(LocalDBManager.KEY_NAME));
                                        int repeats = cursor.getInt(cursor.getColumnIndex(LocalDBManager.KEY_REPEATS));
                                        showNotification(notificationName, repeats);
                                        local.updateAlertToday(cursor.getInt(cursor.getColumnIndex(LocalDBManager.KEY_ID)));
                                    }
                                } while (cursor.moveToNext());
                            }
                            Thread.sleep(10000);//60 sec
                        }
                    } catch (Exception e) {
                        Log.i("Exception", "Exception: " + e.getMessage());
                        shouldStop = true;
                    }
                }
            }
        }).start();
    }*/
}
