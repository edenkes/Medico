package bredesh.medico.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import bredesh.medico.R;


public class FragmentRecents extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment_recents, container, false);

        String[] menuItem =
                {"Do somting,",
                        "Do else",
                        "Do another"};

        ListView listView = (ListView) view.findViewById(R.id.listView12);

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                menuItem
        );

        listView.setAdapter(stringArrayAdapter);

        // Inflate the layout for this fragment
        return view;
    }
}
