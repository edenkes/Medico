package bredesh.medico.Fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import bredesh.medico.Camera.LocalDBManager;
import bredesh.medico.R;


public class PersonalFragment extends Fragment {
    private int points;
    private String first_name;
    private String last_name;
    private String email_address;
    private LocalDBManager dbManager;

    private Button btAddPoints; //test
    private EditText etFirstName, etLastName, etEmailAddress, etPoints;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);

        setupInfoFromDB(view);


        btAddPoints = (Button) view.findViewById(R.id.btAddPoints); //test

        btAddPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dbManager.setPoints(points+=1);
                etPoints.setText(points + " points");
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void setupInfoFromDB(View view) {
        dbManager  = new LocalDBManager(getActivity().getApplicationContext());
        

        if ((first_name = dbManager.getFirstName()) == null)
            first_name = "";

        if ((last_name = dbManager.getLastName()) == null)
            last_name = "";

        if ((email_address = dbManager.getEmail()) == null)
            email_address = "";

        if ((points = dbManager.getPoints()) < 0)
            points = 0;


        etFirstName = (EditText) view.findViewById(R.id.etFirstName);

        etLastName = (EditText) view.findViewById(R.id.etLastName);

        etEmailAddress = (EditText) view.findViewById(R.id.etEmailAddress);

        etPoints = (EditText) view.findViewById(R.id.etPoints);


        etFirstName.setText(first_name + "");

        etLastName.setText(last_name + "");

        etEmailAddress.setText(email_address + "");

        etPoints.setText(points + " points");

    }

}
