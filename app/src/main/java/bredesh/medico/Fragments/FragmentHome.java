package bredesh.medico.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import bredesh.medico.Fragments.PictureItem.AlertAdapter;
import bredesh.medico.Fragments.PictureItem.VideoItem;
import bredesh.medico.PushNotfications.NotificationService;
import bredesh.medico.R;


public class FragmentHome extends Fragment {

    GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment_home, container, false);
        gridView = (GridView) view.findViewById(R.id.gridView);

        final AlertAdapter alertAdapter = new AlertAdapter(getActivity().getApplicationContext());
        gridView.setAdapter(alertAdapter);

        final Intent SERVICE_INTENT = new Intent(getActivity().getBaseContext(), NotificationService.class);
        getActivity().startService(SERVICE_INTENT);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Uri videoUri = ((VideoItem)(alertAdapter.getItem(position))).getUri();
            Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
            startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        gridView.setAdapter(new AlertAdapter(getActivity().getApplicationContext()));

    }
}
