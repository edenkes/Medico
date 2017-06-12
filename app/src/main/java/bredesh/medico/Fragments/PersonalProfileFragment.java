package bredesh.medico.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.sccomponents.widgets.ScArcGauge;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import bredesh.medico.CalculatedPoints;
import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.PointsCalculator;
import bredesh.medico.R;
import bredesh.medico.Utils.SwipeDetector;


class PointsInfo
{
    public float low;
    public int id;

    public PointsInfo(float low, int id)
    {
        this.low = low;
        this.id = id;
    }
}

public class PersonalProfileFragment extends Fragment {
    private TextView txCurrentUserName;
    private PointsCalculator pointsCalculator;
    private Resources resources;
    private boolean isOnlyGainedPoint = false;
    private ScArcGauge gauge;
    TextView txPointsGained, tvPointsMessage, tvCurrentDayText, tvCurrentDayDate;
    ImageView ivTrophy;

    final int daysOfTheWeek = 7;
    final String EasingStr = "en", RussianStr = "ru";
    private GregorianCalendar currentDate, yesterday, twoDaysAgo;
    private GregorianCalendar today = new GregorianCalendar();


    final PointsInfo[] pointsInfos =
            {
                    new PointsInfo(0f, R.string.points_msg_noexercises),
                    new PointsInfo(1f, R.string.points_msg_well_done),
                    new PointsInfo(0.7f, R.string.points_msg_very_good),
                    new PointsInfo(0.35f, R.string.points_msg_not_bad),
                    new PointsInfo(0f, R.string.points_msg_can_do_better)
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_profile, container, false);

        SwipeDetector swipeDetector = new SwipeDetector(view);

        resources = getResources();

        setupInfoFromDB(view);

        swipeDetector.setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum SwipeType) {
                if (SwipeType == SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT) {
                    if (currentDate.compareTo(today) == -1) {
                        currentDate.add(Calendar.DATE, 1);
                        setDayPoints(v, pointsCalculator.CalculatePoints(currentDate, currentDate));
                    }
                }
                else if (SwipeType == SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT) {
                    currentDate.add(Calendar.DATE, -1);
                    setDayPoints(v, pointsCalculator.CalculatePoints(currentDate, currentDate));
                }

            }
        });

        setBarChart(view);

        // Inflate the layout for this fragment
        return view;
    }


    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private long daysBetween(GregorianCalendar first, GregorianCalendar second)
    {
        long firstTime = first.getTime().getTime();
        long secondTime = second.getTime().getTime();
        return ( firstTime - secondTime )/ (1000*60*60*24);
    }


    private void setDayPoints(View view, CalculatedPoints calculatedPoints)
    {
        gauge.setHighValue(calculatedPoints.gainedPoints, 0, calculatedPoints.possiblePoints);
        txPointsGained.setText(resources.getString(R.string.you_gained_points, calculatedPoints.gainedPoints, calculatedPoints.possiblePoints));
        int pointsMsgId;
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
        String dateText = dateFormat.format(currentDate.getTime());
        tvCurrentDayDate.setText(dateText);

        int dayDiff =(int)daysBetween(today, currentDate );

        switch (dayDiff)
        {
            case 0:
                tvCurrentDayText.setText(resources.getString(R.string.days_today));
                break;
            case 1:
                tvCurrentDayText.setText(resources.getString(R.string.days_yesterday));
                break;
            case 2:
                tvCurrentDayText.setText(resources.getString(R.string.days_2_days_ago));
                break;
            default:
                tvCurrentDayText.setText(resources.getString(R.string.days_days_ago,dayDiff));
                break;
        }


        float pointsRatio = calculatedPoints.possiblePoints > 0? calculatedPoints.gainedPoints / (float) calculatedPoints.possiblePoints : -1;
        if (calculatedPoints.possiblePoints == 0 || calculatedPoints.gainedPoints == 0) {
            ivTrophy.setVisibility(View.GONE);
        }
        else {
            ivTrophy.setVisibility(View.VISIBLE);
            if (calculatedPoints.gainedPoints / (float) calculatedPoints.possiblePoints > 0.5)
                ivTrophy.setImageDrawable(resources.getDrawable(R.drawable.icons8_trophy_gold_100, null));
            else
                ivTrophy.setImageDrawable(resources.getDrawable(R.drawable.icons8_trophy_blue_100, null));
        }
        pointsMsgId = pointsInfos[0].id;
        if (pointsRatio >=0) {
            for (int i = 1; i < pointsInfos.length; i++) {
                if (pointsRatio>=pointsInfos[i].low) {
                    pointsMsgId = pointsInfos[i].id;
                    break;
                }
            }
        }
        tvPointsMessage.setText(resources.getString(pointsMsgId));
    }

    private void setupInfoFromDB(View view) {

        txCurrentUserName = (TextView) view.findViewById(R.id.txCurrentUserName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            readContactInfo();
        }

        Activity activity = getActivity();
        Resources resources = activity.getResources();
        pointsCalculator = new PointsCalculator(activity);
        today.set(Calendar.HOUR_OF_DAY,0);
        today.set(Calendar.MINUTE,0);
        today.set(Calendar.SECOND,0);
        today.set(Calendar.MILLISECOND,0);
        currentDate = new GregorianCalendar();
        currentDate.set(Calendar.HOUR_OF_DAY,0);
        currentDate.set(Calendar.MINUTE,0);
        currentDate.set(Calendar.SECOND,0);
        currentDate.set(Calendar.MILLISECOND,0);
        
        
        CalculatedPoints pointsToday = pointsCalculator.CalculatePoints(currentDate, currentDate);

        tvCurrentDayDate = (TextView) view.findViewById(R.id.tvCurrentDayDate);
        tvCurrentDayText = (TextView) view.findViewById(R.id.tvCurrentDayText);
        gauge = (ScArcGauge) view.findViewById(R.id.gauge);
        txPointsGained = (TextView) view.findViewById(R.id.txPointsGained);

        // Set the features stroke cap style to rounded
        gauge.findFeature(ScArcGauge.BASE_IDENTIFIER)
                .getPainter().setStrokeCap(Paint.Cap.ROUND);
        gauge.findFeature(ScArcGauge.PROGRESS_IDENTIFIER)
                .getPainter().setStrokeCap(Paint.Cap.ROUND);
        ivTrophy = (ImageView) view.findViewById(R.id.ivTrophy);
        tvPointsMessage = (TextView) view.findViewById(R.id.tvPointsMessage);

        setDayPoints(view, pointsToday);



        // If you set the value from the xml that not produce an event so I will change the
        // value from code.
        //gauge.setHighValue(60);

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
