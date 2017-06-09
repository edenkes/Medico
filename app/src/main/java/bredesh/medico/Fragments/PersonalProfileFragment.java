package bredesh.medico.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import bredesh.medico.CalculatedPoints;
import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.PointsCalculator;
import bredesh.medico.R;


public class PersonalProfileFragment extends Fragment {
    private MedicoDB dbManager;

    private TextView txCurrentUserName, txPointsGathered, txPossiblePoints;
    private PointsCalculator pointsCalculator;
    private Resources resources;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_profile, container, false);

        dbManager  = new MedicoDB(getActivity().getApplicationContext());

         resources = getResources();

        setupInfoFromDB(view);

        setBarChart(view);


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


        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        CalculatedPoints points2 = pointsCalculator.CalculatePoints(start, end);

        txPointsGathered.setText(Integer.toString(points.gainedPoints));
        txPossiblePoints.setText(Integer.toString(points.possiblePoints));

    }

    private void setBarChart(View view) {
        BarChart barChart = (BarChart) view.findViewById(R.id.chart);

        ArrayList<String> labels = new ArrayList<String>();
        labels.add(resources.getString(R.string.Sunday_short));
        labels.add(resources.getString(R.string.Monday_Short));
        labels.add(resources.getString(R.string.Tuesday_Short));
        labels.add(resources.getString(R.string.Wednesday_Short));
        labels.add(resources.getString(R.string.Thursday_Short));
        labels.add(resources.getString(R.string.Friday_Short));
        labels.add(resources.getString(R.string.Saturday_Short));

//         for create Grouped Bar chart
        ArrayList<BarEntry> groupGainedPoints = new ArrayList<>();
        groupGainedPoints.add(new BarEntry(4f, 0));
        groupGainedPoints.add(new BarEntry(8f, 1));
        groupGainedPoints.add(new BarEntry(6f, 2));
        groupGainedPoints.add(new BarEntry(12f, 3));
        groupGainedPoints.add(new BarEntry(18f, 4));
        groupGainedPoints.add(new BarEntry(9f, 5));
        groupGainedPoints.add(new BarEntry(13f, 6));

        ArrayList<BarEntry> groupPossiblePoints = new ArrayList<>();
        groupPossiblePoints.add(new BarEntry(6f, 0));
        groupPossiblePoints.add(new BarEntry(10f, 1));
        groupPossiblePoints.add(new BarEntry(8f, 2));
        groupPossiblePoints.add(new BarEntry(12f, 3));
        groupPossiblePoints.add(new BarEntry(20f, 4));
        groupPossiblePoints.add(new BarEntry(21f, 5));
        groupPossiblePoints.add(new BarEntry(15f, 6));

        BarDataSet barDataSet1 = new BarDataSet(groupGainedPoints, "נקודות שצברת");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
//        barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);

        BarDataSet barDataSet2 = new BarDataSet(groupPossiblePoints, "מקסימום הנקודות שניתן לצבור ביום זה");
//        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet2.setColor(Color.rgb(12, 13, 43));

        ArrayList<BarDataSet> dataset = new ArrayList<>();
        dataset.add(barDataSet1);
        dataset.add(barDataSet2);

        BarData data = new BarData(labels, dataset);
//        dataset.setColors(ColorTemplate.LIBERTY_COLORS); //
        barChart.setData(data);
        barChart.animateY(5000);
        barChart.setDescription("");
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
