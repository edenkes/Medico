package bredesh.medico.Notification;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import bredesh.medico.R;

/**
 * Created by Omri on 21/05/2017.
 */

public class ClickedNotification extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamic_ex_info);

        TextView title = (TextView) findViewById(R.id.textView);
        Button accept = (Button) findViewById(R.id.button_accept);
        Button decline = (Button) findViewById(R.id.button_decline);
        Button snooz = (Button) findViewById(R.id.snooze);
    }
}
