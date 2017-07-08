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
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.sccomponents.widgets.ScArcGauge;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

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
    private TextView txPointsGained, tvPointsMessage, tvCurrentDayText, tvCurrentDayDate, txData;
    private ImageView ivTrophy;
    private int layoutDirection;

    final int NUMBER_OF_DAYS = 7;
    final int COLOR_MAX = Color.argb(20, 1, 179, 205);
    final int COLOR_GAINED = Color.rgb(1, 179, 205);

    private GregorianCalendar currentDate, endData;
    private GregorianCalendar today = new GregorianCalendar();
    private ImageButton btNext, btPrevious;


    final PointsInfo[] pointsInfos = {
            new PointsInfo(0f, R.string.points_msg_noexercises, R.string.points_msg_noexercises_prev,0),
            new PointsInfo(1f, R.string.points_msg_well_done,R.string.points_msg_well_done_prev,R.drawable.icons8_trophy_gold_100),
            new PointsInfo(0.9f, R.string.points_msg_very_good,R.string.points_msg_very_good_prev,R.drawable.icons8_trophy_blue_100),
            new PointsInfo(0.8f, R.string.points_msg_not_bad,R.string.points_msg_not_bad_prev,R.drawable.icons8_diploma_100),
            new PointsInfo(0.7f, R.string.points_msg_need_work,R.string.points_msg_need_work_prev,R.drawable.icons8_thumb_up_100),
            new PointsInfo(0f, R.string.points_msg_need_work,R.string.points_msg_need_work_prev,0)
    };

    SwipeDetector.onSwipeEvent dragHandler = new SwipeDetector.onSwipeEvent() {
        @Override
        public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum SwipeType) {
            if (SwipeType == SwipeDetector.SwipeTypeEnum.DRAG_LEFT_TO_RIGHT && layoutDirection == LayoutDirection.RTL ||
                    SwipeType == SwipeDetector.SwipeTypeEnum.DRAG_RIGHT_TO_LEFT && layoutDirection == LayoutDirection.LTR    ) {
                if (currentDate.compareTo(today) == -1) {
                    currentDate.add(Calendar.DATE, 1);
                    setDayPoints(pointsCalculator.CalculatePoints(currentDate, currentDate));
                    setGraph();
                }
            }
            else if (SwipeType == SwipeDetector.SwipeTypeEnum.DRAG_RIGHT_TO_LEFT && layoutDirection == LayoutDirection.RTL ||
                    SwipeType == SwipeDetector.SwipeTypeEnum.DRAG_LEFT_TO_RIGHT && layoutDirection == LayoutDirection.LTR) {
                currentDate.add(Calendar.DATE, -1);
                setDayPoints(pointsCalculator.CalculatePoints(currentDate, currentDate));
                setGraph();
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_profile, container, false);

        mChart = (CombinedChart) view.findViewById(R.id.chart);

        SwipeDetector swipeDetector = new SwipeDetector(view);

        resources = getResources();
        this.layoutDirection = resources.getConfiguration().getLayoutDirection();

        setupInfoFromDB(view);

        swipeDetector.setOnSwipeListener(dragHandler);
        SwipeDetector graphSwipeDetector = new SwipeDetector(view.findViewById(R.id.chart));
        graphSwipeDetector.setOnSwipeListener(dragHandler);

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

        btNext.setVisibility(today? View.INVISIBLE : View.VISIBLE);
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
        txData = (TextView) view.findViewById(R.id.txData);
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
        view.post(new Runnable() {
            @Override
            public void run() {
                int gHeight = (int) (gauge.getHeight() * 1.123);
                int gWidth = gauge.getWidth();
                ViewGroup.LayoutParams layoutParams = gauge.getLayoutParams();
                int squareSize = 0;
                if (gHeight > gWidth) {
                    squareSize = gWidth;
                }
                else
                    squareSize = gHeight;
                layoutParams.height = (int)(squareSize/1.123);
                layoutParams.width = squareSize;
                gauge.setLayoutParams(layoutParams);
                gauge.invalidate();
            }
        });

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
                    mChart.animateY(5000);

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

        btNext = (ImageButton) view.findViewById(R.id.btNext);
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (currentDate.compareTo(today) == -1) {
                        currentDate.add(Calendar.DATE, 1);
                        setDayPoints(pointsCalculator.CalculatePoints(currentDate, currentDate));
                        setGraph();
                    }
            }
        });
        btPrevious = (ImageButton) view.findViewById(R.id.btPrevious);
        btPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    currentDate.add(Calendar.DATE, -1);
                    setDayPoints(pointsCalculator.CalculatePoints(currentDate, currentDate));
                    setGraph();
            }
        });

        setDayPoints(pointsToday);
    }

    private void setGraph() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mFormat = new SimpleDateFormat("d MMM");
        endData = new GregorianCalendar();
        endData.setTime(currentDate.getTime());
        endData.add(Calendar.DATE, -NUMBER_OF_DAYS+1);

        txData.setText(mFormat.format(endData.getTimeInMillis()) + " - " + mFormat.format(currentDate.getTimeInMillis()));

        mChart.getDescription().setEnabled(false);
