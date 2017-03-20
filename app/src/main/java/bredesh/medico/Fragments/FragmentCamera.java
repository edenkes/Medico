package bredesh.medico.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import bredesh.medico.Camera.VideoData;
import bredesh.medico.Fragments.PictureItem.AddNewItem;
import bredesh.medico.R;


public class FragmentCamera extends Fragment {

    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    Button btnAddNew;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_camera, container, false);

        Button camera, video;

        camera = (Button) view.findViewById(R.id.buttonCamera);
        btnAddNew = (Button) view.findViewById(R.id.btnAddNew);

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewItem.class);
                startActivity(intent);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent takeImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takeImageIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takeImageIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        video = (Button) view.findViewById(R.id.buttonVideo);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == Activity.RESULT_OK)
        {
            if(requestCode == REQUEST_VIDEO_CAPTURE){
                intent.putExtra("last_recorded_video_uri", intent.getData().toString());
                startActivity(new Intent(getActivity(),VideoData.class));
            }
            else if(requestCode == REQUEST_IMAGE_CAPTURE){
                intent.putExtra("last_captured_image_uri", intent.getData().toString());
                startActivity(new Intent(getActivity(), VideoData.class));
            }
        }

    }

}