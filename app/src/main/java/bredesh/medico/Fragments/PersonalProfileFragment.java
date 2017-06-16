package bredesh.medico.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.LayoutDirection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.sccomponents.widgets.ScArcGauge;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import bredesh.medico.CalculatedPoints;
import bredesh.medico.PointsCalculator;
import bredesh.medico.R;
import bredesh.medico.Utils.SwipeDetector;

class PointsInfo {
    float low;
    int todayMsgId;
    int prevDaysMsgId;
    int iconId;

    PointsInfo(float low, int id, int prevId, int iconId) {
        this.low = low;
        this.todayMsgId = id;
        this.prevDaysMsgId = prevId;
        this.iconId = iconId;
    }
}

public class PersonalProfileFragment extends Fragment {
    private CombinedChart mChart;
    private TextView txCurrentUserName;
    private PointsCalculator pointsCalculator;
    private Resources resources;
    private ScArcGauge gauge;
    private TextView txPointsGained, tvPointsMessage, tvCurrentDayText, tvCurrentDayDate;
    private ImageView ivTrophy;

    final int NUMBER_OF_DAYS = 7;
    final int COLOR_LINE = Color.rgb(255, 178, 102);
    final int COLOR_BAR = Color.rgb(12, 13, 73);

    private GregorianCalendar currentDate;
    private GregorianCalendar today = new GregorianCalendar();


