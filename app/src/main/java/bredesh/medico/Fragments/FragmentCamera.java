package bredesh.medico.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import bredesh.medico.Camera.VideoData;
import bredesh.medico.R;

import static android.app.Activity.RESULT_OK;


public class FragmentCamera extends Fragment {

    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_camera, container, false);

        ImageButton camera, video, removeAll;

        camera = (ImageButton) view.findViewById(R.id.btCamera);
        camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent takeImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takeImageIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takeImageIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        video = (ImageButton) view.findViewById(R.id.btVideo);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
            }
        });




        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            if(resultCode == RESULT_OK && intent != null ){
                if(intent.getData() != null){
                        intent.putExtra("RecordedUri", intent.getData().toString());
                        intent.setClass(getActivity(), VideoData.class);
                        startActivity(intent);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Couldn't attach the video/picture", Toast.LENGTH_LONG).show();
                    intent.putExtra("RecordedUri", "null");
                    intent.setClass(getActivity(), VideoData.class);
                    startActivity(intent);
                }
        }
    }
}