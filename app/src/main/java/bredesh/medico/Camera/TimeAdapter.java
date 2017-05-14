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

            setItemsView(convertView, viewHolder, item);

            convertView.setTag(viewHolder);
        } else convertView.getTag();

        return convertView;
    }


    private void setItemsView(View convertView, ViewHolder viewHolder, String item) {

        viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        viewHolder.alarm = (ImageButton) convertView.findViewById(R.id.timePick);
        viewHolder.remove = (ImageButton) convertView.findViewById(R.id.remove);

        viewHolder.alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String strSelectedHour = "" + selectedHour, strSelectedMinute;
                        if(selectedMinute < 10)
                            strSelectedMinute = "0" + selectedMinute;
                        else
                            strSelectedMinute = "" + selectedMinute;
                    //    t.setText( strSelectedHour + " : " + strSelectedMinute);
                    }
                }, hour, minute, DateFormat.is24HourFormat(context));//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        viewHolder.tvTime.setText(item);

    }

    private class ViewHolder{
        TextView tvTime;
        ImageButton remove, alarm;
    }

}
