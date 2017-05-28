package bredesh.medico.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bredesh.medico.NotificationService;

public class StartUp extends BroadcastReceiver {

    public StartUp() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // start your service here
        context.startService(new Intent(context, NotificationService.class));
    }

}