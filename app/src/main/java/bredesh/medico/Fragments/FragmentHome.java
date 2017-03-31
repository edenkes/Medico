package bredesh.medico.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import bredesh.medico.Fragments.PictureItem.AlertAdapter;
import bredesh.medico.PushNotfications.NotificationService;
import bredesh.medico.R;


public class FragmentHome extends Fragment {

    GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment_home, container, false);
        gridView = (GridView) view.findViewById(R.id.gridView);

        gridView.setAdapter(new AlertAdapter(getActivity().getApplicationContext()));

        final Intent SERVICE_INTENT = new Intent(getActivity().getBaseContext(), NotificationService.class);
        getActivity().startService(SERVICE_INTENT);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


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
