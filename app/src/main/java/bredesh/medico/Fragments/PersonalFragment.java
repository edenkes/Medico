package bredesh.medico.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import bredesh.medico.Fragments.PictureItem.RecyclerAdapter;
import bredesh.medico.MedicoDB;
import bredesh.medico.R;


public class PersonalFragment extends Fragment {
    private MedicoDB dbManager;

    private TextView etFirstName, etLastName, etEmailAddress, etPoints;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);

        dbManager  = new MedicoDB(getActivity().getApplicationContext());

        dbManager.setFirstName("eden");

        setupInfoFromDB(view);

        Button btEdit = (Button) view.findViewById(R.id.btEdit);
        btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), EditInfo.class));

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void setupInfoFromDB(View view) {
        etFirstName = (TextView) view.findViewById(R.id.etFirstName);

        etLastName = (TextView) view.findViewById(R.id.etLastName);

        etEmailAddress = (TextView) view.findViewById(R.id.etEmailAddress);

        etPoints = (TextView) view.findViewById(R.id.etPoints);

        setTexts();
    }

    private void setTexts() {
        String first_name;
        if ((first_name = dbManager.getFirstName()) == null)
            first_name = "";

        String last_name;
        if ((last_name = dbManager.getLastName()) == null)
            last_name = "";

        String email_address;
        if ((email_address = dbManager.getEmail()) == null)
            email_address = "";

        int points;
        if ((points = dbManager.getPoints()) < 0)
            points = 0;

        etFirstName.setText(first_name + "");

        etLastName.setText(last_name + "");

        etEmailAddress.setText(email_address + "");

        etPoints.setText(points + " " + getResources().getString(R.string.Points));
    }

    @Override
    public void onResume() {
        super.onResume();
        setTexts();
    }

}
