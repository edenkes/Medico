package bredesh.medico.Camera;

import android.app.TimePickerDialog;
import android.support.annotation.LayoutRes;
import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;

import bredesh.medico.R;

/**
 * Created by Omri on 09/05/2017.
 */

public class TimeAdapter extends BaseAdapter {

    private Context context;
    private int id;
    private ArrayList<String> arrayList;

    public TimeAdapter(Context context, @LayoutRes int resource, ArrayList<String> objects) {
        super();
        this.context = context;
        this.id = resource;
        this.arrayList = objects;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public String getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrayList.indexOf(getItem(position));
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.time_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            final String item = arrayList.get(position);
            convertView.setTag(viewHolder);
            setItemsView(convertView, viewHolder, item,position);
        } else
        {
            final String item = arrayList.get(position);
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            setItemsView(convertView, viewHolder, item, position);
        }

        return convertView;
    }

    private String getZeroTrailedTime(int time)
    {
        String result = "0" + time;
        return result.substring(result.length()-2);
    }

    private void setItemsView(View convertView, final ViewHolder viewHolder, final String item, final int position) {

        viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        viewHolder.alarm = (ImageButton) convertView.findViewById(R.id.timePick);
        viewHolder.remove = (ImageButton) convertView.findViewById(R.id.remove);

        viewHolder.tvTime.setText(item);
        final String[] hourAndMinutes = item.split(" : ");

        viewHolder.alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int hour = Integer.parseInt(hourAndMinutes[0]);
                int minute = Integer.parseInt(hourAndMinutes[1]);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String strSelectedHour = getZeroTrailedTime(selectedHour);
                        String strSelectedMinute = getZeroTrailedTime(selectedMinute);

                        String finalTime = strSelectedHour + " : " + strSelectedMinute;
                         viewHolder.tvTime.setText(finalTime);
                        hourAndMinutes[0] = strSelectedHour;
                        hourAndMinutes[1] = strSelectedMinute;
                        arrayList.set(position, finalTime);
                    }
                }, hour, minute, DateFormat.is24HourFormat(context));//Yes 24 hour time
                mTimePicker.setTitle("Select Time");

                mTimePicker.show();

            }
        });

        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                arrayList.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    private class ViewHolder{
        TextView tvTime;
        ImageButton remove, alarm;
    }

}
