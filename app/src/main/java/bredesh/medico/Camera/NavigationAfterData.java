package bredesh.medico.Camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import bredesh.medico.R;

/**
 * Created by Omri on 07-Mar-17.
 */

public class NavigationAfterData extends Activity {

    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_after_data);

        Button record, capture, personalPlan;

        record = (Button) findViewById(R.id.record);
        capture = (Button) findViewById(R.id.capture);
        personalPlan = (Button) findViewById(R.id.personalplan);

        personalPlan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO: need to add intent to personal plan
            }
        });

        record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
            }
        });

        capture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent takeImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takeImageIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeImageIntent, REQUEST_VIDEO_CAPTURE);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == REQUEST_VIDEO_CAPTURE){
                intent.putExtra("last_recorded_video_uri", intent.getData().toString());
            }
            else if(requestCode == REQUEST_IMAGE_CAPTURE){
                intent.putExtra("last_captured_image_uri", intent.getData().toString());
            }

            startActivity(new Intent(NavigationAfterData.this, VideoData.class));
        }

    }
}
