package bredesh.medico.Fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import bredesh.medico.Game.PersonalInfoDatabase;
import bredesh.medico.R;


public class FragmentPersonal extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PersonalInfoDatabase db = new PersonalInfoDatabase(getActivity().getApplicationContext());
        //Cursor cursor = db.getEmail();
        db.setFirstName("eden");
        String str = db.getFirstName();
//        Toast.makeText(getActivity().getApplicationContext(), str , Toast.LENGTH_LONG).show();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_personal, container, false);
    }

}