    final PointsInfo[] pointsInfos = {
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

         mChart = (CombinedChart) view.findViewById(R.id.chart);

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
                        setGraph();
                    }
                }
                else if (SwipeType == SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT) {
                    currentDate.add(Calendar.DATE, -1);
                    setDayPoints(pointsCalculator.CalculatePoints(currentDate, currentDate));
                    setGraph();
                }

            }
        });

        // Inflate the layout for this fragment
        return view;
    }
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private long daysBetween(GregorianCalendar first, GregorianCalendar second) {
        long firstTime = first.getTime().getTime();
        long secondTime = second.getTime().getTime();
        return ( firstTime - secondTime )/ (1000*60*60*24);
    }


    private void setDayPoints(CalculatedPoints calculatedPoints) {
        gauge.setHighValue(calculatedPoints.gainedPoints, 0, calculatedPoints.possiblePoints);
        txPointsGained.setText(resources.getString(R.string.you_gained_points, calculatedPoints.gainedPoints,
                calculatedPoints.possiblePoints));
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
        tvPointsMessage.setText(resources.getString(pointsMsgId,
                calculatedPoints.possiblePoints - calculatedPoints.gainedPoints));
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
/*

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            readContactInfo();
        }
*/

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
        ImageButton btGraph = (ImageButton) view.findViewById(R.id.btGraph);

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
                    mChart.animateY(5000);
                    ((ImageButton) v).setImageDrawable(resources.getDrawable(R.drawable.ic_insert_chart_black_24dp, null));
                }
            }
        });

        ImageButton btRightNext = (ImageButton) view.findViewById(R.id.btRightNext);
        btRightNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int layoutDirection = getResources().getConfiguration().getLayoutDirection();
                if (layoutDirection == LayoutDirection.LTR) {
                    if (currentDate.compareTo(today) == -1) {
                        currentDate.add(Calendar.DATE, 1);
                        setDayPoints(pointsCalculator.CalculatePoints(currentDate, currentDate));
                        setGraph();
                    }
                }else {
                    currentDate.add(Calendar.DATE, -1);
                    setDayPoints(pointsCalculator.CalculatePoints(currentDate, currentDate));
                    setGraph();
                }
            }
        });
        ImageButton btLeftPrevious = (ImageButton) view.findViewById(R.id.btLeftPrevious);
        btLeftPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int layoutDirection = getResources().getConfiguration().getLayoutDirection();
                if (layoutDirection == LayoutDirection.LTR) {
                    currentDate.add(Calendar.DATE, -1);
                    setDayPoints(pointsCalculator.CalculatePoints(currentDate, currentDate));
                    setGraph();
                }else{
                    if (currentDate.compareTo(today) == -1) {
                        currentDate.add(Calendar.DATE, 1);
                        setDayPoints(pointsCalculator.CalculatePoints(currentDate, currentDate));
                        setGraph();
                    }
                }
            }
        });

        setDayPoints(pointsToday);
    }

    private void setGraph() {
        mChart.getDescription().setEnabled(false);
//        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE,
                CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        Legend legend = mChart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            @SuppressLint("SimpleDateFormat")
            private SimpleDateFormat mFormat = new SimpleDateFormat("E");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
//                Toast.makeText(getActivity(), "value="+value,Toast.LENGTH_LONG).show();
                int layoutDirection = getResources().getConfiguration().getLayoutDirection();
                if (layoutDirection == LayoutDirection.LTR) {
                    value = value - NUMBER_OF_DAYS + 1 + differenceTime();
                }else    value = NUMBER_OF_DAYS - value + differenceTime();

                long millis = TimeUnit.DAYS.toMillis((long) value);

                return mFormat.format((new Date(millis)));
            }
        });

        CombinedData data = new CombinedData();

        data.setData(generateLineData());
        data.setData(generateBarData());

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        mChart.setData(data);
        mChart.invalidate();
        mChart.setDoubleTapToZoomEnabled(false);
    }

    private BarData generateBarData() {
        ArrayList<BarEntry> groupGainedPoints = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_DAYS; i++) {
//            Calendar timeCalendar = new GregorianCalendar();
            Calendar timeCalendar = new GregorianCalendar();
            timeCalendar.setTime(currentDate.getTime());
            int layoutDirection = getResources().getConfiguration().getLayoutDirection();
            if (layoutDirection == LayoutDirection.LTR) {
                timeCalendar.add(Calendar.DATE, i-NUMBER_OF_DAYS+1);
            }
            else    timeCalendar.add(Calendar.DATE, -i);
            groupGainedPoints.add(new BarEntry(i,
                    pointsCalculator.CalculatePoints(timeCalendar, timeCalendar).gainedPoints));  //adding the gained points
        }

        BarDataSet set1 = new BarDataSet(groupGainedPoints, getResources().getString(R.string.pointsGained));

        set1.setColor(COLOR_BAR);
        set1.setValueTextColor(Color.rgb(12, 13, 73));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        return new BarData(set1);
    }

    private LineData generateLineData() {
        LineData d = new LineData();
        ArrayList<Entry> groupPossiblePoints = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_DAYS ; i++) {
            Calendar timeCalendar = new GregorianCalendar();
            timeCalendar.setTime(currentDate.getTime());
            int layoutDirection = getResources().getConfiguration().getLayoutDirection();
            if (layoutDirection == LayoutDirection.LTR) {
                timeCalendar.add(Calendar.DATE, i-NUMBER_OF_DAYS+1);
            }
            else                    timeCalendar.add(Calendar.DATE, -i);
            groupPossiblePoints.add(new BarEntry(i,
                    pointsCalculator.CalculatePoints(timeCalendar, timeCalendar).possiblePoints)); //adding the max points for this day
        }

        LineDataSet set = new LineDataSet(groupPossiblePoints, getResources().getString(R.string.possiblePoints));
        set.setColor(COLOR_LINE);
        set.setLineWidth(4.5f);
        set.setCircleColor(COLOR_LINE);
        set.setCircleRadius(4f);
        set.setFillColor(COLOR_LINE);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(COLOR_LINE);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    public void readContactInfo() {
        Cursor c = getActivity().getApplication().getContentResolver().query(ContactsContract.Profile.CONTENT_URI,
                null, null, null, null);
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
                Toast.makeText(getActivity(), "Until you grant the permission, we cannot display the names",
                        Toast.LENGTH_SHORT).show();
        }
    }

    private long differenceTime(){
        return (long) (currentDate.get(Calendar.DAY_OF_MONTH) - today.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onResume() {
        super.onResume();
        setGraph();
    }
}
