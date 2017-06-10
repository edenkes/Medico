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
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import bredesh.medico.CalculatedPoints;
import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.PointsCalculator;
import bredesh.medico.R;


public class PersonalProfileFragment extends Fragment {
    private TextView txCurrentUserName;
    private PointsCalculator pointsCalculator;
    private Resources resources;
    private boolean isOnlyGainedPoint = false;

    final int daysOfTheWeek = 7;
    final String EasingStr = "en", RussianStr = "ru";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_profile, container, false);


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

        pointsCalculator = new PointsCalculator(getActivity());

        /*txPointsGathered = (TextView) view.findViewById(R.id.txPointsGathered);
        txPossiblePoints = (TextView) view.findViewById(R.id.txPossiblePoints);

        CalculatedPoints points = pointsCalculator.CalculatePoints(new GregorianCalendar(), new GregorianCalendar());

        txPointsGathered.setText(Integer.toString(points.gainedPoints));
        txPossiblePoints.setText(Integer.toString(points.possiblePoints));
*/

    }

    private void setBarChart(View view) {
        final BarChart barChart = (BarChart) view.findViewById(R.id.chart);
        MedicoDB dbManager  = new MedicoDB(getActivity().getApplicationContext());

        String[] days;
        days = new String[] { resources.getString(R.string.Sunday_short), resources.getString(R.string.Monday_Short),
                resources.getString(R.string.Tuesday_Short), resources.getString(R.string.Wednesday_Short),
                resources.getString(R.string.Thursday_Short), resources.getString(R.string.Friday_Short),
                resources.getString(R.string.Saturday_Short) };


        ArrayList<String> daysLabels;
        daysLabels = new ArrayList<>();
        ArrayList<BarEntry> groupGainedPoints = new ArrayList<>();      //         for create Grouped Bar chart
        ArrayList<BarEntry> groupPossiblePoints = new ArrayList<>();    //         for create Grouped Bar chart
        for (int i = 0; i < daysOfTheWeek; i++) {

            Calendar timeCalendar = new GregorianCalendar();
            int year = timeCalendar.get(Calendar.YEAR) - 1900;
            int month = timeCalendar.get(Calendar.MONTH);
            //  Check the language for deciding if to read left to right.
            String language = dbManager.getLang();
            int date;
            if(language.compareTo(EasingStr)==0 || language.compareTo(RussianStr)==0)
                date = timeCalendar.get(Calendar.DATE) - (6 - i);
            else date = timeCalendar.get(Calendar.DATE) - i;
            timeCalendar.setTime(new Date(year, month, date));

            String dayStr = days[timeCalendar.get(Calendar.DAY_OF_WEEK) - 1];
            daysLabels.add(dayStr);                    // adding the the day name to label list

            CalculatedPoints points = pointsCalculator.CalculatePoints(timeCalendar, timeCalendar);
            float value_gainedPoints = points.gainedPoints;
            float value_possiblePoints = points.possiblePoints;
            groupGainedPoints.add(new BarEntry(value_gainedPoints, i));         //adding the gained points
            groupPossiblePoints.add(new BarEntry(value_possiblePoints, i));     //adding the max points for this day
        }
        BarDataSet barDataSet1 = new BarDataSet(groupGainedPoints, "נקודות שצברת");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
//        barDataSet1.setColors(ColorTemplate.LIBERTY_COLORS);

        BarDataSet barDataSet2 = new BarDataSet(groupPossiblePoints, "מקסימום הנקודות שניתן לצבור ביום זה");
        barDataSet2.setColor(Color.rgb(12, 13, 73));

        ArrayList<BarDataSet> dataset = new ArrayList<>();
        dataset.add(barDataSet1);
        dataset.add(barDataSet2);

        ArrayList<BarDataSet> dataset2 = new ArrayList<>();
        dataset2.add(barDataSet1);

        final BarData data = new BarData(daysLabels, dataset);
        final BarData data2 = new BarData(daysLabels, dataset2);

        barChart.setData(data);
        barChart.animateY(5000);
        barChart.setDescription("");
        barChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnlyGainedPoint) {
                    barChart.setData(data);
                    barChart.animateY(3000);

                    isOnlyGainedPoint = false;
                }else{
                    barChart.setData(data2);
                    barChart.animateY(3000);

                    isOnlyGainedPoint = true;
                }
            }
        });
    }

    public void readContactInfo() {
        Cursor c = getActivity().getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            txCurrentUserName.setText(c.getString(c.getColumnIndex("display_name")));
            c.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                readContactInfo();
            } else
                Toast.makeText(getActivity(), "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
