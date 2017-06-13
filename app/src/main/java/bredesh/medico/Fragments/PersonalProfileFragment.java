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
import android.widget.ImageButton;
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
import java.util.GregorianCalendar;

import bredesh.medico.CalculatedPoints;
import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.PointsCalculator;
import bredesh.medico.R;
import bredesh.medico.Utils.SwipeDetector;


class PointsInfo
{
    float low;
    public int todayMsgId;
    public int prevDaysMsgId;
    public int iconId;

    PointsInfo(float low, int id, int prevId, int iconId)
    {
        this.low = low;
        this.todayMsgId = id;
        this.prevDaysMsgId = prevId;
        this.iconId = iconId;
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
    ImageButton btGraph;

    final int daysOfTheWeek = 7;
    final String EasingStr = "en", RussianStr = "ru";
    BarChart barChart;
    private GregorianCalendar currentDate;
    private GregorianCalendar today = new GregorianCalendar();


    final PointsInfo[] pointsInfos =
            {
                    new PointsInfo(0f, R.string.points_msg_noexercises, R.string.points_msg_noexercises_prev,0),
                    new PointsInfo(1f, R.string.points_msg_well_done,R.string.points_msg_well_done_prev,R.drawable.icons8_trophy_gold_100),
                    new PointsInfo(0.9f, R.string.points_msg_very_good,R.string.points_msg_very_good_prev,R.drawable.icons8_trophy_blue_100),
                    new PointsInfo(0.8f, R.string.points_msg_not_bad,R.string.points_msg_not_bad_prev,R.drawable.icons8_diploma_100),
                    new PointsInfo(0.7f, R.string.points_msg_need_work,R.string.points_msg_need_work_prev,R.drawable.icons8_thumb_up_100),
                    new PointsInfo(0f, R.string.points_msg_need_work,R.string.points_msg_need_work_prev,0)
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_profile, container, false);
        barChart = (BarChart) view.findViewById(R.id.chart);

        SwipeDetector swipeDetector = new SwipeDetector(view);

        resources = getResources();

        setupInfoFromDB(view);

        swipeDetector.setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum SwipeType) {
                if (SwipeType == SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT) {
                    if (currentDate.compareTo(today) == -1) {
                        currentDate.add(Calendar.DATE, 1);
                        setDayPoints(pointsCalculator.CalculatePoints(currentDate, currentDate));
                    }
                }
                else if (SwipeType == SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT) {
                    currentDate.add(Calendar.DATE, -1);
                    setDayPoints(pointsCalculator.CalculatePoints(currentDate, currentDate));
                }

            }
        });

        setBarChart();

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


    private void setDayPoints(CalculatedPoints calculatedPoints)
    {
        gauge.setHighValue(calculatedPoints.gainedPoints, 0, calculatedPoints.possiblePoints);
        txPointsGained.setText(resources.getString(R.string.you_gained_points, calculatedPoints.gainedPoints, calculatedPoints.possiblePoints));
        int pointsMsgId;
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());
        String dateText = dateFormat.format(currentDate.getTime());
        tvCurrentDayDate.setText(dateText);

        int dayDiff =(int)daysBetween(today, currentDate );

        boolean today = false;

        switch (dayDiff)
        {
            case 0:
                tvCurrentDayText.setText(resources.getString(R.string.days_today));
                today = true;
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

        int trophyIconId = 0;

        pointsMsgId = pointsInfos[0].todayMsgId;
        if (pointsRatio >=0) {
            for (int i = 1; i < pointsInfos.length; i++) {
                if (pointsRatio>=pointsInfos[i].low) {
                    pointsMsgId = today? pointsInfos[i].todayMsgId : pointsInfos[i].prevDaysMsgId;
                    trophyIconId = pointsInfos[i].iconId;
                    break;
                }
            }
        }
        tvPointsMessage.setText(resources.getString(pointsMsgId, calculatedPoints.possiblePoints - calculatedPoints.gainedPoints));
        if (trophyIconId != 0)
        {
            ivTrophy.setImageDrawable(resources.getDrawable(trophyIconId, null));
            ivTrophy.setVisibility(View.VISIBLE);
        }
        else
        {
            ivTrophy.setVisibility(View.GONE);
        }
    }

    private void setupInfoFromDB(final View view) {

        txCurrentUserName = (TextView) view.findViewById(R.id.txCurrentUserName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            readContactInfo();
        }

        Activity activity = getActivity();
        final Resources resources = activity.getResources();
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
        btGraph = (ImageButton) view.findViewById(R.id.btGraph);

        btGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dailyView = view.findViewById(R.id.llDailyPoints);
                View graphView= view.findViewById(R.id.llGraphLayout);
                if (dailyView.getVisibility() != View.GONE)
                {
                    dailyView.setVisibility(View.GONE);
                    graphView.setVisibility(View.VISIBLE);
                    ((ImageButton) v).setImageDrawable(resources.getDrawable(R.drawable.icons8_trophy_blue_100, null));
                }
                else
                {
                    dailyView.setVisibility(View.VISIBLE);
                    graphView.setVisibility(View.GONE);
                    barChart.animateY(5000);
                    ((ImageButton) v).setImageDrawable(resources.getDrawable(R.drawable.ic_insert_chart_black_24dp, null));
                }
            }
        });

        setDayPoints(pointsToday);
    }

    private void setBarChart() {
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
            //  Check the language for deciding if to read left to right.
            String language = dbManager.getLang();
            if(language.compareTo(EasingStr)==0 || language.compareTo(RussianStr)==0)
                timeCalendar.add(Calendar.DATE, i);
            else    timeCalendar.add(Calendar.DATE, -i);
            String dayStr = days[timeCalendar.get(Calendar.DAY_OF_WEEK) - 1];
            daysLabels.add(dayStr);                    // adding the the day name to label list

            CalculatedPoints points = pointsCalculator.CalculatePoints(timeCalendar, timeCalendar);
            float value_gainedPoints = points.gainedPoints;
            float value_possiblePoints = points.possiblePoints;
            groupGainedPoints.add(new BarEntry(value_gainedPoints, i));         //adding the gained points
            groupPossiblePoints.add(new BarEntry(value_possiblePoints, i));     //adding the max points for this day
        }

        BarDataSet barDataSet1 = new BarDataSet(groupGainedPoints, getResources().getString(R.string.pointsGained));
        barDataSet1.setColor(Color.rgb(35, 155, 100));
//        barDataSet1.setColors(ColorTemplate.LIBERTY_COLORS);

        BarDataSet barDataSet2 = new BarDataSet(groupPossiblePoints, getResources().getString(R.string.possiblePoints));
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
        setBarChart();
    }

}