//        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR/*, CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE,
                CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER*/
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
//        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            @SuppressLint("SimpleDateFormat")
            private SimpleDateFormat mFormat = new SimpleDateFormat("E");

            @Override
            public String getFormattedValue(float index, AxisBase axis) {
                Calendar timeCalendar = new GregorianCalendar();
                timeCalendar.setTime(currentDate.getTime());
                int layoutDirection = getResources().getConfiguration().getLayoutDirection();
                if (layoutDirection == LayoutDirection.LTR) {
                    timeCalendar.add(Calendar.DATE, (int) (index-NUMBER_OF_DAYS+1));
                }
                else    timeCalendar.add(Calendar.DATE, (int) -index);

                return mFormat.format(timeCalendar.getTimeInMillis());
            }
        });

        CombinedData data = new CombinedData();
        data.setData(generateBarDataCombined());

        xAxis.setAxisMaximum(data.getXMax() + 0.5f);
        xAxis.setAxisMinimum(data.getXMin() - 0.50f);

        mChart.setData(data);
        mChart.setHighlightPerDragEnabled(false);
        mChart.setHighlightPerTapEnabled(false);
        mChart.invalidate();
        mChart.setDoubleTapToZoomEnabled(false);
    }

    private BarData generateBarDataCombined() {
        ArrayList<BarEntry> groupPossiblePoints = new ArrayList<>();

        for (int index = 0; index < NUMBER_OF_DAYS; index++) {
            Calendar timeCalendar = new GregorianCalendar();
            timeCalendar.setTime(currentDate.getTime());
            int layoutDirection = getResources().getConfiguration().getLayoutDirection();
            if (layoutDirection == LayoutDirection.LTR) {
                timeCalendar.add(Calendar.DATE, index-NUMBER_OF_DAYS+1);
            }
            else    timeCalendar.add(Calendar.DATE, -index);
            CalculatedPoints calculatedPoints = pointsCalculator.CalculatePoints(timeCalendar, timeCalendar);

            groupPossiblePoints.add(new BarEntry(index, new float[]{calculatedPoints.gainedPoints,
                    calculatedPoints.possiblePoints - calculatedPoints.gainedPoints}));
        }

        BarDataSet dataSet = new BarDataSet(groupPossiblePoints, "");
        dataSet.setStackLabels(new String[]{getResources().getString(R.string.pointsGained), getResources().getString(R.string.possiblePoints)});
        dataSet.setColors(COLOR_GAINED, COLOR_MAX);
        dataSet.setValueTextColor(COLOR_MAX);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(false);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        return new BarData(dataSet);
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

    @Override
    public void onResume() {
        super.onResume();
        setGraph();
    }
}
