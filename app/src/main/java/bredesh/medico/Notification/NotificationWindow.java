package bredesh.medico.Notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
