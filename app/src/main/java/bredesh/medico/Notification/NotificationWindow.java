package bredesh.medico.Notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.GregorianCalendar;

import bredesh.medico.MainActivity;
import bredesh.medico.R;

public class NotificationWindow extends AppCompatActivity {

    private Button btDone, btSnooze, btCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifiction_window);

        btDone = (Button) findViewById(R.id.btDone);
        btSnooze = (Button) findViewById(R.id.btSnooze);
        btCancel = (Button) findViewById(R.id.btCancel);
    }


    public void onClickDone(View view){
        Toast.makeText(getApplicationContext(), "Good job", Toast.LENGTH_LONG).show();
        finish();
        startActivity(new Intent(NotificationWindow.this,MainActivity.class));

    }

    public void onClickSnooze(View view){
        Toast.makeText(getApplicationContext(), "Alert been delay in five mints", Toast.LENGTH_LONG).show();

        Long alertTime = new GregorianCalendar().getTimeInMillis()+ 5*1000;

        Intent alertIntent = new Intent(this, AlertReceiver.class);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime,
                PendingIntent.getBroadcast(this, 1, alertIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT));

        finish();
        startActivity(new Intent(NotificationWindow.this,MainActivity.class));
    }


    public void onClickCancel(View view){
        Toast.makeText(getApplicationContext(), "Alert been cancel, Try again later", Toast.LENGTH_LONG).show();
        finish();
        startActivity(new Intent(NotificationWindow.this,MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Alert been cancel, Try again later", Toast.LENGTH_LONG).show();
        finish();
        startActivity(new Intent(NotificationWindow.this,MainActivity.class));
//        super.onBackPressed();
    }
}
