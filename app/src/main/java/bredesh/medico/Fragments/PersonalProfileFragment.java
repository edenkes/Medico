package bredesh.medico.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.GregorianCalendar;

import bredesh.medico.CalculatedPoints;
import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.PointsCalculator;
import bredesh.medico.R;


public class PersonalProfileFragment extends Fragment {
    private MedicoDB dbManager;

    private TextView txCurrentUserName, txPointsGathered, txPossiblePoints;
    private PointsCalculator pointsCalculator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_profile, container, false);

        dbManager  = new MedicoDB(getActivity().getApplicationContext());

        setupInfoFromDB(view);

        // Inflate the layout for this fragment
        return view;
    }


    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private void setupInfoFromDB(View view) {

        txCurrentUserName = (TextView) view.findViewById(R.id.txCurrentUserName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            readContactInfo();
        }

        txPointsGathered = (TextView) view.findViewById(R.id.txPointsGathered);
        txPossiblePoints = (TextView) view.findViewById(R.id.txPossiblePoints);
        pointsCalculator = new PointsCalculator(getActivity());

        CalculatedPoints points = pointsCalculator.CalculatePoints(new GregorianCalendar(), new GregorianCalendar());

        txPointsGathered.setText(Integer.toString(points.gainedPoints));
        txPossiblePoints.setText(Integer.toString(points.possiblePoints));
    }

    public void readContactInfo()
    {
        Cursor c = getActivity().getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            txCurrentUserName.setText(c.getString(c.getColumnIndex("display_name")));
            c.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                readContactInfo();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public void onResume() {
        super.onResume();
    }

}
