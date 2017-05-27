package bredesh.medico.Camera;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.List;

import bredesh.medico.R;

import static android.R.id.list;

/**
 * Created by Omri on 26/05/2017.
 */

public class TimeAdapterRecycler extends RecyclerView.Adapter<TimeAdapterRecycler.CustomViewHolder> {

    private List<String> chainTime;
    private Context context;

    public TimeAdapterRecycler(Context context, List<String> chainTime)
    {
        this.context = context;
        this.chainTime = chainTime;
    }

    @Override
    public TimeAdapterRecycler.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_time_item, viewGroup, false);
        return new TimeAdapterRecycler.CustomViewHolder(view);
    }

    private String getZeroTrailedTime(int time)
    {
        String result = "0" + time;
        return result.substring(result.length()-2);
    }

    @Override
    public void onBindViewHolder(final TimeAdapterRecycler.CustomViewHolder holder, final int position) {
        final String timeItem = chainTime.get(position);

        holder.tvTime.setText(timeItem);
        final String[] hourAndMinutes = timeItem.split(" : ");

        holder.timePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour = Integer.parseInt(hourAndMinutes[0]);
                int minute = Integer.parseInt(hourAndMinutes[1]);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String strSelectedHour = getZeroTrailedTime(selectedHour);
                        String strSelectedMinute = getZeroTrailedTime(selectedMinute);

                        String finalTime = strSelectedHour + " : " + strSelectedMinute;
                        holder.tvTime.setText(finalTime);
                        hourAndMinutes[0] = strSelectedHour;
                        hourAndMinutes[1] = strSelectedMinute;
                        chainTime.set(position, finalTime);
                    }
                }, hour, minute, DateFormat.is24HourFormat(context));//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chainTime.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, chainTime.size());
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != chainTime ? chainTime.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTime;
        private ImageButton timePick, remove;

        private CustomViewHolder(View convertView) {
            super(convertView);
            this.tvTime = (TextView) convertView.findViewById(R.id.tvTimer);
            this.timePick = (ImageButton) convertView.findViewById(R.id.timePicker);
            this.remove = (ImageButton) convertView.findViewById(R.id.remover);
        }
    }


}

