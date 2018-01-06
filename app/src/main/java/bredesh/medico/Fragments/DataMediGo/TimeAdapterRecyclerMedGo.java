package bredesh.medico.Fragments.DataMediGo;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.List;

import bredesh.medico.R;

/**
 * Created by edenk on 12/10/2017.
 */

class TimeAdapterRecyclerMedGo  extends RecyclerView.Adapter<TimeAdapterRecyclerMedGo.CustomViewHolder> {

    private List<String> chainTime;
    private Context context;
    private Button[] buttons;
    private IRemoveLastAlert caller;

    TimeAdapterRecyclerMedGo(Context context, List<String> chainTime, Button[] buttons, IRemoveLastAlert caller)
    {
        this.context = context;
        this.chainTime = chainTime;
        this.buttons = buttons;
        this.caller = caller;
    }

    private void showButtons(boolean show)
    {
        int state = show ? View.VISIBLE : View.INVISIBLE;
        for (Button button : buttons) {
            button.setVisibility(state);
        }
    }


    @Override
    public TimeAdapterRecyclerMedGo.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_time_item, viewGroup, false);
        return new TimeAdapterRecyclerMedGo.CustomViewHolder(view);
    }

    private String getZeroTrailedTime(int time)
    {
        String result = "0" + time;
        return result.substring(result.length()-2);
    }

    private void hideKeyboard(View view)
    {
        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBindViewHolder(final TimeAdapterRecyclerMedGo.CustomViewHolder holder, final int position)
    {
        final String timeItem = chainTime.get(position);

        final String[] hourAndMinutes = timeItem.split(" : ");

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                int hour = Integer.parseInt(hourAndMinutes[0]);
                int minute = Integer.parseInt(hourAndMinutes[1]);
                TimePickerDialog mTimePicker;

                mTimePicker = new TimePickerDialog(context, R.style.Theme_Dialog, new TimePickerDialog.OnTimeSetListener() {
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
                mTimePicker.setTitle(context.getResources().getString(R.string.time_picker_select_time));
                mTimePicker.show();

                mTimePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        showButtons(true);
                    }
                });
                showButtons(false);

            }
        };

        holder.timePick.setOnClickListener(listener);
        holder.tvTime.setText(timeItem);
        holder.tvTime.setOnClickListener(listener);

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                if (chainTime.size() == 1)
                    caller.OnRemoveLastAlert();
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
            this.tvTime = convertView.findViewById(R.id.tvTimer);
            this.timePick = convertView.findViewById(R.id.timePicker);
            this.remove = convertView.findViewById(R.id.remover);
        }
    }
}