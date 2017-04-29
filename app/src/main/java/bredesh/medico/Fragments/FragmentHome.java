package bredesh.medico.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import bredesh.medico.Fragments.PictureItem.AlertAdapter;
import bredesh.medico.Fragments.PictureItem.VideoItem;
import bredesh.medico.push_notifications.NotificationService;
import bredesh.medico.R;


public class FragmentHome extends Fragment  {

    ListView lvHome;
    Button btPlay;
    private Uri videoUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment_home, container, false);
        lvHome = (ListView) view.findViewById(R.id.lvHome);
//        btPlay = (Button) gridView.findViewById(R.id.btPlay);
        btPlay = (Button) view.findViewById(R.id.btPlay);

//        int numOfBars = view.gr

        final AlertAdapter alertAdapter = new AlertAdapter(getActivity().getApplicationContext());
        lvHome.setAdapter(alertAdapter);

        final Intent SERVICE_INTENT = new Intent(getActivity().getBaseContext(), NotificationService.class);
        getActivity().startService(SERVICE_INTENT);

        lvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity().getApplicationContext(), "onListItemClick: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();

                videoUri = ((VideoItem)(alertAdapter.getItem(position))).getUri();

                if(videoUri != null ) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                    startActivity(intent);
                }else
                    Toast.makeText(getActivity().getApplicationContext(), "Couldn't find the video/photo", Toast.LENGTH_SHORT).show();

                btPlay = (Button) parent.findViewById(R.id.btPlay);

                btPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(getActivity().getApplicationContext(), "onListItemClick: Play", Toast.LENGTH_SHORT).show();
                        if(videoUri != null ) {
//                            Toast.makeText(getActivity().getApplicationContext(), "Intent video", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                            startActivity(intent);
                        }else
                            Toast.makeText(getActivity().getApplicationContext(), "Couldn't find the video/photo", Toast.LENGTH_SHORT).show();

                    }
                });


            }

        });



        /*btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "onListItemClick: Main", Toast.LENGTH_SHORT).show();

            }
        });*/
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        lvHome.setAdapter(new AlertAdapter(getActivity().getApplicationContext()));

    }



}
