package bredesh.medico.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import bredesh.medico.Camera.RemindersData;
import bredesh.medico.DAL.MedicoDB;
import bredesh.medico.Fragments.ItemMediGo.RemindersIt;
import bredesh.medico.Fragments.PictureItem.RemindersItem;
import bredesh.medico.Fragments.PictureItem.RemindersRecyclerAdapter;
import bredesh.medico.Fragments.RecyclerAdapterMediGo.RemindersRA;
import bredesh.medico.R;

public class PersonalRemindersFragment extends Fragment {
    private Context context;
    List<RemindersIt> arrayList;
    RecyclerView lvHome;
    RemindersRA adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)  {
        View view = inflater.inflate(R.layout.fragment_fragment_home, container, false);
        lvHome = view.findViewById(R.id.recycler_view);
        lvHome.setLayoutManager(new LinearLayoutManager(getActivity()));
        context = getActivity().getApplicationContext();

        TextView title = view.findViewById(R.id.tvExercises);
        title.setText(getResources().getText(R.string.reminders));

        setArrayList();

        adapter = new RemindersRA(context,arrayList,getActivity());
        lvHome.setAdapter(adapter);

        FloatingActionButton btAddAlert = view.findViewById(R.id.addAlert);
        btAddAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RemindersData.class));
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        bredesh.medico.Fragments.FragmentHome fragment = new bredesh.medico.Fragments.FragmentHome();
        ft.replace(R.id.fragment_place, fragment);
        ft.commit();
    }

    private void setArrayList() {
        MedicoDB db = new MedicoDB(getActivity().getApplicationContext());
        Cursor c = db.getAllAlertsByKind(MedicoDB.KIND.Reminders);

        arrayList = new ArrayList<>();
        int index;

        for (c.moveToFirst(), index = 0; !c.isAfterLast(); c.moveToNext(), index++) {
            int id = c.getInt(c.getColumnIndex(MedicoDB.KEY_ID));
            String time = c.getString(c.getColumnIndex(MedicoDB.KEY_TIME));
            String[] times = time.split(Pattern.quote(getResources().getString(R.string.times_splitter)));
            boolean detailedTimes = true;
            String allTimes = time;
            if (times.length > 3) {
                time = String.format(getString(R.string.several_times), times.length);
                detailedTimes = false;
            }

            String name = c.getString(c.getColumnIndex(MedicoDB.KEY_NAME));
            String uriVideo = c.getString(c.getColumnIndex(MedicoDB.URIVIDEO));
            String alertSoundUri = c.getString(c.getColumnIndex(MedicoDB.KEY_ALERT_SOUND_URI));

            int[] days = new int[7];
            days[0] = c.getInt(c.getColumnIndex(MedicoDB.SUNDAY));
            days[1] = c.getInt(c.getColumnIndex(MedicoDB.MONDAY));
            days[2] = c.getInt(c.getColumnIndex(MedicoDB.TUESDAY));
            days[3] = c.getInt(c.getColumnIndex(MedicoDB.WEDNESDAY));
            days[4] = c.getInt(c.getColumnIndex(MedicoDB.THURSDAY));
            days[5] = c.getInt(c.getColumnIndex(MedicoDB.FRIDAY));
            days[6] = c.getInt(c.getColumnIndex(MedicoDB.SATURDAY));

            Cursor cReminders = db.getRemindersByID(id);
            String notes =   cReminders.getString(cReminders.getColumnIndex(MedicoDB.KEY_NOTES));

            arrayList.add(index, new RemindersIt(id, time, name, uriVideo, days, detailedTimes, allTimes,
                    MedicoDB.KIND.Reminders, notes, alertSoundUri));
        }
        c.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        setArrayList();
        adapter = new RemindersRA(context,arrayList,getActivity());
        lvHome.setAdapter(adapter);
    }
}
